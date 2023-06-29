package com.example.myapplication.userstories.current_transaction

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R
import com.example.myapplication.databinding.DialogTransactionInputBinding
import com.example.myapplication.network.responses.EtherTransactionEntity
import com.example.myapplication.utils.view_binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigInteger

@AndroidEntryPoint
class TransactionInputDialog : DialogFragment(R.layout.dialog_transaction_input) {

    private val binding by viewBinding(DialogTransactionInputBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            (it.getSerializable(CURRENT_TRANSACTION) as EtherTransactionEntity).let { currentTransaction ->
                val decodeInput = decodeInput(currentTransaction)
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

    private fun decodeInput(currentTransaction: EtherTransactionEntity): String {
        val inputData = currentTransaction.input
        var resultText = requireContext().getString(R.string.no_smart_contract_data)

        // Проверяем сигнатуру функции
        when (inputData.take(10)) {
            // 0x095ea7b3 is the Method ID for approve(address,uint256)
            "0x095ea7b3" -> {
                val spenderAddress = "0x" + inputData.slice(34..73)
                val amount = BigInteger(inputData.slice(74 until inputData.length), 16)
                resultText = requireContext().getString(R.string.approve_smart_contract_text, spenderAddress, amount)
            }
            // 0xa9059cbb is the Method ID for transfer(address,uint256)
            "0xa9059cbb" -> {
                val toAddress = "0x" + inputData.slice(34..73)
                val amount = BigInteger(inputData.slice(74 until inputData.length), 16)
                resultText = requireContext().getString(R.string.transfer_smart_contract_text, toAddress, amount)
            }
        }
        return resultText
    }

    companion object {
        const val CURRENT_TRANSACTION = "current_transaction"
    }
}