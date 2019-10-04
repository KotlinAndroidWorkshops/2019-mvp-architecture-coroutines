package fr.ekito.myweatherapp.view.weather

import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.util.coroutine.SchedulerProvider
import fr.ekito.myweatherapp.util.mvp.AsyncPresenter

class WeatherHeaderPresenter(
        private val dailyForecastRepository: DailyForecastRepository,
        private val schedulerProvider: SchedulerProvider
) : AsyncPresenter<WeatherHeaderContract.View>(schedulerProvider), WeatherHeaderContract.Presenter {

    override var view: WeatherHeaderContract.View? = null

    override fun loadNewLocation(location: String) {
        launch {
            try {
                onIO { dailyForecastRepository.getWeather(location) }
                view?.showLocationSearchSucceed(location)
            } catch (e: Throwable) {
                view?.showLocationSearchFailed(location, e)
            }
        }
    }

    override fun getWeatherOfTheDay() = launch {
        try {
            val weather = onIO { dailyForecastRepository.getWeather().first() }
            view?.showWeather(weather.location, weather)
        } catch (e: Throwable) {
            view?.showError(e)
        }
    }

}