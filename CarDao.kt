// app/src/main/java/com/drivecosts/data/local/database/dao/CarDao.kt

package com.drivecosts.data.local.database.dao

import androidx.room.*
import com.drivecosts.data.local.database.entity.CarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {
    
    /**
     * Отримати всі активні автомобілі (Stream)
     */
    @Query("SELECT * FROM cars WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllCars(): Flow<List<CarEntity>>
    
    /**
     * Отримати автомобіль за ID
     */
    @Query("SELECT * FROM cars WHERE id = :carId LIMIT 1")
    fun getCarById(carId: String): Flow<CarEntity?>
    
    /**
     * Отримати кількість активних автомобілів
     */
    @Query("SELECT COUNT(*) FROM cars WHERE isActive = 1")
    fun getActiveCarsCount(): Flow<Int>
    
    /**
     * Додати новий автомобіль
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: CarEntity)
    
    /**
     * Оновити інформацію автомобіля
     */
    @Update
    suspend fun updateCar(car: CarEntity)
    
    /**
     * Програмне видалення (просто позначити як неактивне)
     */
    @Query("UPDATE cars SET isActive = 0, updatedAt = :timestamp WHERE id = :carId")
    suspend fun deactivateCar(carId: String, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Фізичне видалення з БД (CASCADE видалить fuel_logs, expenses, reminders)
     */
    @Query("DELETE FROM cars WHERE id = :carId")
    suspend fun deleteCar(carId: String)
    
    /**
     * Пакетне вставлення
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCars(cars: List<CarEntity>)
    
    /**
     * Отримати автомобіль з найбільшим одометром
     */
    @Query("SELECT * FROM cars WHERE isActive = 1 ORDER BY currentOdometer DESC LIMIT 1")
    suspend fun getCarWithHighestOdometer(): CarEntity?
}
