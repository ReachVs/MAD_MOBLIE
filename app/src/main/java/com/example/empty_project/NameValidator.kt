package com.example.empty_project

object NameValidator {
    const val MAX_NAME_LENGTH = 20

    /**
     * Validates the name.
     * Returns true if the name is not blank and within the allowed length.
     */
    fun isValid(name: String): Boolean {
        return name.isNotBlank() && name.trim().length <= MAX_NAME_LENGTH
    }
}
