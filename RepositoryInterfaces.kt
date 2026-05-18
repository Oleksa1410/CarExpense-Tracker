// app/src/main/java/com/drivecosts/domain/repository/

package com.drivecosts.domain.repository

import com.drivecosts.domain.model.Car
import com.drivecosts.domain.model.FuelLog
import com.drivecosts.domain.model.Expense
import com.drivecosts.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

/**
 * CarRepository - контракт для управління автомобілями
 * Реалізація: CarRepositoryImpl (у data層)
 */
interface CarRepository {
    fun getAllCars(): Flow<List<Car>>
    fun getCarById(carId: String): Flow<Car?>
    suspend fun insertCar(car: Car)
    suspend fun updateCar(car: Car)
    suspend fun deleteCar(carId: String)
    fun getActiveCarsCount(): Flow<Int>
}

/**
 * FuelRepository - контракт для журналу палива
 * Реалізація: FuelRepositoryImpl (у data層)
 */
interface FuelRepository {
    fun getFuelLogsByCarId(carId: String): Flow<List<FuelLog>>
    
    suspend fun insertFuelLog(fuelLog: FuelLog)
    suspend fun updateFuelLog(fuelLog: FuelLog)
    suspend fun deleteFuelLog(logId: String)
    
    // Запити для звітності
    fun getFuelLogsByCarIdAndDateRange(
        carId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<FuelLog>>
    
    suspend fun getLastFuelLog(carId: String): FuelLog?
    
    fun getTotalFuelCostByCarAndMonth(
        carId: String,
        year: Int,
        month: Int
    ): Flow<Double>
}

/**
 * ExpenseRepository - контракт для витрат на ремонт/запчастини
 * Реалізація: ExpenseRepositoryImpl (у data層)
 */
interface ExpenseRepository {
    fun getExpensesByCarId(carId: String): Flow<List<Expense>>
    
    suspend fun insertExpense(expense: Expense)
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expenseId: String)
    
    // Запити для звітності
    fun getExpensesByCarIdAndDateRange(
        carId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<Expense>>
    
    fun getTotalExpenseByCarAndMonth(
        carId: String,
        year: Int,
        month: Int
    ): Flow<Double>
    
    fun getExpensesByCategory(
        carId: String,
        category: String
    ): Flow<List<Expense>>
}

/**
 * ReminderRepository - контракт для нагадувань
 * Реалізація: ReminderRepositoryImpl (у data層)
 */
interface ReminderRepository {
    fun getRemindersByCarId(carId: String): Flow<List<Reminder>>
    
    suspend fun insertReminder(reminder: Reminder)
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(reminderId: String)
    
    suspend fun markReminderAsCompleted(reminderId: String)
    
    // Запити для WorkManager
    suspend fun getUpcomingReminders(): List<Reminder>
    suspend fun getOverdueReminders(): List<Reminder>
    
    fun getRemindersByType(
        carId: String,
        type: String
    ): Flow<List<Reminder>>
}
