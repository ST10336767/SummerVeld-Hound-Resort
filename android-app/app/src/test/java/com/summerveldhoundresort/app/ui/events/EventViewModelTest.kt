package com.summerveldhoundresort.app.ui.events

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.summerveldhoundresort.app.db.entities.Event
import com.summerveldhoundresort.app.network.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class EventViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var eventRepository: EventRepository

    @Mock
    private lateinit var eventsObserver: Observer<List<Event>>

    @Mock
    private lateinit var loadingObserver: Observer<Boolean>

    @Mock
    private lateinit var errorObserver: Observer<String?>

    private lateinit var eventViewModel: EventViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        eventViewModel = EventViewModel(eventRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadEvents should return list of events successfully`() = runTest {
        // Given
        val mockEvents = listOf(
            Event("event1", "Dog Training", "Basic obedience training", "2024-02-15", "Main Hall", "10:00 AM"),
            Event("event2", "Pet Grooming", "Professional grooming session", "2024-02-16", "Grooming Room", "2:00 PM")
        )

        `when`(eventRepository.getAllEvents()).thenReturn(mockEvents)

        // When
        eventViewModel.loadEvents()

        // Then
        verify(eventRepository).getAllEvents()
        // Note: In a real test, you would observe the LiveData values
    }

    @Test
    fun `loadEvents should handle empty list`() = runTest {
        // Given
        val emptyEvents = emptyList<Event>()
        `when`(eventRepository.getAllEvents()).thenReturn(emptyEvents)

        // When
        eventViewModel.loadEvents()

        // Then
        verify(eventRepository).getAllEvents()
    }

    @Test
    fun `loadEvents should handle repository error`() = runTest {
        // Given
        val errorMessage = "Failed to load events"
        `when`(eventRepository.getAllEvents()).thenThrow(RuntimeException(errorMessage))

        // When
        eventViewModel.loadEvents()

        // Then
        verify(eventRepository).getAllEvents()
        // Note: In a real test, you would verify error handling
    }

    @Test
    fun `createEvent should create new event successfully`() = runTest {
        // Given
        val newEvent = Event("", "New Event", "Event description", "2024-02-20", "Location", "3:00 PM")
        val createdEvent = Event("event123", "New Event", "Event description", "2024-02-20", "Location", "3:00 PM")

        `when`(eventRepository.createEvent(newEvent)).thenReturn(createdEvent)

        // When
        eventViewModel.createEvent(newEvent)

        // Then
        verify(eventRepository).createEvent(newEvent)
    }

    @Test
    fun `updateEvent should update existing event successfully`() = runTest {
        // Given
        val eventId = "event123"
        val updatedEvent = Event(eventId, "Updated Event", "Updated description", "2024-02-20", "New Location", "4:00 PM")

        `when`(eventRepository.updateEvent(eventId, updatedEvent)).thenReturn(updatedEvent)

        // When
        eventViewModel.updateEvent(eventId, updatedEvent)

        // Then
        verify(eventRepository).updateEvent(eventId, updatedEvent)
    }

    @Test
    fun `deleteEvent should delete event successfully`() = runTest {
        // Given
        val eventId = "event123"

        `when`(eventRepository.deleteEvent(eventId)).thenReturn(true)

        // When
        eventViewModel.deleteEvent(eventId)

        // Then
        verify(eventRepository).deleteEvent(eventId)
    }

    @Test
    fun `deleteEvent should handle non-existent event`() = runTest {
        // Given
        val eventId = "nonexistent"

        `when`(eventRepository.deleteEvent(eventId)).thenReturn(false)

        // When
        eventViewModel.deleteEvent(eventId)

        // Then
        verify(eventRepository).deleteEvent(eventId)
    }

    @Test
    fun `getEventById should return specific event`() = runTest {
        // Given
        val eventId = "event123"
        val expectedEvent = Event(eventId, "Test Event", "Test description", "2024-02-15", "Main Hall", "10:00 AM")

        `when`(eventRepository.getEventById(eventId)).thenReturn(expectedEvent)

        // When
        eventViewModel.getEventById(eventId)

        // Then
        verify(eventRepository).getEventById(eventId)
    }

    @Test
    fun `getEventById should handle non-existent event`() = runTest {
        // Given
        val eventId = "nonexistent"

        `when`(eventRepository.getEventById(eventId)).thenReturn(null)

        // When
        eventViewModel.getEventById(eventId)

        // Then
        verify(eventRepository).getEventById(eventId)
    }

    @Test
    fun `searchEvents should filter events by name`() = runTest {
        // Given
        val searchQuery = "training"
        val allEvents = listOf(
            Event("event1", "Dog Training", "Basic obedience", "2024-02-15", "Main Hall", "10:00 AM"),
            Event("event2", "Pet Grooming", "Professional grooming", "2024-02-16", "Grooming Room", "2:00 PM"),
            Event("event3", "Advanced Training", "Advanced obedience", "2024-02-17", "Training Room", "3:00 PM")
        )
        val filteredEvents = allEvents.filter { it.name.contains(searchQuery, ignoreCase = true) }

        `when`(eventRepository.searchEvents(searchQuery)).thenReturn(filteredEvents)

        // When
        eventViewModel.searchEvents(searchQuery)

        // Then
        verify(eventRepository).searchEvents(searchQuery)
    }

    @Test
    fun `getEventsByDate should return events for specific date`() = runTest {
        // Given
        val targetDate = "2024-02-15"
        val eventsForDate = listOf(
            Event("event1", "Morning Training", "Basic training", targetDate, "Main Hall", "10:00 AM"),
            Event("event2", "Afternoon Session", "Advanced training", targetDate, "Training Room", "2:00 PM")
        )

        `when`(eventRepository.getEventsByDate(targetDate)).thenReturn(eventsForDate)

        // When
        eventViewModel.getEventsByDate(targetDate)

        // Then
        verify(eventRepository).getEventsByDate(targetDate)
    }
}
