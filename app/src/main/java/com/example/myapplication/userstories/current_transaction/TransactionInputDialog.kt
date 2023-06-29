package com.example.myapplication.userstories.current_transaction

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R
import com.example.myapplication.databinding.DialogTransactionInputBinding
import com.example.myapplication.network.responses.EtherTransactionEntity
import com.example.myapplication.utils.AmountFormatter
import com.example.myapplication.utils.view_binding.viewBinding
import com.example.myapplication.utils.web3.Web3AmountConverter
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.math.BigInteger

@AndroidEntryPoint
class TransactionInputDialog : DialogFragment(R.layout.dialog_transaction_input) {

    private val binding by viewBinding(DialogTransactionInputBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            (it.getSerializable(CURRENT_TRANSACTION) as EtherTransactionEntity).let { currentTransaction ->
                val decodeInput = Web3AmountConverter.decodeInput(requireContext(), currentTransaction)
                binding.inputText.text = decodeInput
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Задаем параметры окна диалога после его создания
        dialog?.window?.let { window ->
            val params = WindowManager.LayoutParams()
            params.copyFrom(window.attributes)
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = requireContext().resources.getDimensionPixelSize(R.dimen.dialog_size)
            window.attributes = params
        }
    }

    companion object {
        const val CURRENT_TRANSACTION = "current_transaction"
    }
}