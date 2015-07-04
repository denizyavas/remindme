package com.deniz.ratpack.handlers.customer

import com.deniz.ratpack.handlers.customer.domain.TrackTranslationResult
import com.deniz.ratpack.handlers.customer.domain.TranslationRequestResult
import com.deniz.ratpack.process.ProcessIdImpl
import com.deniz.remindme.business.domain.TranslatedValue
import com.deniz.remindme.persistence.TranslationRequestRepository
import com.deniz.remindme.persistence.entity.TranslationRequest
import com.deniz.remindme.persistence.entity.enums.TranslationStatusEnum
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ratpack.exec.ExecControl
import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler

import static ratpack.jackson.Jackson.json
import static ratpack.rx.RxRatpack.observe

/**
 * author: TRYavasU
 * date: 09/04/2015
 */
@Slf4j(value = "log", category = "TrackTranslationHandler")
@Slf4j(value = "activity", category = "activity")
@Component
@org.springframework.context.annotation.Lazy
class TrackTranslationHandler extends GroovyHandler {

    @Autowired
    TranslationRequestRepository translationRequestRepository

    @Autowired
    @org.springframework.context.annotation.Lazy
    ExecControl execControl

    @Override
    protected void handle(GroovyContext context) {
        TrackTranslationResult trackTranslationResult = new TrackTranslationResult()
        context.with {

            def params = [:]
            params.trackId = context.pathTokens.trackId

            if (!params.trackId) {
                activity.warn "Request to track translation with trackId: ${params.trackId} rejected."
                response.status(400)
                trackTranslationResult.status = 400
            } else {
                rx.Observable.just("starting").flatMap({
                    observe(execControl.blocking({
                        translationRequestRepository.findByTrackId(params.trackId)
                    }))
                }).finallyDo({
                    if (trackTranslationResult.trackId) {
                        response.status 200
                        trackTranslationResult.status = 200
                        render json(trackTranslationResult)
                    } else {
                        response.status 404
                        trackTranslationResult.status = 404
                        render json(trackTranslationResult)
                    }
                }).subscribe({
                    trackTranslationResult.translationStatus = it?.status
                    trackTranslationResult.trackId = it?.trackId
                    activity.debug "Track Translation request handler flow emitted: {}", it
                }, { e ->
                    trackTranslationResult.errors << e
                    activity.warn "Error in track translation request handler", e
                })
            }
        }
    }
}