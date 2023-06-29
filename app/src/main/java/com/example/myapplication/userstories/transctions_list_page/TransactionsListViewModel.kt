package com.example.myapplication.userstories.transctions_list_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.network.clients.services.EtherScanServiceApi
import com.example.myapplication.network.responses.EtherTransactionEntity
import com.example.myapplication.network.responses.EtherTransactionWrapper
import com.example.myapplication.network.responses.TransactionsList
import com.example.myapplication.utils.coroutines.ICoroutineSupport
import com.example.myapplication.utils.web3.Web3Validator
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionsListViewModel @Inject constructor(
    private val api: EtherScanServiceApi,
    private val gson: Gson,
) : ViewModel(), ICoroutineSupport {
    sealed class TransactionsListState {
        object RequestProcession : TransactionsListState()
        data class ErrorState(val text: String?) : TransactionsListState()
        object UnknownErrorState : TransactionsListState()
        data class TransactionsReceived(val transactions: List<EtherTransactionWrapper>) :
            TransactionsListState()
    }

    private var _transactionsListState: MutableLiveData<TransactionsListState> =
        MutableLiveData(TransactionsListState.RequestProcession)
    val transactionsListState: LiveData<TransactionsListState> = _transactionsListState
    private lateinit var currentAddress: String

    fun setCurrentAddress(currentAddress: String) {
        this.currentAddress = currentAddress
    }

    fun loadTransactionsList() = launch {
        _transactionsListState.postValue(TransactionsListState.RequestProcession)
        val result = api.getTransactions(currentAddress)
        val errorEntity = result.errorEntity
        val data = result.data
        if (errorEntity != null) {
            _transactionsListState.postValue(TransactionsListState.ErrorState(errorEntity.errorDesc))
        } else if (data != null && data.status != 1) {
            val errorText = data.result.asString
            _transactionsListState.postValue(TransactionsListState.ErrorState(errorText))
        } else if (data != null) {
            val parsedTransactions = gson.fromJson(data.result, TransactionsList::class.java)
            val transactions = wrapTransactions(parsedTransactions)
            _transactionsListState.postValue(TransactionsListState.TransactionsReceived(transactions))
        } else {
            _transactionsListState.postValue(TransactionsListState.UnknownErrorState)
        }
    }

    private fun wrapTransactions(data: List<EtherTransactionEntity>): List<EtherTransactionWrapper> {
        return data.map {
            EtherTransactionWrapper(
                it,
                it.from != currentAddress
            )
        }
    }
}