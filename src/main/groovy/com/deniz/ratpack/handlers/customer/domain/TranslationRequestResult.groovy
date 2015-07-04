package com.deniz.ratpack.handlers.customer.domain

/**
 * author: TRYavasU
 * date: 05/04/2015
 */
class TranslationRequestResult {
    def translatedValues = []
    def errors = []
    def term
    def source
    def target
    def status
    def checkUrl
}
