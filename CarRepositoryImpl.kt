// app/src/main/java/com/drivecosts/data/repository/CarRepositoryImpl.kt

package com.drivecosts.data.repository

import com.drivecosts.data.local.database.dao.CarDao
import com.drivecosts.data.local.database.entity.CarEntity
import com.drivecosts.domain.model.Car
import com.drivecosts.domain.model.FuelType
import com.drivecosts.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CarRepositoryImpl @Inject constructor(
    private val carDao: CarDao
) : CarRepository {

    /**
     * Отримати всі активні автомобілі як Domain моделі
     */
    override fun getAllCars(): Flow<List<Car>> {
        return carDao.getAllCars().map { entities ->
            entities.map { entity ->
                entity.toDomainModel()
            }
        }
    }

    /**
     * Отримати автомобіль за ID
     */
    override fun getCarById(carId: String): Flow<Car?> {
        return carDao.getCarById(carId).map { entity ->
            entity?.toDomainModel()
        }
    }

    /**
     * Додати новий автомобіль
     */
    override suspend fun insertCar(car: Car) {
        val entity = car.toEntity()
        carDao.insertCar(entity)
    }

    /**
     * Оновити автомобіль (одометр, статус, тощо)
     */
    override suspend fun updateCar(car: Car) {
        val entity = car.toEntity()
        carDao.updateCar(entity)
    }

    /**
     * Видалити автомобіль (програмне видалення - позначити як неактивне)
     */
    override suspend fun deleteCar(carId: String) {
        carDao.deactivateCar(carId)
    }

    /**
     * Отримати кількість активних автомобілів
     */
    override fun getActiveCarsCount(): Flow<Int> {
        return carDao.getActiveCarsCount()
    }

    // ========== EXTENSIONS ==========

    /**
     * Конвертація Domain моделі → Entity (для БД)
     */
    private fun Car.toEntity(): CarEntity {
        return CarEntity(
            id = id,
            name = name,
            brand = brand,
            model = model,
            year = year,
            licensePlate = licensePlate,
            currentOdometer = currentOdometer,
            fuelType = fuelType.name,
            currency = currency,
            isActive = isActive,
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Конвертація Entity → Domain модель (для UI)
     */
    private fun CarEntity.toDomainModel(): Car {
        return Car(
            id = id,
            name = name,
            brand = brand,
            model = model,
            year = year,
            licensePlate = licensePlate,
            currentOdometer = currentOdometer,
            fuelType = FuelType.valueOf(fuelType),
            currency = currency,
            isActive = isActive
        )
    }
}
