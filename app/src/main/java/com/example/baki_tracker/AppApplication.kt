package com.example.baki_tracker

import android.app.Application
import com.example.baki_tracker.dependencyInjection.ApplicationComponent
import com.example.baki_tracker.dependencyInjection.ApplicationComponentProvider
import com.example.baki_tracker.dependencyInjection.create

class AppApplication : Application(), ApplicationComponentProvider {
    override val component: ApplicationComponent
            by lazy { ApplicationComponent::class.create(applicationContext) }
}
