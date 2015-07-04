package com.deniz.newvalue

import com.deniz.ratpack.handlers.agency.NewValueParser
import com.deniz.remindme.business.domain.TranslatedValue
import org.junit.Test
import org.springframework.core.io.ClassPathResource

/**
 * author: TRYavasU
 * date: 04/04/2015
 */
class NewValueParserTest {

    @Test
    void shouldParse() {
        String recipeToParse = new ClassPathResource("newvalue/request1.json").file.getText("utf-8")
        TranslatedValue translatedValue = NewValueParser.parse(recipeToParse)
        assert translatedValue.term == "merhaba"
        assert translatedValue.source == "tr_TR"
        assert translatedValue.target == "en_GB"
        assert translatedValue.value == "hello"
    }

    @Test(expected = MissingPropertyException.class)
    void shouldNotParse() {
        String recipeToParse = new ClassPathResource("newvalue/request_not_a_json.json").file.getText("utf-8")
        TranslatedValue translatedValue = NewValueParser.parse(recipeToParse)
    }

    @Test(expected = MissingPropertyException.class)
    void shouldNotParse2() {
        String recipeToParse = new ClassPathResource("newvalue/request_missing_term.json").file.getText("utf-8")
        TranslatedValue translatedValue = NewValueParser.parse(recipeToParse)
    }

    @Test(expected = MissingPropertyException.class)
    void shouldNotParse3() {
        String recipeToParse = new ClassPathResource("newvalue/request_missing_source.json").file.getText("utf-8")
        TranslatedValue translatedValue = NewValueParser.parse(recipeToParse)
    }

    @Test(expected = MissingPropertyException.class)
    void shouldNotParse4() {
        String recipeToParse = new ClassPathResource("newvalue/request_missing_target.json").file.getText("utf-8")
        TranslatedValue translatedValue = NewValueParser.parse(recipeToParse)
    }


    @Test(expected = MissingPropertyException.class)
    void shouldNotParse5() {
        String recipeToParse = new ClassPathResource("newvalue/request_missing_value.json").file.getText("utf-8")
        TranslatedValue translatedValue = NewValueParser.parse(recipeToParse)
    }

}
