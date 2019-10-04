package fr.ekito.myweatherapp.data.local

import fr.ekito.myweatherapp.data.WeatherDataSource
import fr.ekito.myweatherapp.data.json.Geocode
import fr.ekito.myweatherapp.data.json.Weather
import kotlinx.coroutines.*

/**
 * Read json files and render weather date
 */
class FileDataSource(val jsonReader: JsonReader, val delayed: Boolean) :
        WeatherDataSource {

    private val supervisorJob = SupervisorJob()
    val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + supervisorJob)

    private val cities by lazy { jsonReader.getAllLocations() }

    private fun isKnownCity(address: String): Boolean = cities.values.contains(address)

    private fun cityFromLocation(lat: Double?, lng: Double?): String {
        return cities.filterKeys { it.lat == lat && it.lng == lng }.values.firstOrNull()
                ?: DEFAULT_CITY
    }

    override fun geocode(address: String): Deferred<Geocode> {
        return coroutineScope.async {
            if (delayed) {
                delay(1_000)
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
        return coroutineScope.async {
            if (delayed) {
                delay(1_000)
            }
            val city = cityFromLocation(lat, lon)
            jsonReader.getWeather(city)
        }
    }

    fun close(){
        supervisorJob.cancel()
    }

    companion object {
        const val DEFAULT_CITY = "toulouse"
    }
}