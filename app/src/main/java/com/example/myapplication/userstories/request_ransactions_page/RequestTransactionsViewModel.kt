package com.example.myapplication.userstories.request_ransactions_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.utils.web3.Web3Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RequestTransactionsViewModel @Inject constructor() : ViewModel() {
    sealed class RequestTransactionsState {
        data class Initial(val submitAllowed: Boolean) : RequestTransactionsState()
    }

    private var submitAllowedState: Boolean = false
    private var _requestTransactionsState: MutableLiveData<RequestTransactionsState> =
        MutableLiveData(RequestTransactionsState.Initial(submitAllowedState))
    val requestTransactionsState: LiveData<RequestTransactionsState> = _requestTransactionsState

    fun validateAddress(accountAddress: String) {
        submitAllowedState = Web3Validator.isErc20Address(accountAddress)
        resetState()
    }

    fun resetState() {
        _requestTransactionsState.postValue(RequestTransactionsState.Initial(submitAllowedState))
    }
}