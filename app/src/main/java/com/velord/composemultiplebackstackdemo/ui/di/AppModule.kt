package com.velord.composemultiplebackstackdemo.ui.di

import com.velord.composemultiplebackstackdemo.ui.main.bottomNavigation.BottomNavEventService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideBottomNavEventService(): BottomNavEventService = BottomNavEventService
}