package fr.ekito.myweatherapp.util.coroutine

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Rx Scheduler Provider
 */
interface SchedulerProvider {
    fun io(): CoroutineDispatcher
    fun ui(): CoroutineDispatcher
    fun computation(): CoroutineDispatcher
}