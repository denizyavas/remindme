package com.deniz.ratpack.handlers.agency

import com.deniz.remindme.business.domain.TranslatedValue
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

/**
 * author: TRYavasU
 * date: 04/04/2015
 */
@Slf4j
class NewValueParser {

    static TranslatedValue parse(String jsonText) throws MissingPropertyException {
        def parsedText = new JsonSlurper().setType(JsonParserType.LAX).parseText(jsonText)

        TranslatedValue translatedValue = [term: parsedText.term, source: parsedText.source, target: parsedText.target, value: parsedText.value]
        if (!translatedValue?.isComplete()) {
            throw new MissingPropertyException("Not a valid json")
        }
        log.debug "TranslatedValue: $translatedValue is parsed respectedly from JSON:${jsonText}"
        translatedValue
    }
}
