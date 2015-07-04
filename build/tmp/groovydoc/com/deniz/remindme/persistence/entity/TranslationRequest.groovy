package com.deniz.remindme.persistence.entity

import com.deniz.remindme.persistence.entity.enums.TranslationStatusEnum
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * author: TRYavasU
 * date: 06/04/2015
 */
@EqualsAndHashCode(includes = ['trackId'])
@ToString(includeNames = true, includePackage = false, ignoreNulls = true)
@Entity
class TranslationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id

    String trackId

    TranslationStatusEnum status

}
