package fr.ekito.myweatherapp.integration

import fr.ekito.myweatherapp.di.testWeatherApp
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import junit.framework.Assert
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class WeatherRepositoryTest : KoinTest {

    val repository by inject<DailyForecastRepository>()

    val location = "Paris"

    @Before
    fun before() {
        startKoin {
            modules(testWeatherApp)
        }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun testGetWeatherSuccess() = runBlocking {
        repository.getWeather(location)
        assertNotNull(repository.getWeather(location))
    }

    @Test
    fun testCachedWeather() = runBlocking {
        val l1 = repository.getWeather("Paris")
        val l2 = repository.getWeather("Toulouse")
        val l3 = repository.getWeather()

        Assert.assertEquals(l3, l2)
        Assert.assertNotSame(l1, l2)
    }

    @Test
    fun testGetDetail() = runBlocking {
        repository.getWeather(location)
        val list = repository.getWeather(location)
        val first = list.first()
        val value = repository.getWeatherDetail(first.id)
        assertEquals(first, value)
    }
}