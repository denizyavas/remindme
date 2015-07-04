package com.deniz.ratpack.handlers.customer

import com.deniz.ratpack.handlers.customer.domain.TranslationRequestResult
import com.deniz.ratpack.process.ProcessIdImpl
import com.deniz.remindme.business.TranslationAgencyService
import com.deniz.remindme.business.TranslationService
import com.deniz.remindme.business.domain.TranslatableValue
import com.deniz.remindme.business.domain.TranslatedValue
import com.deniz.remindme.persistence.TranslationRequestRepository
import com.deniz.remindme.persistence.entity.TranslationRequest
import com.deniz.remindme.persistence.entity.enums.TranslationStatusEnum
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ratpack.exec.ExecControl
import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler

import static ratpack.jackson.Jackson.json
import static ratpack.rx.RxRatpack.observe

/**
 * author: TRYavasU
 * date: 04/04/2015
 */
@Slf4j(value = "log", category = "TranslationRequestHandler")
@Slf4j(value = "activity", category = "activity")
@Component
@org.springframework.context.annotation.Lazy
class TranslationRequestHandler extends GroovyHandler {

    @Value('${translation.check.url}')
    String checkUrl

    @Autowired
    @org.springframework.context.annotation.Lazy
    ExecControl execControl

    @Autowired
    TranslationService translationService

    @Autowired
    TranslationAgencyService translationAgencyService

    @Autowired
    TranslationRequestRepository translationRequestRepository

    @Override
    protected void handle(GroovyContext context) {
        TranslationRequestResult translationResult = new TranslationRequestResult()
        List<TranslatedValue> translatedValues = []
        context.with {

            def params = [:]
            params.processId = request.queryParams.processId ? new ProcessIdImpl(request.queryParams.processId) : new ProcessIdImpl()

            activity.info "Request taken with processId: ${params.processId.toString()}."

            params.recipe = request.body.bytes

            if (!params.recipe) {
                activity.warn "Request to translate with processId: ${params.processId.toString()} rejected."
                response.status(400)
                translationResult.status = 400
                render json(translationResult)
            } else {
                try {
                    TranslatableValue translatableValue = TranslationRequestParser.parse(new String(params.recipe as byte[]))

                    translationResult.with {
                        term = translatableValue.term
                        source = translatableValue.source
                        target = translatableValue.target
                    }
                    rx.Observable.from(translatableValue).flatMap({
                        observe(execControl.blocking({
                            translatedValues = translationService.checkTranslation(translatableValue)
                        }
                        ))
                    }).flatMap({
                        observe(execControl.blocking({
                            if (!translatedValues) {
                                translationAgencyService.send(translatableValue, params.processId.processId)
                            }
                        }))
                    }).flatMap({
                        observe(execControl.blocking({
                            translationRequestRepository.save(new TranslationRequest(trackId: params.processId.processId, status: TranslationStatusEnum.ACQUIRED))
                        }))
                    }).finallyDo({
                        translationResult.translatedValues = translatedValues
                        if (translationResult?.translatedValues) {
                            response.status 200
                            translationResult.status = 200
                            render json(translationResult)
                        } else {
                            response.status 201
                            translationResult.checkUrl = formCheckUrl(params.processId)
                            translationResult.status = 201
                            render json(translationResult)
                        }
                    }).subscribe({
                        activity.debug "Translation request handler flow emitted: {}", it
                    }, { e ->
                        translationResult.errors << e
                        activity.warn "Error in translation request handler", e
                    }
                    )
                } catch (MissingPropertyException e) {
                    response.status 400
                    translationResult.status = 400
                    translationResult.errors << e
                    render json(translationResult)
                }
            }
        }
    }

    def formCheckUrl(ProcessIdImpl trackingId) {
        checkUrl.replace(":trackId", trackingId.processId)
    }
}