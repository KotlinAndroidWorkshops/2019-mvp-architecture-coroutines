package fr.ekito.myweatherapp.mock.mvp

import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.util.TestSchedulerProvider
import fr.ekito.myweatherapp.view.detail.DetailContract
import fr.ekito.myweatherapp.view.detail.DetailPresenter
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class DetailPresenterMockTest {

    lateinit var presenter: DetailContract.Presenter
    val view: DetailContract.View = mockk(relaxed = true)
    val repository: DailyForecastRepository = mockk(relaxed = true)

    // TODO uncomment to use LiveData in Test
//    @get:Rule
//    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        presenter = DetailPresenter(repository, TestSchedulerProvider())
        presenter.view = view
    }

    @Test
    fun testGetLastWeather() {
        val weather = mock(DailyForecast::class.java)
        val id = "ID"

        coEvery { repository.getWeatherDetail(id) } returns weather

        presenter.getDetail(id)

        verifySequence {
            view.showDetail(weather)
        }
    }

    @Test
    fun testGeLasttWeatherFailed() {
        val error = Throwable("Got error")
        val id = "ID"

        coEvery { repository.getWeatherDetail(id) } throws error

        presenter.getDetail(id)

        verifySequence {
            view.showError(error)
        }
    }
}