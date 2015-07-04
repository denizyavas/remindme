package com.deniz.remindme.persistence

import com.deniz.remindme.persistence.entity.TranslationRequest
import org.springframework.data.jpa.repository.JpaRepository

/**
 * author: TRYavasU
 * date: 07/04/2015
 */
interface TranslationRequestRepository extends JpaRepository<TranslationRequest, Long> {

    TranslationRequest findByTrackId(String trackId)

}
