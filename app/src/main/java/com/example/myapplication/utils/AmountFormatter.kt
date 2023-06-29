package com.example.myapplication.utils

import java.math.BigDecimal

object AmountFormatter {

    fun format(amount: BigDecimal): String {
        return "${trimNulls(
            String.format(
                "%.100f",
                amount
            )
        )} ETH"
    }

    private fun trimNulls(string: String): String {
        var stringDouble = string
        var commaIndex = stringDouble.indexOf(",")

        if (commaIndex != -1) {
            for (i in stringDouble.length - 1 downTo commaIndex) {
                if (stringDouble[i] == '0') {
                    stringDouble = stringDouble.substring(0, i)
                } else {
                    break
                }
            }

            commaIndex = stringDouble.indexOf(",")
            if (commaIndex == stringDouble.length - 1) {
                stringDouble = stringDouble.substringBefore(",")
            }
        }
        return stringDouble
    }
}