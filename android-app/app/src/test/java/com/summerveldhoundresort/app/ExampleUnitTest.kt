package com.summerveldhoundresort.app

import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun string_concatenation_works() {
        val firstName = "John"
        val lastName = "Doe"
        val fullName = "$firstName $lastName"
        assertEquals("John Doe", fullName)
    }

    @Test
    fun email_validation_works() {
        val validEmail = "test@example.com"
        val invalidEmail = "invalid-email"
        
        assertTrue(validEmail.contains("@"))
        assertFalse(invalidEmail.contains("@"))
    }
}