package fr.ekito.myweatherapp.view.weather

import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.util.coroutine.SchedulerProvider
import fr.ekito.myweatherapp.util.mvp.AsyncPresenter
import fr.ekito.myweatherapp.view.weather.list.WeatherItem

/**
 * Weather Presenter
 */
class WeatherListPresenter(
        private val dailyForecastRepository: DailyForecastRepository,
        private val schedulerProvider: SchedulerProvider
) : AsyncPresenter<WeatherListContract.View>(schedulerProvider), WeatherListContract.Presenter {

    override var view: WeatherListContract.View? = null

    override fun getWeatherList() {
        launch {
            try {
                val weatherList = onIO {
                    val list = dailyForecastRepository.getWeather().map { WeatherItem.from(it) }
                    list.takeLast(list.size - 1)
                }
                view?.showWeatherItemList(weatherList)
            } catch (e: Throwable) {
                view?.showError(e)
            }
        }
    }
}