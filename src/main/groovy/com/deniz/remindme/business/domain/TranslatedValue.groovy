package com.deniz.remindme.business.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * author: TRYavasU
 * date: 04/04/2015
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(includes = ['term', 'source', 'target', 'value'])
@ToString(includeNames = true, includePackage = false, ignoreNulls = true)
class TranslatedValue {
    def term
    def source
    def target
    def value

    @JsonIgnore
    boolean isComplete() {
        term && source && target && value
    }

}
