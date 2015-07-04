package com.deniz.translationrequest

import com.deniz.ratpack.handlers.customer.TranslationRequestParser
import com.deniz.remindme.business.domain.TranslatableValue
import org.junit.Test
import org.springframework.core.io.ClassPathResource

/**
 * author: TRYavasU
 * date: 04/04/2015
 */
class TranslationRequestParserTest {

    @Test
    void shouldParse() {
        String recipeToParse = new ClassPathResource("translationrequest/request1.json").file.getText("utf-8")
        TranslatableValue translatableValue = TranslationRequestParser.parse(recipeToParse)
        assert translatableValue.term == "merhaba"
        assert translatableValue.source == "tr_TR"
        assert translatableValue.target == "en_GB"
    }

    @Test(expected = MissingPropertyException.class)
    void shouldNotParse() {
        String recipeToParse = new ClassPathResource("translationrequest/request_not_a_json.json").file.getText("utf-8")
        TranslatableValue translatableValue = TranslationRequestParser.parse(recipeToParse)
    }

    @Test(expected = MissingPropertyException.class)
    void shouldNotParse2() {
        String recipeToParse = new ClassPathResource("translationrequest/request_missing_term.json").file.getText("utf-8")
        TranslatableValue translatableValue = TranslationRequestParser.parse(recipeToParse)
    }

    @Test(expected = MissingPropertyException.class)
    void shouldNotParse3() {
        String recipeToParse = new ClassPathResource("translationrequest/request_missing_source.json").file.getText("utf-8")
        TranslatableValue translatableValue = TranslationRequestParser.parse(recipeToParse)
    }

    @Test(expected = MissingPropertyException.class)
    void shouldNotParse4() {
        String recipeToParse = new ClassPathResource("translationrequest/request_missing_target.json").file.getText("utf-8")
        TranslatableValue translatableValue = TranslationRequestParser.parse(recipeToParse)
    }

}
