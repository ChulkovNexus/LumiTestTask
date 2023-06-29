package com.example.myapplication.network.clients.services

import com.example.myapplication.network.ServiceGenerator
import com.example.myapplication.network.dsi.Resource
import com.example.myapplication.network.makeRequestAsync
import com.example.myapplication.network.responses.BaseEthResponse
import com.example.myapplication.network.responses.EtherTransactionEntity
import retrofit2.Retrofit

class EtherScanServiceApi(
    private val retrofit: Retrofit
) {

    suspend fun getTransactions(
        address: String,
    ): Resource<BaseEthResponse<List<EtherTransactionEntity>>> {
        val service = ServiceGenerator.createService(EtherScanServiceClient::class.java, retrofit)

        val request = service.getTransactions(
            module = "account",
            action = "txlist",
            address = address,
            endblock = "latest",
            page = "1",
            offset = "100",
            sort = "asc",
        )
        return request.makeRequestAsync()
    }

}