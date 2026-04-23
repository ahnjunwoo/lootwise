package com.junwoo.lootwise.deal.service

object ReviewScoreDescriptionTranslator {
    private val koreanDescriptions = mapOf(
        "Overwhelmingly Positive" to "압도적으로 긍정적",
        "Very Positive" to "매우 긍정적",
        "Positive" to "긍정적",
        "Mostly Positive" to "대체로 긍정적",
        "Mixed" to "복합적",
        "Mostly Negative" to "대체로 부정적",
        "Negative" to "부정적",
        "Very Negative" to "매우 부정적",
        "Overwhelmingly Negative" to "압도적으로 부정적",
    )

    fun toKorean(description: String?): String? =
        description?.let { koreanDescriptions[it] }
}
