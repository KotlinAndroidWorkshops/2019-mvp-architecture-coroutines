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

    var supervisorJob: CompletableJob? = null
    var coroutineScope: CoroutineScope? = null

    fun launch(job: suspend CoroutineScope.() -> Unit) {
        coroutineScope?.apply {
            if (isActive) {
                launch(schedulerProvider.ui(), block = job)
            } else {
                System.err.println("$job cancelled!")
            }
        }
    }

    suspend fun <T> onIO(job: suspend CoroutineScope.() -> T) = withContext(schedulerProvider.io(), block = job)

    override fun subscribe(view: V) {
        supervisorJob = SupervisorJob()
        coroutineScope = CoroutineScope(Dispatchers.Main + supervisorJob!!)
        this.view = view
    }

    @CallSuper
    override fun unSubscribe() {
        supervisorJob?.cancel()
        supervisorJob = null
        coroutineScope = null
        view = null
    }
}