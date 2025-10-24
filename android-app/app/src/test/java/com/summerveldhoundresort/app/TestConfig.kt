package com.summerveldhoundresort.app

import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Base test configuration for unit tests
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
abstract class TestConfig {
    
    @get:Rule
    val testRule: TestRule = TestRule { statement, description ->
        // Setup test environment
        statement
    }
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        // Initialize test context
        RuntimeEnvironment.setApplication(TestApplication())
    }
}

/**
 * Test application class for Robolectric tests
 */
class TestApplication : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
    }
}