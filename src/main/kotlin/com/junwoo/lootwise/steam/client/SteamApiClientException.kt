package com.junwoo.lootwise.steam.client

class SteamApiClientException(
    val statusCode: Int,
    val responseBody: String,
) : RuntimeException("Steam API request failed with status=$statusCode")
