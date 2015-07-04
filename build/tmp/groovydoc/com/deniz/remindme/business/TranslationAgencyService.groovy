package com.deniz.remindme.business

import com.deniz.ratpack.http.HttpClient
import com.deniz.ratpack.http.HttpResponse
import com.deniz.remindme.business.domain.TranslatableValue
import com.deniz.remindme.persistence.TranslationRequestRepository
import com.deniz.remindme.persistence.entity.TranslationRequest
import com.deniz.remindme.persistence.entity.enums.TranslationStatusEnum
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * author: TRYavasU
 * date: 05/04/2015
 */
@Slf4j
@Service
@org.springframework.context.annotation.Lazy
class TranslationAgencyService {

    @Autowired
    @Qualifier("httpClient")
    HttpClient httpClient

    rx.Observable<HttpResponse> send(TranslatableValue translatableValue, String trackId) {
        def errors = []
        rx.Observable.just("starting").flatMap({
            //TODO: Add an agency url here according to translatable value
            httpClient.doGet("Translation Agency URL")
        }).filter({ HttpResponse response ->
            response.isSuccessful("Sending request to translation", errors)
        }).map({
            log.error "Errors : ${errors}"
            "done"
        })
    }
}
