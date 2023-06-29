package com.example.myapplication.utils.web3

import java.math.BigDecimal

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
}