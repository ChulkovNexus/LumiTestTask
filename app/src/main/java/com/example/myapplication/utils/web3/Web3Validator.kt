package com.example.myapplication.utils.web3

import org.bouncycastle.jcajce.provider.digest.Keccak
import java.math.BigInteger
import java.util.regex.Pattern

object Web3Validator {
    // Регулярное выражение для проверки формата Ethereum адреса.
    private val ETHEREUM_ADDRESS_PATTERN = Pattern.compile("^0x[0-9a-fA-F]{40}$")

    /**
     * Проверяет, является ли предоставленная строка действительным Ethereum адресом.
     *
     * @param address Адрес для проверки.
     * @return Возвращает true, если адрес является действительным Ethereum адресом, в противном случае false.
     */
    fun isErc20Address(address: String?): Boolean {
        // Проверка на null и формат адреса.
        if (address == null || !ETHEREUM_ADDRESS_PATTERN.matcher(address).matches()) {
            return false
        }

        // Проверка контрольной суммы адреса.
        return isChecksumValid(address)
    }

    /**
     * Проверяет, является ли контрольная сумма Ethereum адреса действительной в соответствии со стандартом EIP-55.
     *
     * @param address Адрес для проверки.
     * @return Возвращает true, если контрольная сумма действительна, в противном случае false.
     */
    private fun isChecksumValid(address: String): Boolean {
        // Удаление префикса '0x' из адреса.
        val checksumAddress = address.substring(2)

        // Создание экземпляра Keccak-256 хеш-функции.
        val digest = Keccak.Digest256()
        // Вычисление хеша адреса с преобразованием в нижний регистр.
        val hash = digest.digest(checksumAddress.lowercase().toByteArray())
        // Преобразование хеша в шестнадцатеричную строку.
        val lowercaseHash = String.format("%064x", BigInteger(1, hash))

        // Проверка каждого символа в адресе на соответствие контрольной сумме.
        for (i in 0..39) {
            val addressChar = checksumAddress[i]
            val hashValue = Integer.parseInt(lowercaseHash[i].toString(), 16)

            // Символ должен быть в верхнем регистре, если соответствующее значение хеша больше или равно 8.
            // В противном случае, символ должен быть в нижнем регистре.
            if ((hashValue >= 8 && Character.toUpperCase(addressChar) != addressChar)
                || (hashValue < 8 && Character.toLowerCase(addressChar) != addressChar)
            ) {
                return false
            }
        }

        return true
    }
}