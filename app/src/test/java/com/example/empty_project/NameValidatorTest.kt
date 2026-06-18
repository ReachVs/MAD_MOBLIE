package com.example.empty_project

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NameValidatorTest {

    @Test
    fun isValid_validName_returnsTrue() {
        assertTrue(NameValidator.isValid("Alice"))
        assertTrue(NameValidator.isValid("A"))
        assertTrue(NameValidator.isValid("Bob the Builder"))
    }

    @Test
    fun isValid_blankName_returnsFalse() {
        assertFalse(NameValidator.isValid(""))
        assertFalse(NameValidator.isValid(" "))
        assertFalse(NameValidator.isValid("  \n  "))
    }

    @Test
    fun isValid_longName_returnsFalse() {
        val longName = "A".repeat(NameValidator.MAX_NAME_LENGTH + 1)
        assertFalse(NameValidator.isValid(longName))
    }

    @Test
    fun isValid_maxAllowedLength_returnsTrue() {
        val maxName = "A".repeat(NameValidator.MAX_NAME_LENGTH)
        assertTrue(NameValidator.isValid(maxName))
    }
}
