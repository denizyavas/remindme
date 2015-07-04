package com.deniz.remindme.business

import com.deniz.remindme.business.domain.TranslatableValue
import com.deniz.remindme.business.domain.TranslatedValue
import com.deniz.remindme.mongo.TranslationDao
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * author: TRYavasU
 * date: 04/04/2015
 */
@Slf4j
@Service
@org.springframework.context.annotation.Lazy
class TranslationService {

    @Autowired
    TranslationDao translationDao

    List<TranslatedValue> checkTranslation(TranslatableValue translatableValue) {
        translationDao.getTranslation(translatableValue)?.collect({
            new TranslatedValue(term: it.term, source: it.source, target: it.target, value: it.value)
        })
    }

    void addTranslation(TranslatedValue translatedValue) {
        translationDao.insertValue(translatedValue)
    }

}
