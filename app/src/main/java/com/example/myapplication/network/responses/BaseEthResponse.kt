package com.example.myapplication.network.responses

data class  BaseEthResponse<T> (
    val status: Int,
    val message: String,
    val result: T,
)