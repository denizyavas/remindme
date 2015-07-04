package com.deniz.translationrequest.service

import com.deniz.remindme.business.TranslationService
import com.deniz.remindme.business.domain.TranslatableValue
import com.deniz.remindme.business.domain.TranslatedValue
import com.deniz.remindme.mongo.TranslationDao
import groovy.mock.interceptor.StubFor
import org.junit.Before
import org.junit.Test

/**
 * author: TRYavasU
 * date: 05/04/2015
 */
class TranslationServiceTest {

    TranslationService translationService
    StubFor mockTranslationDao

    @Before
    void before() {
        mockTranslationDao = new StubFor(TranslationDao)
        translationService = new TranslationService()
    }

    @Test
    void "addTranslation"() {
        TranslatedValue translatedValue = new TranslatedValue(term: "merhaba", source: "tr_TR", target: "en_GB", value: "hello")

        mockTranslationDao.demand.with {
            insertValue(1) { TranslatedValue tv ->
                assert tv.term == "merhaba"
                assert tv.source == "tr_TR"
                assert tv.target == "en_GB"
                assert tv.value == "hello"
            }
        }

        translationService.translationDao = mockTranslationDao.proxyInstance()
        translationService.addTranslation(translatedValue)
    }

    @Test
    void "checkTranslation"() {
        TranslatableValue translatableValue = new TranslatableValue(term: "merhaba", source: "tr_TR", target: "en_GB")

        mockTranslationDao.demand.with {
            getTranslation(1) { TranslatableValue tv ->
                assert tv.term == "merhaba"
                assert tv.source == "tr_TR"
                assert tv.target == "en_GB"
                [new TranslatedValue(term: "merhaba", source: "tr_TR", target: "en_GB", value: "hello")]
            }
        }

        translationService.translationDao = mockTranslationDao.proxyInstance()
        translationService.checkTranslation(translatableValue)
    }
}
