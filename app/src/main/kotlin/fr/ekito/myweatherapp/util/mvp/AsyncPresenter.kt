package fr.ekito.myweatherapp.util.mvp

import android.support.annotation.CallSuper
import fr.ekito.myweatherapp.util.coroutine.SchedulerProvider
import kotlinx.coroutines.*

/**
 * Base Presenter feature - for Rx Jobs
 *
 * launch() - launch a Rx request
 * clear all request on stop
 */
abstract class AsyncPresenter<V>(private val schedulerProvider: SchedulerProvider) : BasePresenter<V> {

    private val supervisorJob = SupervisorJob()
    val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + supervisorJob)

    fun launch(job: suspend CoroutineScope.() -> Unit) {
        coroutineScope.launch(schedulerProvider.ui(), block = job)
    }

    suspend fun <T> onIO(job: suspend CoroutineScope.() -> T) = withContext(schedulerProvider.io(), block = job)

    override fun subscribe(view: V) {
        this.view = view
    }

    @CallSuper
    override fun unSubscribe() {
        supervisorJob.cancel()
        view = null
    }
}