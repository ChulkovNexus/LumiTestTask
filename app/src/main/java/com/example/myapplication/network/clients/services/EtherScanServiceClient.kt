package com.example.myapplication.network.clients.services

import com.example.myapplication.network.responses.BaseEthResponse
import com.example.myapplication.network.responses.EtherTransactionEntity
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface EtherScanServiceClient {

    @Headers("Content-Type: application/json")
    @GET("/api")
    fun getTransactions(
        @Query("module") module: String?,
        @Query("action") action: String?,
        @Query("address") address: String?,
        @Query("endblock") endblock: String?,
        @Query("page") page: String?,
        @Query("offset") offset: String?,
        @Query("sort") sort: String?,
    ): Deferred<Response<BaseEthResponse>>
}