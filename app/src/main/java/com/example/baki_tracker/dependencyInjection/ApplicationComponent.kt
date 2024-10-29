package com.example.baki_tracker.dependencyInjection

import android.content.Context
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component @Singleton
abstract class ApplicationComponent(@get:Provides val context: Context)

interface ApplicationComponentProvider {
    val component: ApplicationComponent
}

// To help accessing the component later
val Context.applicationComponent
    get() = (applicationContext as ApplicationComponentProvider).component
