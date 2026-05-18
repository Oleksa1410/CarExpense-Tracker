# ✅ DriveCosts - Чек-лист Реалізації

**Розроблено для**: Solo-Developer  
**Архітектура**: MVVM + Clean Architecture  
**Стек**: Kotlin + Jetpack Compose + Room + Hilt

---

## 🎯 ФАЗА 1: Налаштування Проєкту

### 1.1 Базова структура

- [ ] Клоновано репозиторій з GitHub
- [ ] Синхронізовано Gradle (`./gradlew sync`)
- [ ] Скопійовані файли конфігурації:
  - [ ] `gradle.properties` (версіонування)
  - [ ] `gradle/wrapper/gradle-wrapper.properties` (версія Gradle)
  - [ ] `build.gradle.kts` (root проєкту)
  - [ ] `settings.gradle.kts`
  - [ ] `app/build.gradle.kts` (app конфіг)

### 1.2 Проєктна структура

- [ ] Створено папки:
  ```
  app/src/main/java/com/drivecosts/
  ├── presentation/
  │   ├── ui/screens/
  │   ├── ui/components/
  │   ├── viewmodel/
  │   └── navigation/
  ├── domain/
  │   ├── model/
  │   └── repository/
  ├── data/
  │   ├── local/database/
  │   │   ├── dao/
  │   │   └── entity/
  │   ├── local/datastore/
  │   ├── local/worker/
  │   └── repository/
  └── di/
  ```

### 1.3 AdMob налаштування

- [ ] Зареєстровано на Google AdMob (https://admob.google.com)
- [ ] Створено App у AdMob
- [ ] Отримано:
  - [ ] ADMOB_APP_ID (ca-app-pub-xxxxx~yyyyy)
  - [ ] ADMOB_BANNER_ID (ca-app-pub-xxxxx/zzzzz)
  - [ ] ADMOB_INTERSTITIAL_ID (ca-app-pub-xxxxx/aaaaa)
- [ ] Додано у `gradle.properties`

---

## 🏗️ ФАЗА 2: Архітектура & Domain層

### 2.1 Domain Models

- [ ] **Car.kt** - модель автомобіля
  ```kotlin
  data class Car(
      val id: String,
      val name: String,
      val brand: String,
      val model: String,
      val year: Int,
      val licensePlate: String,
      val currentOdometer: Long,
      val fuelType: FuelType,
      val currency: String,
      val isActive: Boolean
  )
  ```

- [ ] **FuelLog.kt** - журнал палива
  ```kotlin
  data class FuelLog(
      val id: String,
      val carId: String,
      val odometer: Long,
      val fuelAmount: Double,
      val fuelPrice: Double,
      val totalPrice: Double,
      val currency: String,
      val date: Long,
      val notes: String
  )
  ```

- [ ] **Expense.kt** - витрати
  - [ ] Категорії: REPAIR, SPARE_PARTS, INSURANCE, SERVICE, OTHER

- [ ] **Reminder.kt** - нагадування
  - [ ] Типи: MAINTENANCE, INSURANCE, INSPECTION, OTHER

### 2.2 Repository Interfaces

- [ ] **CarRepository.kt** (interface)
  - [ ] `getAllCars(): Flow<List<Car>>`
  - [ ] `getCarById(carId: String): Flow<Car?>`
  - [ ] `insertCar(car: Car)`
  - [ ] `updateCar(car: Car)`
  - [ ] `deleteCar(carId: String)`

- [ ] **FuelRepository.kt** (interface)
  - [ ] `getFuelLogsByCarId(carId: String)`
  - [ ] `getFuelLogsByCarIdAndDateRange(...)`
  - [ ] `insertFuelLog(fuelLog: FuelLog)`
  - [ ] Методи для розрахунків

- [ ] **ExpenseRepository.kt** (interface)
  - [ ] `getExpensesByCarId(carId: String)`
  - [ ] `getExpensesByCategory(carId, category)`
  - [ ] `insertExpense(expense: Expense)`

- [ ] **ReminderRepository.kt** (interface)
  - [ ] `getRemindersByCarId(carId: String)`
  - [ ] `getUpcomingReminders()` (для WorkManager)
  - [ ] `insertReminder(reminder: Reminder)`

---

## 🗄️ ФАЗА 3: Data Layer & Database

### 3.1 Room Entities

- [ ] **CarEntity.kt**
  - [ ] Всі поля як у Car, але для БД
  - [ ] @PrimaryKey id
  - [ ] @Entity tableName = "cars"

- [ ] **FuelLogEntity.kt**
  - [ ] @ForeignKey на CarEntity (CASCADE DELETE)
  - [ ] Індекси на: carId, date

- [ ] **ExpenseEntity.kt**
  - [ ] @ForeignKey на CarEntity
  - [ ] Індекси на: carId, date, category

- [ ] **ReminderEntity.kt**
  - [ ] @ForeignKey на CarEntity
  - [ ] Індекси на: carId, nextDate

### 3.2 Room DAOs

- [ ] **CarDao.kt**
  - [ ] `@Query getAllCars(): Flow<List<CarEntity>>`
  - [ ] `@Insert insertCar(car: CarEntity)`
  - [ ] `@Update updateCar(car: CarEntity)`
  - [ ] `@Query DELETE BY ID`
  - [ ] `@Query getCarById(carId: String)`

- [ ] **FuelLogDao.kt**
  - [ ] `@Query SELECT * FROM fuel_logs WHERE carId = :carId`
  - [ ] `@Query SELECT BY DATE RANGE`
  - [ ] `@Query SUM(totalPrice) GROUP BY carId, month`

- [ ] **ExpenseDao.kt**
  - [ ] Аналогічно FuelLogDao

- [ ] **ReminderDao.kt**
  - [ ] `@Query getUpcomingReminders()`
  - [ ] `@Query getByType(carId, type)`

### 3.3 AppDatabase

- [ ] **AppDatabase.kt**
  - [ ] @Database entities = [Car, FuelLog, Expense, Reminder]
  - [ ] version = 1
  - [ ] fallbackToDestructiveMigration()
  - [ ] abstract fun xxxDao(): XxxDao

### 3.4 Repository Implementations

- [ ] **CarRepositoryImpl.kt**
  - [ ] Вводить CarDao через @Inject
  - [ ] Реалізує все методи CarRepository
  - [ ] Entity ↔ Domain конверсія

- [ ] **FuelRepositoryImpl.kt**
  - [ ] Вводить FuelLogDao
  - [ ] Методи для звітності

- [ ] **ExpenseRepositoryImpl.kt**
  - [ ] Аналогічно FuelRepository

- [ ] **ReminderRepositoryImpl.kt**
  - [ ] Для WorkManager інтеграції

### 3.5 DataStore (Налаштування)

- [ ] **SettingsDataStore.kt**
  - [ ] KEY: CURRENCY
  - [ ] KEY: LANGUAGE
  - [ ] Методи: `getCurrency()`, `setCurrency()`
  - [ ] Методи: `getLanguage()`, `setLanguage()`

---

## 💉 ФАЗА 4: Dependency Injection (Hilt)

### 4.1 Hilt Modules

- [ ] **DatabaseModule.kt**
  - [ ] `provideAppDatabase(context)`
  - [ ] `provide*Dao(database)` для кожного DAO
  - [ ] `provideSettingsDataStore(context)`
  - [ ] @Singleton для всіх

- [ ] **RepositoryModule.kt**
  - [ ] @Binds для:
    - [ ] CarRepository → CarRepositoryImpl
    - [ ] FuelRepository → FuelRepositoryImpl
    - [ ] ExpenseRepository → ExpenseRepositoryImpl
    - [ ] ReminderRepository → ReminderRepositoryImpl

### 4.2 Hilt Annotations

- [ ] **MainActivity.kt**
  - [ ] @AndroidEntryPoint

- [ ] **ViewModels**
  - [ ] @HiltViewModel
  - [ ] @Inject constructor параметри

- [ ] **App.kt** (Application class)
  - [ ] @HiltAndroidApp

---

## 📱 ФАЗА 5: Presentation Layer (UI)

### 5.1 ViewModels

- [ ] **HomeViewModel.kt**
  - [ ] @HiltViewModel
  - [ ] _cars: MutableStateFlow
  - [ ] cars: StateFlow (public)
  - [ ] loadCars() у init
  - [ ] addCar(car: Car)
  - [ ] deleteCar(carId: String)

- [ ] **FuelViewModel.kt**
  - [ ] _fuelLogs: MutableStateFlow
  - [ ] loadFuelLogs(carId: String)
  - [ ] addFuelLog(fuelLog: FuelLog)
  - [ ] Методи розрахунків (витрата л/100км)

- [ ] **ReportViewModel.kt**
  - [ ] _monthlyReport: MutableStateFlow<MonthlyReport>
  - [ ] generateMonthlyReport(carId, year, month)
  - [ ] Розрахунок метрик

- [ ] **ExpenseViewModel.kt**
  - [ ] Аналогічно FuelViewModel

- [ ] **ReminderViewModel.kt**
  - [ ] loadReminders(carId: String)
  - [ ] addReminder(reminder: Reminder)

- [ ] **SettingsViewModel.kt**
  - [ ] loadSettings()
  - [ ] setCurrency(currency: String)
  - [ ] setLanguage(language: String)

### 5.2 Screens (Compose)

- [ ] **HomeScreen.kt**
  - [ ] Список всіх автомобілів (CarCard)
  - [ ] FloatingActionButton "Додати авто"
  - [ ] Navigation до деталей

- [ ] **AddCarScreen.kt**
  - [ ] Форма додавання авто
  - [ ] Валідація
  - [ ] Save button

- [ ] **CarDetailsScreen.kt**
  - [ ] Карточка з інформацією
  - [ ] Кнопки: Додати паливо, Витрати, Нагадування, Звіт

- [ ] **AddFuelScreen.kt**
  - [ ] Поле: Одометр
  - [ ] Поле: Кількість літрів
  - [ ] Поле: Ціна за літр
  - [ ] Поле: Дата
  - [ ] Поле: Нотатка
  - [ ] Save button (+ BannerAd внизу)

- [ ] **ReportScreen.kt**
  - [ ] Місяць/Рік picker
  - [ ] Статистика:
    - [ ] Витрата л/100км
    - [ ] Вартість за км
    - [ ] Загальна вартість палива
    - [ ] Дистанція
  - [ ] Список записів за період

- [ ] **ReminderScreen.kt**
  - [ ] Список нагадувань
  - [ ] Додати нагадування
  - [ ] Марк як завершено

- [ ] **SettingsScreen.kt**
  - [ ] Вибір валюти (dropdown)
  - [ ] Вибір мови (dropdown)
  - [ ] Про додаток

- [ ] **MainNavigation.kt**
  - [ ] NavHost з NavGraph
  - [ ] BottomNavigationBar з маршрутами

### 5.3 Components

- [ ] **CarCard.kt**
  - [ ] Показує марку, модель, рік
  - [ ] Одометр
  - [ ] Текущий курс палива
  - [ ] Кнопки: Edit, Delete

- [ ] **FuelForm.kt**
  - [ ] Reusable форма для додавання палива

- [ ] **ExpenseItem.kt**
  - [ ] Показує категорію, суму, дату

- [ ] **ReminderItem.kt**
  - [ ] Показує тип, дату, статус

- [ ] **BannerAdView.kt**
  - [ ] AndroidView з AdView
  - [ ] ADMOB_BANNER_ID

- [ ] **CurrencyFormatter.kt** (Utility)
  - [ ] formatPrice(amount, currency): String
  - [ ] Правильне форматування для UAH, USD, EUR

### 5.4 Theme & Styling

- [ ] **Theme.kt**
  - [ ] Material 3 цвета
  - [ ] Dark mode поддержка

- [ ] **colors.xml**
  - [ ] Primary, Secondary, Tertiary
  - [ ] Background, Surface

- [ ] **dimens.xml**
  - [ ] Padding, margin стандартні розміри

---

## 🔔 ФАЗА 6: Notifications & Reminders

### 6.1 WorkManager

- [ ] **ReminderWorker.kt**
  - [ ] @HiltWorker
  - [ ] override doWork(): Result
  - [ ] Запит до ReminderRepository
  - [ ] sendNotification() для кожного

- [ ] **ReminderScheduler.kt**
  - [ ] scheduleReminders()
  - [ ] PeriodicWorkRequest (15 хвилин)
  - [ ] WorkManager.enqueueUniquePeriodicWork()

### 6.2 Notification Channel

- [ ] Програмне створення у ReminderWorker:
  - [ ] Channel ID: "REMINDER_CHANNEL"
  - [ ] Importance: HIGH

### 6.3 Інтеграція у MainActivity

- [ ] Запуск ReminderScheduler.scheduleReminders() у onCreate()

---

## 🌍 ФАЗА 7: Мультивалютність & Багатомовність

### 7.1 String Resources

- [ ] **res/values/strings.xml** (Українська)
  - [ ] app_name, app_version
  - [ ] Всі текстові константи
  - [ ] Мінімум 50+ рядків

- [ ] **res/values-en/strings.xml** (Англійська)
  - [ ] Переклади всіх рядків

- [ ] **res/values-de/strings.xml** (опціонально)

### 7.2 CurrencyFormatter

- [ ] Правильне форматування:
  ```
  UAH: ₴1,234.56
  USD: $1,234.56
  EUR: €1,234.56
  ```

### 7.3 Динамічна зміна мови (Runtime)

- [ ] SettingsViewModel.setLanguage()
  - [ ] Зберегти у DataStore
  - [ ] Рекреувати Activity

---

## 📊 ФАЗА 8: Утиліти & Розрахунки

### 8.1 FuelCalculator

- [ ] **FuelCalculator.kt**
  - [ ] calculateConsumption(distance, fuelUsed): Double
    - Формула: (fuelUsed / distance) * 100
  - [ ] calculateCostPerKm(totalCost, distance): Double
    - Формула: totalCost / distance
  - [ ] calculateFuelMetrics(fuelLogs): FuelMetrics

### 8.2 DateFormatter

- [ ] **DateFormatter.kt**
  - [ ] formatDate(timestamp): String (dd.MM.yyyy)
  - [ ] formatMonth(year, month): String (назва місяця)
  - [ ] getMonthBoundaries(year, month): Pair<Long, Long>

### 8.3 Constants

- [ ] **Constants.kt**
  - [ ] DATABASE_NAME = "drivecosts.db"
  - [ ] CURRENCY_CODES = listOf("UAH", "USD", "EUR")
  - [ ] FUEL_TYPES = listOf("PETROL", "DIESEL", "LPG", "ELECTRIC")

---

## 🔐 ФАЗА 9: Безпека & Підпис

### 9.1 Keystore Setup

- [ ] Генеровано keystore:
  ```bash
  keytool -genkey -v -keystore ~/.android/drivecosts.keystore \
    -keyalg RSA -keysize 2048 -validity 10000 -alias drivecosts_key
  ```

- [ ] Збережені паролі в безпечному місці

### 9.2 Gradle Signing Config

- [ ] Створено `keystore.properties` (НЕ в git)
  - [ ] storeFile
  - [ ] storePassword
  - [ ] keyAlias
  - [ ] keyPassword

- [ ] Оновлено `app/build.gradle.kts`:
  - [ ] signingConfigs.release
  - [ ] buildTypes.release.signingConfig

### 9.3 .gitignore

- [ ] keystore.properties
- [ ] *.keystore
- [ ] build/ папка

---

## 📦 ФАЗА 10: Build & Version Management

### 10.1 gradle.properties

- [ ] Встановлено початкову версію:
  ```
  VERSION_MAJOR=1
  VERSION_MINOR=0
  VERSION_PATCH=0
  VERSION_NAME=1.0.0
  VERSION_CODE=10000
  ```

### 10.2 Version Bump Script

- [ ] Створено `version-bump.sh`
- [ ] chmod +x version-bump.sh
- [ ] Тестирование:
  ```bash
  ./version-bump.sh patch  # 1.0.0 → 1.0.1
  ```

### 10.3 app/build.gradle.kts Version Integration

- [ ] Читання з gradle.properties:
  ```kotlin
  versionName = "$vMajor.$vMinor.$vPatch"
  versionCode = vMajor * 10000 + vMinor * 100 + vPatch
  ```

---

## 🚀 ФАЗА 11: GitHub & CI/CD

### 11.1 GitHub Setup

- [ ] Інціалізовано git репозиторій
- [ ] Створено GitHub репозиторій
- [ ] Додано remote:
  ```bash
  git remote add origin https://github.com/YOUR_USERNAME/drivecosts.git
  ```

### 11.2 GitHub Actions Workflow

- [ ] Створено `.github/workflows/build.yml`
- [ ] Конфіг включає:
  - [ ] Trigger на push main tag
  - [ ] Setup JDK 17
  - [ ] Build Release APK
  - [ ] Create GitHub Release
  - [ ] Upload APK file

### 11.3 GitHub Secrets

- [ ] Додано:
  - [ ] KEYSTORE_BASE64 (base64 keystore)
  - [ ] KEYSTORE_PASSWORD
  - [ ] KEY_ALIAS
  - [ ] KEY_PASSWORD

Генерування base64:
```bash
cat ~/.android/drivecosts.keystore | base64 > keystore.b64
# Скопіювати вміст у GitHub Secrets
```

---

## 📖 ФАЗА 12: Документація

### 12.1 Основна документація

- [ ] **README.md**
  - [ ] Опис функціональності
  - [ ] Швидкий старт
  - [ ] Структура проєкту
  - [ ] Дорожна карта

- [ ] **BUILD_MANUAL.md**
  - [ ] Детальний мануал збирання APK
  - [ ] Налаштування Google Play Console
  - [ ] Troubleshooting

- [ ] **ARCHITECTURE.md**
  - [ ] Архітектура системи
  - [ ] Шари (MVVM, Clean)
  - [ ] Дизайн рішення

- [ ] **DATABASE_SCHEMA.md**
  - [ ] ERD діаграма
  - [ ] Опис таблиць
  - [ ] Індекси та відносини

### 12.2 Коментарі у коді

- [ ] Документовані всі public методи (KDoc)
- [ ] Комплексні логіки мають поясненні коментарі

---

## ✅ ФАЗА 13: Тестування

### 13.1 Unit Tests

- [ ] **CarRepositoryTest.kt**
  - [ ] testInsertAndRetrieveCar()
  - [ ] testDeleteCar()

- [ ] **FuelCalculatorTest.kt**
  - [ ] testCalculateConsumption()
  - [ ] testCalculateCostPerKm()

- [ ] **DateFormatterTest.kt**
  - [ ] testFormatDate()
  - [ ] testGetMonthBoundaries()

### 13.2 UI Tests

- [ ] **HomeScreenTest.kt**
  - [ ] testDisplaysCars()
  - [ ] testNavigateToAddCar()

- [ ] **AddFuelScreenTest.kt**
  - [ ] testValidationError()
  - [ ] testSuccessfulAddition()

### 13.3 Manual Testing

- [ ] Інсталяція на емулятор
- [ ] Тестування всіх екранів
- [ ] Тестування офлайн (відключити WiFi)
- [ ] Тестування нагадувань

---

## 📱 ФАЗА 14: Google Play Preparation

### 14.1 Google Play Console

- [ ] Зареєстровано акаунт розробника ($25)
- [ ] Створено додаток

### 14.2 Store Listing

- [ ] **Main store listing:**
  - [ ] Icon (512x512)
  - [ ] Feature graphic (1024x500)
  - [ ] Screenshots (min 2, max 8)
  - [ ] Short description
  - [ ] Full description

- [ ] **Content rating:**
  - [ ] Заповнено questionnaire

- [ ] **Pricing & distribution:**
  - [ ] Price: Free
  - [ ] Countries: Select all

### 14.3 App Releases

- [ ] **Production:**
  - [ ] Завантажено app-release.apk
  - [ ] Version name: 1.0.0
  - [ ] Release notes: "Initial release"

---

## 🎉 ФАЗА 15: Launch!

### 15.1 Pre-Launch Checklist

- [ ] Всі функції реалізовані ✅
- [ ] Тестовано на емуляторі ✅
- [ ] Тестовано на реальному пристрої ✅
- [ ] APK підписаний ✅
- [ ] Version відповідає 1.0.0 ✅
- [ ] Документація полна ✅
- [ ] GitHub репозиторій готовий ✅

### 15.2 Release Процес

```bash
# 1. Фіналізація версії
./version-bump.sh minor  # 1.0.0 → 1.1.0

# 2. Збір Release APK
./gradlew clean assembleRelease

# 3. Тестування APK на пристрої
adb install -r app/build/outputs/apk/release/app-release.apk

# 4. Git commit & tag
git add .
git commit -m "feat: release v1.0.0"
git tag -a v1.0.0 -m "Release version 1.0.0"

# 5. Push на GitHub
git push origin main --tags

# 6. GitHub Actions буде автоматично:
#    - Зібрати APK
#    - Підписати APK
#    - Створити Release на GitHub
#    - Залити APK у Releases

# 7. Завантажити у Google Play Console (вручну)
#    Скопіювати app-release.apk з GitHub Releases
#    Завантажити у Google Play Console → Production
#    Натиснути "Publish"

# 8. Чекати модерацію (2-4 години)

# 9. Святкувати! 🎉
```

### 15.3 Post-Launch

- [ ] Моніторити Google Play Console
- [ ] Читати user reviews
- [ ] Фіксити баги швидко
- [ ] Планувати v1.1.0

---

## 📋 Заключне повідомлення

```
✅ Проєкт готовий до публікації у Google Play!

📊 Метрики реалізації:
├─ Архітектура: MVVM + Clean ✅
├─ Database: Room + SQLite ✅
├─ UI: Jetpack Compose ✅
├─ DI: Hilt ✅
├─ Notifications: WorkManager ✅
├─ Мультивалютність ✅
├─ Багатомовність ✅
├─ CI/CD: GitHub Actions ✅
├─ Версіонування: Semantic ✅
└─ Документація: Повна ✅

🚀 Готовий до старту!
```

---

**Версія чек-ліста**: 1.0.0  
**Дата створення**: Січень 2024  
**Статус**: ✅ Ready for Development
