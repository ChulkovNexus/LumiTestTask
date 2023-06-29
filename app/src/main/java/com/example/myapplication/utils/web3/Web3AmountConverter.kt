package com.example.myapplication.utils.web3

import android.content.Context
import com.example.myapplication.R
import com.example.myapplication.network.responses.EtherTransactionEntity
import com.example.myapplication.utils.AmountFormatter
import java.math.BigDecimal
import java.math.BigInteger

object Web3AmountConverter {
    fun fromWei(number: String?, unit: Unit): BigDecimal {
        return fromWei(BigDecimal(number), unit)
    }

    fun fromWei(number: BigDecimal, unit: Unit): BigDecimal {
        return number.divide(unit.weiFactor)
    }

    fun toWei(number: String?, unit: Unit): BigDecimal {
        return toWei(BigDecimal(number), unit)
    }

    fun toWei(number: BigDecimal, unit: Unit): BigDecimal {
        return number.multiply(unit.weiFactor)
    }

    enum class Unit(private val currencyName: String, factor: Int) {
        WEI("wei", 0),
        KWEI("kwei", 3),
        MWEI("mwei", 6),
        GWEI("gwei", 9),
        SZABO("szabo",12),
        FINNEY("finney", 15),
        ETHER("ether", 18),
        KETHER("kether", 21),
        METHER("mether",24),
        GETHER("gether", 27);

        val weiFactor: BigDecimal

        init {
            weiFactor = BigDecimal.TEN.pow(factor)
        }

        override fun toString(): String {
            return currencyName
        }

        companion object {
            fun fromString(name: String?): Unit {
                if (name != null) {
                    for (unit in Unit.values()) {
                        if (name.equals(unit.currencyName, ignoreCase = true)) {
                            return unit
                        }
                    }
                }
                return Unit.valueOf(name!!)
            }
        }
    }

    fun decodeInput(context: Context, currentTransaction: EtherTransactionEntity): String {
        val inputData = currentTransaction.input
        var resultText = context.getString(R.string.no_smart_contract_data)

        // Проверяем сигнатуру функции
        when (inputData.take(10)) {
            // 0x095ea7b3 is the Method ID for approve(address,uint256)
            "0x095ea7b3" -> {
                val spenderAddress = "0x" + inputData.slice(34..73)
                val amount = BigInteger(inputData.slice(74 until inputData.length), 16)
                val amountText = AmountFormatter.format(fromWei(BigDecimal(amount), Web3AmountConverter.Unit.ETHER))
                resultText = context.getString(
                    R.string.approve_smart_contract_text,
                    spenderAddress,
                    amountText
                )
            }
            // 0xa9059cbb is the Method ID for transfer(address,uint256)
            "0xa9059cbb" -> {
                val toAddress = "0x" + inputData.slice(34..73)
                val amount = BigInteger(inputData.slice(74 until inputData.length), 16)

                val amountText = AmountFormatter.format(fromWei(BigDecimal(amount), Web3AmountConverter.Unit.ETHER))
                resultText = context.getString(
                    R.string.transfer_smart_contract_text,
                    toAddress,
                    amountText
                )
            }
        }
        return resultText
    }

    fun decodeAmountFromInput(currentTransaction: EtherTransactionEntity): BigDecimal {
        val inputData = currentTransaction.input

        // Проверяем сигнатуру функции
        when (inputData.take(10)) {
            // 0x095ea7b3 is the Method ID for approve(address,uint256)
            "0x095ea7b3" -> {
                val amount = BigInteger(inputData.slice(74 until inputData.length), 16)
                return fromWei(BigDecimal(amount), Web3AmountConverter.Unit.ETHER)
            }
            // 0xa9059cbb is the Method ID for transfer(address,uint256)
            "0xa9059cbb" -> {
                val amount = BigInteger(inputData.slice(74 until inputData.length), 16)
                return fromWei(BigDecimal(amount), Web3AmountConverter.Unit.ETHER)
            }
        }
        return BigDecimal(0)
    }
}