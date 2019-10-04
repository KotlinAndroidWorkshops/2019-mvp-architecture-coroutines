package fr.ekito.myweatherapp.util

import fr.ekito.myweatherapp.util.coroutine.SchedulerProvider

class TestSchedulerProvider : SchedulerProvider {
    override fun io() = kotlinx.coroutines.Dispatchers.Unconfined

    override fun ui() = kotlinx.coroutines.Dispatchers.Unconfined

    override fun computation() = kotlinx.coroutines.Dispatchers.Unconfined
}