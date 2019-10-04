package fr.ekito.myweatherapp.integration

import fr.ekito.myweatherapp.data.WeatherDataSource
import fr.ekito.myweatherapp.data.json.Geocode
import fr.ekito.myweatherapp.di.onlineTestWeatherApp
import fr.ekito.myweatherapp.domain.ext.getDailyForecasts
import fr.ekito.myweatherapp.domain.ext.getLocation
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class WeatherWebServiceTest : KoinTest {

    val weatherWebService by inject<WeatherDataSource>()

    val address = "Toulouse, France"

    @Before
    fun before() {
        startKoin {
            modules(onlineTestWeatherApp)
        }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `get geocode`() {
        val data: Geocode = weatherWebService.geocode(address).blockingGet()
        assertNotNull(data.status == "OK")
    }

    @Test
    fun `get weather`() {
        val geocodeData = weatherWebService.geocode(address).blockingGet()
        val location = geocodeData.getLocation() ?: error("No location found")
        val weather = weatherWebService.weather(location.lat, location.lng, "EN").blockingGet()
        assertNotNull(weather.getDailyForecasts(address).isNotEmpty())
    }

    @Test
    fun `get weather - functional`() {
        val forecast = weatherWebService.geocode(address)
                .map { geocodeData -> geocodeData.getLocation() ?: error("No location") }
                .flatMap { location -> weatherWebService.weather(location.lat, location.lng, "EN") }
                .map { weather -> weather.getDailyForecasts(address) }
                .blockingGet()

        assertNotNull(forecast.isNotEmpty())
    }

    @Test
    fun `observe weather`() {
        weatherWebService.geocode(address)
                .map { geocodeData -> geocodeData.getLocation() ?: error("No location") }
                .flatMap { location -> weatherWebService.weather(location.lat, location.lng, "EN") }
                .map { weather -> weather.getDailyForecasts(address) }
                .observeOn(Schedulers.io())
                .subscribe { forecast ->
                    println("forecast -> $forecast")
                }
    }
}