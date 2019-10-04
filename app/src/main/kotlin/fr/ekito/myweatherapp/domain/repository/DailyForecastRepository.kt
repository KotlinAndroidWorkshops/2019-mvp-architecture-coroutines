package fr.ekito.myweatherapp.domain.repository

import fr.ekito.myweatherapp.data.WeatherDataSource
import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.ext.getDailyForecasts
import fr.ekito.myweatherapp.domain.ext.getLocation

/**
 * Weather repository
 */
interface DailyForecastRepository {
    /**
     * Get weather from given location
     * if location is null, get last weather or default
     */
    suspend fun getWeather(address: String? = null): List<DailyForecast>

    /**
     * Get weather for given id
     */
    suspend fun getWeatherDetail(id: String): DailyForecast
}

/**
 * Weather repository
 * Make use of WeatherDataSource & add some cache
 */
class DailyForecastRepositoryImpl(private val weatherDatasource: WeatherDataSource) :
        DailyForecastRepository {

    private fun lastLocationFromCache() = weatherCache.firstOrNull()?.location

    private val weatherCache = arrayListOf<DailyForecast>()

    override suspend fun getWeatherDetail(id: String): DailyForecast = weatherCache.first { it.id == id }

    override suspend fun getWeather(
            address: String?
    ): List<DailyForecast> {
        // Take cache
        return if (address == null && weatherCache.isNotEmpty()) return weatherCache
        else {
            val targetLocation: String = address ?: lastLocationFromCache() ?: DEFAULT_LOCATION
            weatherCache.clear()
            val location = weatherDatasource.geocode(targetLocation).await().getLocation()
                    ?: error("No location for $address")
            val forecast = weatherDatasource.weather(location.lat, location.lng, DEFAULT_LANG).await().getDailyForecasts(targetLocation)
            weatherCache.addAll(forecast)
            forecast
        }
    }

    companion object {
        const val DEFAULT_LOCATION = "Paris"
        const val DEFAULT_LANG = "EN"
    }
}
