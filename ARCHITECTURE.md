# 🏛️ DriveCosts - Архітектура Додатку

## 1. Архітектурна схема

```
┌─────────────────────────────────────────────────────┐
│                  PRESENTATION LAYER                 │
│  (Jetpack Compose UI, ViewModels, Navigation)      │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────┴──────────────────────────────┐
│                    DOMAIN LAYER                     │
│  (Business Logic, Interfaces, Models)              │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────┴──────────────────────────────┐
│                     DATA LAYER                      │
│  (Room Database, Repositories, DataStore)          │
└─────────────────────────────────────────────────────┘
```

## 2.패терні проєктування

### 2.1 MVVM (Model-View-ViewModel)

```
View (Compose Screen)
        ↓ observes
ViewModel (StateFlow)
        ↓ uses
Repository Interface
        ↓ implements
Repository Implementation
        ↓ uses
Room Database & DataStore
```

**Приклад потоку:**
1. UI (Screen) виявляє дію користувача
2. Викликає функцію у ViewModel
3. ViewModel використовує Repository
4. Repository читає/пише дані з Room
5. Дані повертаються як StateFlow
6. UI автоматично перемальовується

### 2.2 Dependency Injection (Hilt)

```
@HiltViewModel
class FuelViewModel @Inject constructor(
    private val repo: FuelRepository  // Hilt вводить залежність
)

Hilt Container
    ├── Provide FuelRepository
    ├── Provide CarRepository
    └── Provide AppDatabase
```

### 2.3 Repository Pattern

```
Interface (Domain):
    FuelRepository {
        fun getFuelLogs(): Flow<List<FuelLog>>
        fun insertFuelLog(log: FuelLog)
    }

Implementation (Data):
    FuelRepositoryImpl {
        override fun getFuelLogs() = dao.getAllFuelLogs()
        override fun insertFuelLog(log) = dao.insert(log)
    }
```

## 3. Дизайн БД (Room)

### 3.1 ERD (Entity-Relationship Diagram)

```
┌─────────────────────┐
│       CARS          │
├─────────────────────┤
│ id (PK)             │
│ name                │
│ brand               │
│ model               │
│ year                │
│ licensePlate        │
│ currentOdometer     │
│ fuelType            │
│ currency            │
│ isActive            │
│ createdAt           │
│ updatedAt           │
└──────────┬──────────┘
           │ (1:N)
           │
    ┌──────┴──────┬─────────────────┐
    │             │                 │
    ▼             ▼                 ▼
┌──────────┐  ┌──────────┐   ┌──────────────┐
│FUEL_LOGS │  │EXPENSES  │   │REMINDERS     │
├──────────┤  ├──────────┤   ├──────────────┤
│id (PK)   │  │id (PK)   │   │id (PK)       │
│carId(FK) │  │carId(FK) │   │carId(FK)     │
│odometer  │  │category  │   │type          │
│amount    │  │amount    │   │title         │
│price     │  │date      │   │nextDate      │
│date      │  │desc      │   │isRepeat      │
└──────────┘  └──────────┘   └──────────────┘
```

### 3.2 Таблиці та Нормалізація

**CARS**
- Один до багатьох с FUEL_LOGS, EXPENSES, REMINDERS
- Гарантує цілісність при видаленні car (CASCADE DELETE)

**FUEL_LOGS**
- Безпосередньо пов'язана з CAR
- Індекси: carId, date для швидких запитів

**EXPENSES**
- Також до CAR
- Індекс: category для групування по типам витрат

**REMINDERS**
- Повторюючі нагадування через isRepeat + interval
- Індекс: nextDate для пошуку активних

## 4. Керування станом (State Management)

### 4.1 ViewModel + StateFlow

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val carRepository: CarRepository
) : ViewModel() {
    
    // State емітується як Flow
    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> = _cars.asStateFlow()
    
    // UI слідкує за цим Flow
    init {
        viewModelScope.launch {
            carRepository.getAllCars().collect { carList ->
                _cars.value = carList  // State оновлюється
            }
        }
    }
}

// У Compose:
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val cars by viewModel.cars.collectAsState()  // UI переспостерігає
    
    LazyColumn {
        items(cars) { car ->
            CarCard(car)  // Перемалюється при зміні
        }
    }
}
```

### 4.2 State класи (Data Classes)

```kotlin
data class ReportState(
    val isLoading: Boolean = false,
    val data: MonthlyReport? = null,
    val error: String? = null
)

// ViewModel
private val _reportState = MutableStateFlow(ReportState())
val reportState: StateFlow<ReportState> = _reportState

// UI обробляє всі стани
when (state.value) {
    is Loading -> ShowSpinner()
    is Success -> ShowReport(state.data)
    is Error -> ShowErrorMessage(state.error)
}
```

## 5. Безпека & Конфідеційність

### 5.1 Keystore & Signing

```
gradle.properties (НЕ в git, у .gitignore)
    ├── storePassword
    ├── keyPassword
    └── keyAlias

↓ використовується під час build

Release APK (підписаний сертифікатом)
    └── Може бути опубліковано у Google Play
```

### 5.2 DataStore для чутливих даних

```kotlin
// Замість SharedPreferences (небезпечне)
// Використовуємо DataStore з шифруванням

context.dataStore.data.map { preferences ->
    preferences[CURRENCY_KEY] ?: "UAH"
}
```

### 5.3 Room Encryption (опціонально)

```kotlin
// Якщо потрібна шифрована БД:
val db = Room.databaseBuilder(
    context,
    AppDatabase::class.java,
    "drivecosts.db"
)
    .openHelperFactory(
        FrameworkSQLCipherOpenHelperFactory()
    )
    .build()
```

## 6. Offline-First Архітектура

### 6.1 Локальне сховище як source of truth

```
┌─────────────────┐
│  Room Database  │ ← Source of Truth (локально)
└────────┬────────┘
         │
    ┌────┴─────────────────────────┐
    │  UI слідкує за змінами через │
    │  Flow (Room + StateFlow)      │
    └──────────────────────────────┘

Коли користувач додає дані:
1. Пишемо в Room
2. Room emits Flow
3. UI оновлюється
4. Синхронізація з хмарою (опціонально, пізніше)
```

### 6.2 Синхронізація даних (якщо буде реалізована)

```kotlin
// Тепер: Room-only
// Майбутньо: можна додати Retrofit + SyncWorker

class SyncWorker : CoroutineWorker() {
    override suspend fun doWork(): Result {
        return try {
            val unsyncedData = db.dao.getUnsyncedItems()
            
            unsyncedData.forEach { item ->
                api.uploadItem(item)  // Залежить від інтернету
                db.markAsSynced(item.id)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()  // Повторити пізніше
        }
    }
}
```

## 7. Мультивалютність

### 7.1 Архітектура

```
User обирає валюту в Settings
    ↓
SettingsDataStore зберігає вибір (UAH, USD, EUR)
    ↓
ViewModel читає з DataStore
    ↓
CurrencyFormatter форматує відображення

// CurrencyFormatter.kt
fun formatPrice(amount: Double, currency: String): String {
    return when (currency) {
        "UAH" -> "₴${"%.2f".format(amount)}"
        "USD" -> "\$${"%,2f".format(amount)}"
        "EUR" -> "€${"%,2f".format(amount)}"
        else -> "$amount"
    }
}
```

### 7.2 Зберігання у БД

```kotlin
// Кожен FuelLog містить валюту на момент запису
data class FuelLog(
    val id: String,
    val amount: Double,
    val currency: String,  // Зберігаємо валюту разом з сумою
    val date: Long
)

// Це дозволяє історично правильно відображати звіти
```

## 8. Багатомовність (Localization)

### 8.1 Структура ресурсів

```
res/
├── values/strings.xml          (Українська - за замовчуванням)
├── values-en/strings.xml       (Англійська)
├── values-de/strings.xml       (Німецька)
└── values-fr/strings.xml       (Французька)
```

### 8.2 Динамічна зміна мови (Runtime)

```kotlin
// SettingsViewModel
fun setLanguage(lang: String) {
    viewModelScope.launch {
        settingsDataStore.setLanguage(lang)
        // Перезавантажити ресурси
        recreateActivity()
    }
}

// Дефолтна реалізація (у комерційних додатках краще бібліотека):
fun getString(resId: Int): String {
    return context.getString(resId)  // Android автоматично читає з потрібної папки
}
```

## 9. AdMob Інтеграція

### 9.1 Архітектура AD мережі

```
Google AdMob Dashboard
    ├── App ID: ca-app-pub-xxxxx~yyyyy
    ├── Banner Ad Unit: ca-app-pub-xxxxx/zzzzz
    └── Interstitial Unit: ca-app-pub-xxxxx/aaaaa

↓ Configure в gradle.properties

↓ BuildConfig інжектує під час build

↓ Runtime: AdmobHelper ініціалізує MobileAds

↓ Compose вставляє AdView через AndroidView
```

### 9.2 Розташування реклам

```
HomeScreen: Bottom banner
ReportScreen: Top banner
AddFuelScreen: Interstitial перед збереженням (опціонально)
```

### 9.3 Контроль реклам (Admin Panel ідея)

```
Майбутньо: можна додати функцію у SettingsViewModel:

fun disableAds(email: String) {
    // Перевірити email у локальному JSON
    if (isAuthorized(email)) {
        saveSetting("ads_disabled", true)
        // Приховати всі AdView
    }
}
```

## 10. Build & Release Process

### 10.1 CI/CD Pipeline (GitHub Actions)

```
Developer pushes code
    ↓ (webhook)
GitHub Actions triggered
    ├── Checkout code
    ├── Setup JDK 17
    ├── Build Release APK
    ├── Run tests (opcional)
    ├── Upload to artifacts
    └── (Optional) Deploy to Google Play

Developer tags: git tag v1.0.1
    ↓
GitHub Actions создает Release
    └── Attach APK file
```

### 10.2 Версіонування

```
gradle.properties:
VERSION_MAJOR=1
VERSION_MINOR=0
VERSION_PATCH=3

VERSION_CODE = MAJOR * 10000 + MINOR * 100 + PATCH
             = 1 * 10000 + 0 * 100 + 3
             = 10003

Google Play вимагає: VERSION_CODE > попередня версія
```

## 11. Тестування (Unit & UI)

### 11.1 Unit Tests (JUnit)

```kotlin
// RepositoryTest.kt
@RunWith(RobolectricTestRunner::class)
class FuelRepositoryTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @Test
    fun testInsertAndRetrieveFuelLog() = runTest {
        val fuelLog = FuelLog(...)
        repository.insertFuelLog(fuelLog)
        
        val result = repository.getFuelLogs()
        assertTrue(result.contains(fuelLog))
    }
}
```

### 11.2 UI Tests (Compose)

```kotlin
// HomeScreenTest.kt
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    
    @get:Rule
    val composeRule = createComposeRule()
    
    @Test
    fun testDisplaysCars() {
        val cars = listOf(Car(...), Car(...))
        
        composeRule.setContent {
            HomeScreen(viewModel)
        }
        
        composeRule.onNodeWithText("Add Car").assertExists()
    }
}
```

## 12. Performance Optimization

### 12.1 Database Queries

```kotlin
// Дефолтно: N+1 problem
cars.forEach { car ->
    val logs = fuelRepository.getFuelLogs(car.id)  // Запит для кожного car ❌
}

// Правильно: Single query з JOIN
@Query("""
    SELECT cars.*, COUNT(fuel_logs.id) as logCount
    FROM cars
    LEFT JOIN fuel_logs ON cars.id = fuel_logs.carId
    GROUP BY cars.id
""")
fun getCarsWithLogCount(): Flow<List<CarWithLogCount>>

// Atau: separate flows
cars.collect { carList ->
    fuelLogs.collect { logs ->  // Один запит для всіх ❓
        // Process both
    }
}
```

### 12.2 Memory Management

```kotlin
// ViewModel lifecycle
viewModelScope → відмовляє при закритті фрагменту ✅

// Avoid:
GlobalScope.launch { }  // Never dies ❌

// Compose recomposition
@Composable
fun MyScreen() {
    val state by viewModel.state.collectAsState()  // Правильно
    val state = viewModel.state.value              // Неправильно (не реакційно)
}
```

---

**Версія: 1.0.0**  
**Архітектор: Senior Full-Stack Architect**
