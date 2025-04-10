package com.trevin.ticknow.util

import android.text.Editable

object InputValidator {

    fun isValidInput(input: Editable?): Boolean {
        return !input?.trim().isNullOrEmpty() && input.length > 1
    }

}