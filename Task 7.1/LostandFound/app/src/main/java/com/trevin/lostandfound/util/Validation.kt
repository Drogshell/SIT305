package com.trevin.lostandfound.util

class Validation {

    companion object {
        fun isValidPhoneNumber(phoneNum: String): Boolean {
            val pattern = "^0[2-478](?:[\\s-]?\\d){8}$"
            return Regex(pattern).matches(phoneNum)
        }

        fun formatPhoneNumber(phone: String): String {
            val digits = phone.filter { it.isDigit() }
            if (digits.length != 10) return phone

            return "${digits.substring(0, 2)} ${digits.substring(2, 6)} ${digits.substring(6, 10)}"
        }

    }

}