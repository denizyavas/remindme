package com.deniz.ratpack.handlers.agency

import com.deniz.ratpack.handlers.agency.domain.NewValueResult
import com.deniz.ratpack.process.ProcessIdImpl
import com.deniz.remindme.business.TranslationService
import com.deniz.remindme.business.domain.TranslatedValue
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler

import static ratpack.jackson.Jackson.json

/**
 * author: TRYavasU
 * date: 04/04/2015
 */
@Slf4j(value = "log", category = "RequestHandler")
@Slf4j(value = "activity", category = "activity")
@Component
@org.springframework.context.annotation.Lazy
class NewValueHandler extends GroovyHandler {

    @Autowired
    TranslationService translationService

    @Override
    protected void handle(GroovyContext context) {
        NewValueResult newValueResult = new NewValueResult()
        context.with {
            def params = [:]
            params.processId = request.queryParams.processId ? new ProcessIdImpl(request.queryParams.processId) : new ProcessIdImpl()

            activity.info "New value taken with processId: ${params.processId.toString()}."

            params.recipe = request.body.bytes

            if (!params.recipe) {
                activity.warn "Request for new value with processId: ${params.processId.toString()} rejected."
                response.status(400)
                render json(status: 400, processId: params.processId, response: "rejected", message: "Request body is empty")
            } else {

                try {
                    TranslatedValue translatedValue = NewValueParser.parse(new String(params.recipe as byte[]))

                    newValueResult.with {
                        term = translatedValue.term
                        source = translatedValue.source
                        target = translatedValue.target
                        value = translatedValue.value
                    }

                    rx.Observable.from(translatedValue).flatMap({
                        translationService.addTranslation(it)
                        rx.Observable.just(null)
                    }).finallyDo({
                        response.status 200
                        newValueResult.status = 200
                        render json(newValueResult)
                    }).subscribe({
                        activity.debug "New value handler flow emitted: {}", it
                    }, { e ->
                        newValueResult.errors << e
                        activity.warn "Error in new value handler", e
                    }
                    )
                } catch (MissingPropertyException e) {
                    response.status 400
                    newValueResult.status = 400
                    newValueResult.errors << e
                    render json(newValueResult)
                }
            }
        }
    }

}
