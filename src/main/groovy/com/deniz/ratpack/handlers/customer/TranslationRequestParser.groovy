package com.deniz.ratpack.handlers.customer

import com.deniz.remindme.business.domain.TranslatableValue
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

/**
 * author: TRYavasU
 * date: 04/04/2015
 */
@Slf4j
class TranslationRequestParser {
    static TranslatableValue parse(String jsonText) throws MissingPropertyException {
        def parsedText = new JsonSlurper().setType(JsonParserType.LAX).parseText(jsonText)
        TranslatableValue translatableValue = [term: parsedText.term, source: parsedText.source, target: parsedText.target]
        if (!translatableValue?.isComplete()) {
            throw new MissingPropertyException("Not a valid json")
        }
        log.debug "TranslatableValue: $translatableValue is parsed respectedly from JSON:${jsonText}"
        translatableValue
    }
}
