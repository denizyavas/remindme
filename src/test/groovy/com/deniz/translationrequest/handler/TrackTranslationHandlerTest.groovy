package com.deniz.translationrequest.handler

import com.deniz.ratpack.handlers.customer.TrackTranslationHandler
import com.deniz.ratpack.handlers.customer.TranslationRequestHandler
import com.deniz.remindme.business.TranslationAgencyService
import com.deniz.remindme.business.TranslationService
import com.deniz.remindme.business.domain.TranslatableValue
import com.deniz.remindme.business.domain.TranslatedValue
import com.deniz.remindme.persistence.TranslationRequestRepository
import groovy.mock.interceptor.StubFor
import groovy.util.logging.Slf4j
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import org.springframework.core.io.ClassPathResource
import ratpack.exec.ExecController
import ratpack.groovy.test.GroovyUnitTest
import ratpack.jackson.internal.DefaultJsonRender
import ratpack.launch.LaunchConfigBuilder

/**
 *
 */

@Slf4j
class TrackTranslationHandlerTest {

    StubFor mockTranslationRequestRepository
    static ExecController execController

    @Before
    void before() {
        mockTranslationRequestRepository = new StubFor(TranslationRequestRepository)
        execController = LaunchConfigBuilder.noBaseDir().build().execController
    }

    @AfterClass
    static void afterClass() {
        if (execController) execController.close()
    }

    @Test
    void "empty body"() {
        GroovyUnitTest.handle(new TrackTranslationHandler(
                execControl: execController.control,
                translationRequestRepository: mockTranslationRequestRepository.proxyInstance()
        ), {
        }).with {
            assert status.code == 400
            def ren = rendered(DefaultJsonRender).object
            assert ren.status == 400
            assert ren.errors
            assert ren.translatedValues == []
            assert !ren.term
            assert !ren.source
            assert !ren.target
        }
    }

    @Test
    void "success"() {
        def request = new ClassPathResource("translationrequest/request1.json").file.getText("utf-8")
        mockTranslationService.demand.with {
            checkTranslation(1) { TranslatableValue tv ->
                assert tv.term == "merhaba"
                assert tv.source == "tr_TR"
                assert tv.target == "en_GB"
                rx.Observable.just([new TranslatedValue(term: "merhaba", source: "tr_TR", target: "en_GB", value: "hello")])
            }
        }

        GroovyUnitTest.handle(new TrackTranslationHandler(
                execControl: execController.control,
                translationRequestRepository: mockTranslationRequestRepository.proxyInstance()
        ), {
            body(request, "application/json")
        }).with {
            assert status.code == 200
            def ren = rendered(DefaultJsonRender).object
            assert ren.status == 200
            assert ren.errors == []
            assert ren.translatedValues == [new TranslatedValue(term: "merhaba", source: "tr_TR", target: "en_GB", value: "hello")]
            assert ren.term == "merhaba"
            assert ren.source == "tr_TR"
            assert ren.target == "en_GB"
        }
    }

    @Test
    void "fail_not_a_json"() {
        def request = new ClassPathResource("translationrequest/request_not_a_json.json").file.getText("utf-8")

        GroovyUnitTest.handle(new TrackTranslationHandler(
                execControl: execController.control,
                translationRequestRepository: mockTranslationRequestRepository.proxyInstance()
        ), {
            body(request, "application/json")
        }).with {
            assert status.code == 400
            def ren = rendered(DefaultJsonRender).object
            assert ren.status == 400
            assert ren.errors
            assert ren.translatedValues == []
            assert !ren.term
            assert !ren.source
            assert !ren.target
        }
    }

    @Test
    void "fail_missing_source"() {
        def request = new ClassPathResource("translationrequest/request_missing_source.json").file.getText("utf-8")

        GroovyUnitTest.handle(new TrackTranslationHandler(
                execControl: execController.control,
                translationRequestRepository: mockTranslationRequestRepository.proxyInstance()
        ), {
            body(request, "application/json")
        }).with {
            assert status.code == 400
            def ren = rendered(DefaultJsonRender).object
            assert ren.status == 400
            assert ren.errors
            assert ren.translatedValues == []
            assert !ren.term
            assert !ren.source
            assert !ren.target
        }
    }

    @Test
    void "fail_missing_target"() {
        def request = new ClassPathResource("translationrequest/request_missing_target.json").file.getText("utf-8")


        GroovyUnitTest.handle(new TrackTranslationHandler(
                execControl: execController.control,
                translationRequestRepository: mockTranslationRequestRepository.proxyInstance()
        ), {
            body(request, "application/json")
        }).with {
            assert status.code == 400
            def ren = rendered(DefaultJsonRender).object
            assert ren.status == 400
            assert ren.errors
            assert ren.translatedValues == []
            assert !ren.term
            assert !ren.source
            assert !ren.target
        }
    }

    @Test
    void "fail_missing_value"() {
        def request = new ClassPathResource("translationrequest/request_missing_term.json").file.getText("utf-8")


        GroovyUnitTest.handle(new TrackTranslationHandler(
                execControl: execController.control,
                translationRequestRepository: mockTranslationRequestRepository.proxyInstance()
        ), {
            body(request, "application/json")
        }).with {
            assert status.code == 400
            def ren = rendered(DefaultJsonRender).object
            assert ren.status == 400
            assert ren.errors
            assert ren.translatedValues == []
            assert !ren.term
            assert !ren.source
            assert !ren.target
        }
    }


    @Test
    void "success_new_value"() {
        def request = new ClassPathResource("translationrequest/new_request.json").file.getText("utf-8")
        mockTranslationService.demand.with {
            checkTranslation(1) { TranslatableValue tv ->
                assert tv.term == "hello"
                assert tv.source == "en_GB"
                assert tv.target == "tr_TR"
                rx.Observable.just([])
            }
        }
        mockTranslationAgencyService.demand.with {
            send(1) { TranslatableValue tv ->
                assert tv.term == "hello"
                assert tv.source == "en_GB"
                assert tv.target == "tr_TR"
                rx.Observable.just([status: "200"])
            }
        }


        GroovyUnitTest.handle(new TrackTranslationHandler(
                execControl: execController.control,
                translationRequestRepository: mockTranslationRequestRepository.proxyInstance()
        ), {
            body(request, "application/json")
            uri "/?processId=123"
        }).with {
            assert status.code == 201
            def ren = rendered(DefaultJsonRender).object
            assert ren.status == 201
            assert !ren.errors
            assert ren.translatedValues == []
            assert ren.term == "hello"
            assert ren.source == "en_GB"
            assert ren.target == "tr_TR"
            assert ren.checkUrl == "http://mockUrl/123"
        }
    }
}
