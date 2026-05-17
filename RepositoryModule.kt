// app/src/main/java/com/drivecosts/di/RepositoryModule.kt

package com.drivecosts.di

import com.drivecosts.data.repository.CarRepositoryImpl
import com.drivecosts.data.repository.FuelRepositoryImpl
import com.drivecosts.data.repository.ExpenseRepositoryImpl
import com.drivecosts.data.repository.ReminderRepositoryImpl
import com.drivecosts.domain.repository.CarRepository
import com.drivecosts.domain.repository.FuelRepository
import com.drivecosts.domain.repository.ExpenseRepository
import com.drivecosts.domain.repository.ReminderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindCarRepository(
        impl: CarRepositoryImpl
    ): CarRepository

    @Singleton
    @Binds
    abstract fun bindFuelRepository(
        impl: FuelRepositoryImpl
    ): FuelRepository

    @Singleton
    @Binds
    abstract fun bindExpenseRepository(
        impl: ExpenseRepositoryImpl
    ): ExpenseRepository

    @Singleton
    @Binds
    abstract fun bindReminderRepository(
        impl: ReminderRepositoryImpl
    ): ReminderRepository
}
