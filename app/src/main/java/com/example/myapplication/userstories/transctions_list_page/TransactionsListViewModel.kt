package com.example.myapplication.userstories.transctions_list_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.network.clients.services.EtherScanServiceApi
import com.example.myapplication.network.responses.EtherTransactionEntity
import com.example.myapplication.network.responses.EtherTransactionWrapper
import com.example.myapplication.utils.coroutines.ICoroutineSupport
import com.example.myapplication.utils.web3.Web3Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionsListViewModel @Inject constructor(
    private val api: EtherScanServiceApi,
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
        val data = result.data?.result
        if (errorEntity != null) {
            _transactionsListState.postValue(TransactionsListState.ErrorState(errorEntity.errorDesc))
        } else if (data != null) {
            val transactions = wrapTransactions(data)
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