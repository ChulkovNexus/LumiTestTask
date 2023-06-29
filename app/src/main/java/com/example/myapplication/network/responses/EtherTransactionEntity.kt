package com.example.myapplication.network.responses

import java.io.Serializable

data class EtherTransactionEntity(
    val blockNumber: Long,
    val timeStamp: Long,
    val hash: String,
    val nonce: Int,
    val blockHash: String,
    val transactionIndex: Int,
    val from: String,
    val to: String,
    val value: Long,
    val gas: Long,
    val gasPrice: Long,
    val isError: Int,
    val txreceipt_status: String,
    val input: String,
    val contractAddress: String,
    val cumulativeGasUsed: Long,
    val gasUsed: Long,
    val confirmations: Long,
    val methodId: String,
    val functionName: String,
): Serializable

class TransactionsList: ArrayList<EtherTransactionEntity>()

data class EtherTransactionWrapper(
    val transaction: EtherTransactionEntity,
    val isIncomeTransaction: Boolean,
)