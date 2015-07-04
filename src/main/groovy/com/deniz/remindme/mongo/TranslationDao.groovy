package com.deniz.remindme.mongo

import com.deniz.remindme.business.domain.TranslatableValue
import com.deniz.remindme.business.domain.TranslatedValue
import com.gmongo.GMongoClient
import com.mongodb.DB
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * author: TRYavasU
 * date: 04/04/2015
 */
@Component
class TranslationDao {

    @Autowired
    @Qualifier("mongoClient")
    GMongoClient gMongoClient

    @Value('${mongo.db.name}')
    def dbName

    void insertValue(TranslatedValue translatedValue) {
        def persistedTranslation = mongoDb()["translation"].findOne([source: translatedValue.source, target: translatedValue.target, term: translatedValue.term, value: translatedValue.value])

        if (!persistedTranslation) {
            mongoDb()["translation"].save([source: translatedValue.source, target: translatedValue.target, term: translatedValue.term, value: translatedValue.value])
        }
    }

    private DB mongoDb() {
        gMongoClient.getDB(dbName)
    }

    List<TranslatedValue> getTranslation(TranslatableValue translatableValue) {
        def persistedTranslation = mongoDb()["translation"].find(source: translatableValue.source, target: translatableValue.target, term: translatableValue.term)
        persistedTranslation.collect({
            new TranslatedValue(term: translatableValue.term, source: translatableValue.source, target: translatableValue.target, value: it.value)
        })
    }
}
