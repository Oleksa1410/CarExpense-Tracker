// app/src/main/java/com/drivecosts/di/DatabaseModule.kt

package com.drivecosts.di

import android.content.Context
import androidx.room.Room
import com.drivecosts.data.local.database.AppDatabase
import com.drivecosts.data.local.database.dao.CarDao
import com.drivecosts.data.local.database.dao.FuelLogDao
import com.drivecosts.data.local.database.dao.ExpenseDao
import com.drivecosts.data.local.database.dao.ReminderDao
import com.drivecosts.data.local.datastore.SettingsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "drivecosts.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideCarDao(database: AppDatabase): CarDao {
        return database.carDao()
    }

    @Singleton
    @Provides
    fun provideFuelLogDao(database: AppDatabase): FuelLogDao {
        return database.fuelLogDao()
    }

    @Singleton
    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Singleton
    @Provides
    fun provideReminderDao(database: AppDatabase): ReminderDao {
        return database.reminderDao()
    }

    @Singleton
    @Provides
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): SettingsDataStore {
        return SettingsDataStore(context)
    }
}
