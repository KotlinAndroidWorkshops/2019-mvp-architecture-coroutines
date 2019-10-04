package fr.ekito.myweatherapp.data.local

import fr.ekito.myweatherapp.data.WeatherDataSource
import fr.ekito.myweatherapp.data.json.Geocode
import fr.ekito.myweatherapp.data.json.Weather
import fr.ekito.myweatherapp.util.coroutine.SchedulerProvider
import kotlinx.coroutines.*

/**
 * Read json files and render weather date
 */
class FileDataSource(val schedulerProvider: SchedulerProvider, val jsonReader: JsonReader, val delayed: Boolean) :
        WeatherDataSource {

    private val supervisorJob = SupervisorJob()
    val coroutineScope: CoroutineScope = CoroutineScope(schedulerProvider.io() + supervisorJob)

    private val cities by lazy { jsonReader.getAllLocations() }

    private fun isKnownCity(address: String): Boolean = cities.values.contains(address)

    private fun cityFromLocation(lat: Double?, lng: Double?): String {
        return cities.filterKeys { it.lat == lat && it.lng == lng }.values.firstOrNull()
                ?: DEFAULT_CITY
    }

    override fun geocode(address: String): Deferred<Geocode> {
        return coroutineScope.async(schedulerProvider.io()) {
            if (delayed) {
                delay(DELAY)
            }
            val addressToLC = address.toLowerCase()
            if (isKnownCity(addressToLC)) {
                jsonReader.getGeocode(addressToLC)
            } else {
                jsonReader.getGeocode(DEFAULT_CITY)
            }
        }
    }

    override fun weather(lat: Double?, lon: Double?, lang: String): Deferred<Weather> {
        return coroutineScope.async(schedulerProvider.io()) {
            if (delayed) {
                delay(DELAY)
            }
            val city = cityFromLocation(lat, lon)
            jsonReader.getWeather(city)
        }
    }

    fun close() {
        supervisorJob.cancel()
    }

    companion object {
        const val DELAY = 500L
        const val DEFAULT_CITY = "toulouse"
    }
}