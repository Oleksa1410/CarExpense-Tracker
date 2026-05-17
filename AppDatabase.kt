// app/src/main/java/com/drivecosts/data/local/database/AppDatabase.kt

package com.drivecosts.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.drivecosts.data.local.database.dao.CarDao
import com.drivecosts.data.local.database.dao.FuelLogDao
import com.drivecosts.data.local.database.dao.ExpenseDao
import com.drivecosts.data.local.database.dao.ReminderDao
import com.drivecosts.data.local.database.entity.CarEntity
import com.drivecosts.data.local.database.entity.FuelLogEntity
import com.drivecosts.data.local.database.entity.ExpenseEntity
import com.drivecosts.data.local.database.entity.ReminderEntity

@Database(
    entities = [
        CarEntity::class,
        FuelLogEntity::class,
        ExpenseEntity::class,
        ReminderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun carDao(): CarDao
    abstract fun fuelLogDao(): FuelLogDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun reminderDao(): ReminderDao
}
