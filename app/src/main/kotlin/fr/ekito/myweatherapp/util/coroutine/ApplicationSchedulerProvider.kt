package fr.ekito.myweatherapp.util.coroutine

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Application providers
 */
class ApplicationSchedulerProvider : SchedulerProvider {
    override fun ui() = kotlinx.coroutines.Dispatchers.Main
    override fun computation(): CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Default
    override fun io() = kotlinx.coroutines.Dispatchers.IO
}