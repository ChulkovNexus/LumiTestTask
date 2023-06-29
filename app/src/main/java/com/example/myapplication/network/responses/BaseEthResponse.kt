package com.example.myapplication.network.responses

import com.google.gson.JsonElement

data class  BaseEthResponse (
    val status: Int,
    val message: String,
    val result: JsonElement,
)