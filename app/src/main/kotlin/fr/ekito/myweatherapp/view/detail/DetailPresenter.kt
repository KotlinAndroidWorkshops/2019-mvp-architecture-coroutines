package fr.ekito.myweatherapp.view.detail

import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.util.coroutine.SchedulerProvider
import fr.ekito.myweatherapp.util.mvp.AsyncPresenter

class DetailPresenter(
        private val dailyForecastRepository: DailyForecastRepository,
        private val schedulerProvider: SchedulerProvider
) : AsyncPresenter<DetailContract.View>(schedulerProvider), DetailContract.Presenter {

    override var view: DetailContract.View? = null

    override fun getDetail(id: String) = launch {
        try {
            val detail = onIO { dailyForecastRepository.getWeatherDetail(id) }
            view?.showDetail(detail)
        } catch (e: Throwable) {
            view?.showError(e)
        }
    }

}