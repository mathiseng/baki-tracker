package com.example.baki_tracker.dependencyInjection

import me.tatarka.inject.annotations.Component

/**
 * Here the dependencies and classes we need to inject are placed
 */
@Component
abstract class MainActivityComponent(@Component val parent: ApplicationComponent) {


}
