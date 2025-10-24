package com.summerveldhoundresort.app.db.entities

import org.junit.Test
import org.junit.Assert.*

class EventTest {

    @Test
    fun `event creation with all parameters should work correctly`() {
        // Given
        val eventId = "event123"
        val name = "Dog Training Workshop"
        val description = "Learn basic obedience training for your dog"
        val date = "2024-02-15"
        val location = "Main Training Hall"
        val time = "10:00 AM"

        // When
        val event = Event(eventId, name, description, date, location, time)

        // Then
        assertEquals(eventId, event.id)
        assertEquals(name, event.name)
        assertEquals(description, event.description)
        assertEquals(date, event.date)
        assertEquals(location, event.location)
        assertEquals(time, event.time)
    }

    @Test
    fun `event creation with empty constructor should work`() {
        // When
        val event = Event()

        // Then
        assertTrue(event.id.isEmpty())
        assertTrue(event.name.isEmpty())
        assertTrue(event.description.isEmpty())
        assertTrue(event.date.isEmpty())
        assertTrue(event.location.isEmpty())
        assertTrue(event.time.isEmpty())
    }

    @Test
    fun `event with special characters in name should work`() {
        // Given
        val specialName = "Dog's Day Out & Training Session"
        val event = Event(
            id = "event123",
            name = specialName,
            description = "Special event with special characters",
            date = "2024-02-15",
            location = "Main Hall",
            time = "10:00 AM"
        )

        // Then
        assertEquals(specialName, event.name)
    }

    @Test
    fun `event with long description should work`() {
        // Given
        val longDescription = "This is a very long description for an event that contains " +
                "multiple sentences and provides detailed information about what will " +
                "happen during the event, including all the activities and requirements."
        val event = Event(
            id = "event123",
            name = "Test Event",
            description = longDescription,
            date = "2024-02-15",
            location = "Main Hall",
            time = "10:00 AM"
        )

        // Then
        assertEquals(longDescription, event.description)
        assertTrue(event.description.length > 100)
    }

    @Test
    fun `event with different date formats should work`() {
        // Given
        val dateFormats = listOf(
            "2024-02-15",
            "15/02/2024",
            "February 15, 2024",
            "2024-02-15T10:00:00Z"
        )

        dateFormats.forEach { dateFormat ->
            // When
            val event = Event(
                id = "event123",
                name = "Test Event",
                description = "Test description",
                date = dateFormat,
                location = "Main Hall",
                time = "10:00 AM"
            )

            // Then
            assertEquals(dateFormat, event.date)
        }
    }

    @Test
    fun `event with different time formats should work`() {
        // Given
        val timeFormats = listOf(
            "10:00 AM",
            "10:00",
            "22:00",
            "10:00:00",
            "10:00 AM - 12:00 PM"
        )

        timeFormats.forEach { timeFormat ->
            // When
            val event = Event(
                id = "event123",
                name = "Test Event",
                description = "Test description",
                date = "2024-02-15",
                location = "Main Hall",
                time = timeFormat
            )

            // Then
            assertEquals(timeFormat, event.time)
        }
    }

    @Test
    fun `event with empty strings should work`() {
        // Given
        val event = Event(
            id = "",
            name = "",
            description = "",
            date = "",
            location = "",
            time = ""
        )

        // Then
        assertTrue(event.id.isEmpty())
        assertTrue(event.name.isEmpty())
        assertTrue(event.description.isEmpty())
        assertTrue(event.date.isEmpty())
        assertTrue(event.location.isEmpty())
        assertTrue(event.time.isEmpty())
    }

    @Test
    fun `event with null-like values should work`() {
        // Given
        val event = Event(
            id = "null",
            name = "null",
            description = "null",
            date = "null",
            location = "null",
            time = "null"
        )

        // Then
        assertEquals("null", event.id)
        assertEquals("null", event.name)
        assertEquals("null", event.description)
        assertEquals("null", event.date)
        assertEquals("null", event.location)
        assertEquals("null", event.time)
    }
}
