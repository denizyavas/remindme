package com.deniz.translationrequest.handler

import com.deniz.ratpack.handlers.agency.NewValueHandler
import com.deniz.remindme.business.TranslationService
import com.deniz.remindme.business.domain.TranslatedValue
import groovy.mock.interceptor.StubFor
import org.junit.Before
import org.junit.Test
import org.springframework.core.io.ClassPathResource
import ratpack.groovy.test.GroovyUnitTest
import ratpack.jackson.internal.DefaultJsonRender

/**
 * author: TRYavasU
 * date: 05/04/2015
 */
class NewValueHandlerTest {

    StubFor mockTranslationService

    @Before
    void before() {
        mockTranslationService = new StubFor(TranslationService)
    }


    @Test
    void "success"() {
        def request = new ClassPathResource("newvalue/request1.json").file.getText("utf-8")
        mockTranslationService.demand.with {
            addTranslation(1) { TranslatedValue tv ->
                assert tv.term == "merhaba"
                assert tv.source == "tr_TR"
                assert tv.target == "en_GB"
                assert tv.value == "hello"
                rx.Observable.just(null)
            }
        }

        GroovyUnitTest.handle(new NewValueHandler(
                translationService: mockTranslationService.proxyInstance()
        ), {
            body(request, "application/json")
        }).with {
            assert status.code == 200
            def ren = rendered(DefaultJsonRender).object
            assert ren.status == 200
            assert ren.errors == []
            assert ren.term == "merhaba"
            assert ren.source == "tr_TR"
            assert ren.target == "en_GB"
            assert ren.value == "hello"
        }
    }

    @Test
    void "empty body"() {
        GroovyUnitTest.handle(new NewValueHandler(
                translationService: mockTranslationService.proxyInstance()
        ), {
        }).with {
            assert status.code == 400
            def ren = rendered(DefaultJsonRender).object
            assert ren.status == 400
            assert ren.errors
            assert !ren.term
            assert !ren.source
            assert !ren.target
            assert !ren.term
        }
    }

    @Test
    void "fail_not_a_json"() {
        def request = new ClassPathResource("newvalue/request_not_a_json.json").file.getText("utf-8")

        GroovyUnitTest.handle(new NewValueHandler(
                translationService: mockTranslationService.proxyInstance()
        ), {
            body(request, "application/json")
        }).with {
            assert status.code == 400
            def ren = rendered(DefaultJsonRender).object
            assert ren.status == 400
            assert ren.errors
            assert !ren.term
            assert !ren.source
            assert !ren.target
            assert !ren.term
        }
    }

    @Test
    void "fail_missing_source"() {
        def request = new ClassPathResource("newvalue/request_missing_source.json").file.getText("utf-8")

        GroovyUnitTest.handle(new NewValueHandler(
                translationService: mockTranslationService.proxyInstance()
        ), {
            body(request, "application/json")
        }).with {
            assert status.code == 400
            def ren = rendered(DefaultJsonRender).object
            assert ren.status == 400
            assert ren.errors
            assert !ren.term
            assert !ren.source
            assert !ren.target
            assert !ren.term
        }
    }

    @Test
    void "fail_missing_target"() {
        def request = new ClassPathResource("newvalue/request_missing_target.json").file.getText("utf-8")

        GroovyUnitTest.handle(new NewValueHandler(
                translationService: mockTranslationService.proxyInstance()
        ), {
            body(request, "application/json")
        }).with {
            assert status.code == 400
            def ren = rendered(DefaultJsonRender).object
            assert ren.status == 400
            assert ren.errors
            assert !ren.term
            assert !ren.source
            assert !ren.target
            assert !ren.term
        }
    }

    @Test
    void "fail_missing_value"() {
        def request = new ClassPathResource("newvalue/request_missing_term.json").file.getText("utf-8")

        GroovyUnitTest.handle(new NewValueHandler(
                translationService: mockTranslationService.proxyInstance()
        ), {
            body(request, "application/json")
        }).with {
            assert status.code == 400
            def ren = rendered(DefaultJsonRender).object
            assert ren.status == 400
            assert ren.errors
            assert !ren.term
            assert !ren.source
            assert !ren.target
            assert !ren.term
        }
    }

    @Test
    void "fail_missing_translated_value"() {
        def request = new ClassPathResource("newvalue/request_missing_value.json").file.getText("utf-8")

        GroovyUnitTest.handle(new NewValueHandler(
                translationService: mockTranslationService.proxyInstance()
        ), {
            body(request, "application/json")
        }).with {
            assert status.code == 400
            def ren = rendered(DefaultJsonRender).object
            assert ren.status == 400
            assert ren.errors
            assert !ren.term
            assert !ren.source
            assert !ren.target
            assert !ren.term
        }
    }

}

