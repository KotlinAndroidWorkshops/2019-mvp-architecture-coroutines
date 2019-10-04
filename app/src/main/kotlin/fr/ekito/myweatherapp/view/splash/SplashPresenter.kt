package fr.ekito.myweatherapp.view.splash

import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.util.coroutine.SchedulerProvider
import fr.ekito.myweatherapp.util.mvp.AsyncPresenter

class SplashPresenter(
        private val dailyForecastRepository: DailyForecastRepository,
        private val schedulerProvider: SchedulerProvider
) : AsyncPresenter<SplashContract.View>(schedulerProvider), SplashContract.Presenter {

    override var view: SplashContract.View? = null

    override fun getLastWeather() = launch {
        try {
            view?.showIsLoading()
            onIO { dailyForecastRepository.getWeather() }
            view?.showIsLoaded()
        } catch (e: Throwable) {
            view?.showError(e)
        }
    }

}