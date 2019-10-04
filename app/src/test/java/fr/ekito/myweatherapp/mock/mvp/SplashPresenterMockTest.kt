package fr.ekito.myweatherapp.mock.mvp

import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.util.TestSchedulerProvider
import fr.ekito.myweatherapp.view.splash.SplashContract
import fr.ekito.myweatherapp.view.splash.SplashPresenter
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class SplashPresenterMockTest {

    lateinit var presenter: SplashContract.Presenter
    val view: SplashContract.View = mockk(relaxed = true)
    val repository: DailyForecastRepository = mockk(relaxed = true)

    // TODO uncomment to use LiveData in Test
//    @get:Rule
//    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        presenter = SplashPresenter(repository, TestSchedulerProvider())
        presenter.view = view
    }

    @Test
    fun testGetLastWeather() {
        val list = listOf(mock(DailyForecast::class.java))

        coEvery { repository.getWeather() } returns list

        presenter.getLastWeather()

        verifySequence {
            view.showIsLoading()
            view.showIsLoaded()
        }
    }

    @Test
    fun testGetLasttWeatherFailed() {
        val error = Throwable("Got an error")

        coEvery { repository.getWeather() } throws error

        presenter.getLastWeather()

        verifySequence {
            view.showIsLoading()
            view.showError(error)
        }
    }

    companion object {
        const val DEFAULT_LOCATION = "DEFAULT_LOCATION"
    }
}