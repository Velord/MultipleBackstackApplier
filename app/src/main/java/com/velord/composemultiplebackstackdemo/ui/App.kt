package com.velord.composemultiplebackstackdemo.ui

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.context.GlobalContext.startKoin
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation")
class AppModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(AppModule().module)
        }
    }
}