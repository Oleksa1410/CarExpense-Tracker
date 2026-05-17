# 🚗 DriveCosts

**Контролюйте витрати на паливо та ремонт автомобіля прямо у вашому телефоні**

![Android](https://img.shields.io/badge/Android-11%2B-green)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue)
![License](https://img.shields.io/badge/License-GPL--3.0-blue)
![Version](https://img.shields.io/badge/Version-1.0.0-success)

---

## 📱 Функціональність

✅ **Облік палива**
- Ручне введення витрат палива
- Розрахунок витрати л/100км
- Історія заповнень по датам

✅ **Контроль витрат**
- Ремонт та запчастини
- Страхування
- ТО і техогляд

✅ **Звітність**
- Місячні звіти
- Вартість на км
- Порівняння по місяцях
- Експорт дані (планується)

✅ **Нагадування**
- Push-сповіщення про ТО
- Контроль страхування
- Техогляд та інші сервіси

✅ **Мультивалютність**
- UAH (українська гривня)
- USD (доларус США)
- EUR (євро)
- Легко додавати нові валюти

✅ **Багатомовність**
- 🇺🇦 Українська (за замовчуванням)
- 🇬🇧 Англійська

✅ **Множини автомобілів**
- Підтримка декількох автомобілів в одному акаунті
- Окремі звіти для кожного

✅ **100% Офлайн**
- Працює без інтернету
- Всі дані зберігаються локально у БД
- Без обов'язкової реєстрації

✅ **Абсолютно безплатно**
- Без прихованих платежів
- Бонус: мінімальна реклама (AdMob)

---

## 🚀 Швидкий Старт

### Вимоги
- Android Studio 2023.2+
- JDK 17+
- Мінімум 2 GB RAM для збирання

### Встановлення

```bash
# 1. Клонування репозиторію
git clone https://github.com/YOUR_USERNAME/drivecosts.git
cd drivecosts

# 2. Синхронізація Gradle
./gradlew sync

# 3. Установка на емулятор або телефон
./gradlew installDebug

# 4. Запуск (у Android Studio)
# Run → Run 'app'
```

### Збирання Release APK

```bash
# Бета: auto-bump version та збір APK
./version-bump.sh patch
./gradlew assembleRelease

# Результат: app/build/outputs/apk/release/app-release.apk
```

### Публікація на Google Play

Див. детальний мануал: [BUILD_MANUAL.md](./docs/BUILD_MANUAL.md)

---

## 📁 Структура Проєкту

```
drivecosts/
├── app/
│   ├── src/main/java/com/drivecosts/
│   │   ├── presentation/         # UI Compose screens & ViewModels
│   │   ├── domain/              # Business models & interfaces
│   │   ├── data/                # DB, repositories, local storage
│   │   └── di/                  # Hilt dependency injection
│   ├── src/main/res/            # Resources (strings, colors, layouts)
│   └── build.gradle.kts         # App configuration
├── docs/
│   ├── BUILD_MANUAL.md          # Детальний мануал для розробника
│   ├── ARCHITECTURE.md          # Архітектура системи
│   └── DATABASE_SCHEMA.md       # Схема БД
├── gradle.properties            # Version control (MAJOR.MINOR.PATCH)
├── .github/workflows/
│   └── build.yml               # GitHub Actions CI/CD
└── README.md                   # Цей файл
```

---

## 🏗️ Архітектура

```
PRESENTATION LAYER (Compose UI)
        ↓ observes
DOMAIN LAYER (Business Logic)
        ↓ uses
DATA LAYER (Room Database)
```

- **MVVM Pattern**: ViewModel + StateFlow
- **Clean Architecture**: Розділення відповідальності
- **Hilt DI**: Dependency Injection
- **Room Database**: SQLite для офлайн
- **WorkManager**: Нагадування & фонові завдання

Детальніше: [ARCHITECTURE.md](./docs/ARCHITECTURE.md)

---

## 🗄️ База Даних

### Таблиці

| Таблиця | Опис |
|---------|------|
| `cars` | Автомобілі |
| `fuel_logs` | Журнал заповнень палива |
| `expenses` | Витрати на ремонт |
| `reminders` | Нагадування |

### Схема

```sql
CARS (1:N) ─── FUEL_LOGS
         ├─── EXPENSES
         └─── REMINDERS
```

Детальніше: [DATABASE_SCHEMA.md](./docs/DATABASE_SCHEMA.md)

---

## 🔄 Версіонування

Версія: **MAJOR.MINOR.PATCH** (семантичне версіонування)

Апдейт версії перед кожною публікацією:

```bash
./version-bump.sh patch    # 1.0.0 → 1.0.1 (баг-фіксі)
./version-bump.sh minor    # 1.0.1 → 1.1.0 (нові функції)
./version-bump.sh major    # 1.1.0 → 2.0.0 (breaking changes)
```

Версія автоматично читається з `gradle.properties` при збиранні APK.

---

## 🤖 CI/CD (GitHub Actions)

Автоматичний build та публікація при push в main:

```bash
# Запуск workflow:
git tag v1.0.1
git push origin main --tags

# GitHub Actions автоматично:
# 1. Синхронізує deps
# 2. Будує Release APK
# 3.창Підписує (keystore)
# 4. Завантажує на Releases
```

Конфіг: [`.github/workflows/build.yml`](./.github/workflows/build.yml)

---

## 🔐 Безпека

### Підпис APK

Додаток підписується з `drivecosts.keystore` для публікації на Google Play.

```bash
# Перевірити підпис
keytool -printcert -jarfile app-release.apk
```

⚠️ **Keystore пароль**: зберігайте у безпечному місці!

### DataStore

Чутливі налаштування (мова, валюта) зберігаються у зашифрованому DataStore.

### Офлайн

Всі дані зберігаються локально. Синхронізація з хмарою - добавляється пізніше, якщо потрібна.

---

## 📊 Звітність

### Функціональність

- **Місячна звітність**: витрати по місяцях
- **Аналітика палива**: л/100км, вартість за км
- **Графіки**: тренди витрат
- **Експорт** (планується): CSV, PDF

### Приклад Звіту

```
═══════════════════════════════════
  Звіт за Березень 2024
═══════════════════════════════════

🚗 Автомобіль: Toyota Camry 2015

📊 Паливо:
  Заповнено: 8 разів
  Витрачено: 45 л
  Дистанція: 600 км
  Середня витрата: 7.5 л/100км
  Загальна вартість: ₴2,700.00
  Вартість за км: ₴4.50

🔧 Витрати:
  ТО: ₴500
  Запчастини: ₴1,200
  Інше: ₴100
  Всього: ₴1,800

💰 РАЗОМ ЗА МІСЯЦЬ: ₴4,500
```

---

## 🎯 Дорожна карта (Roadmap)

### v1.0.0 (Поточна версія) ✅
- [x] MVVM + Clean Architecture
- [x] Room Database
- [x] Compose UI
- [x] Множини автомобілів
- [x] Облік палива
- [x] Контроль витрат
- [x] Нагадування (WorkManager)
- [x] Мультивалютність
- [x] Багатомовність (UK/EN)
- [x] 100% Офлайн

### v1.1.0 (Планується)
- [ ] Графіки й діаграми
- [ ] Експорт у CSV/PDF
- [ ] Темна тема (Dark Mode)
- [ ] Фотографії чеків
- [ ] Резервна копія локально

### v2.0.0 (Майбутньо)
- [ ] Cloud Sync (Firebase)
- [ ] Синхронізація між пристроями
- [ ] Google Auth
- [ ] Сімейний акаунт
- [ ] Аналітика в режимі реального часу

---

## 🛠️ Розробка

### Встановлення DevTools

```bash
# Синхронізація dependencies
./gradlew sync

# Запуск тестів
./gradlew test

# Запуск UI тестів (потрібен емулятор)
./gradlew connectedAndroidTest

# Статичний аналіз (lint)
./gradlew lint
```

### Код Style

- **Kotlin** 1.9.20
- **Jetpack Compose** для UI
- **SOLID** принципи
- **DRY** (Don't Repeat Yourself)

### Версіонування Коду

Версія автоматично оновлюється у:
- `build.gradle.kts` → versionName, versionCode
- `BuildConfig.java` → VERSION_NAME
- Google Play Console → App version

---

## 🐛 Баг-Репорти

Знайшли баг? Зв'яжіться з розробником або відкрийте Issue у GitHub:

```
https://github.com/YOUR_USERNAME/drivecosts/issues
```

### Формат баг-репорту

```
Title: [BUG] Коротко описання проблеми

Опис:
- Що сталося?
- Як це воспроизвести?
- Очікуваний результат?

Device:
- Android версія
- Модель телефону
- Версія додатку
```

---

## 📄 Ліцензія

Проєкт розповсюджується під ліцензією **GPL-3.0**.

Ви можете:
- ✅ Використовувати
- ✅ Модифікувати
- ✅ Розповсюджувати

З умовою:
- 📝 Надавати копію ліцензії
- 📝 Описувати внесені зміни
- 📝 Розповсюджувати на тих же умовах

Детальніше: [LICENSE](./LICENSE)

---

## 📚 Документація

- **[BUILD_MANUAL.md](./docs/BUILD_MANUAL.md)** - Детальний гайд збирання APK для публікації
- **[ARCHITECTURE.md](./docs/ARCHITECTURE.md)** - Архітектура системи та проєктування
- **[DATABASE_SCHEMA.md](./docs/DATABASE_SCHEMA.md)** - Схема бази даних

---

## 📞 Контакти

- **Email**: dev@drivecosts.app
- **GitHub**: https://github.com/YOUR_USERNAME/drivecosts
- **Issue Tracker**: https://github.com/YOUR_USERNAME/drivecosts/issues

---

## 🙏 Подяка

Дякуємо за використання DriveCosts! 

Якщо вам подобається додаток, поділіться ним з друзями та залиште рецензію у Google Play.

---

**DriveCosts** © 2024  
**Розробник**: Solo Developer  
**Версія**: 1.0.0  
**Остання оновлення**: Січень 2024

---

## 🎯 Quick Links

| Посилання | Опис |
|-----------|------|
| [Android Studio](https://developer.android.com/studio) | IDE для розробки |
| [Google Play Console](https://play.google.com/console) | Публікація |
| [Kotlin Docs](https://kotlinlang.org/docs) | Документація мови |
| [Jetpack Docs](https://developer.android.com/jetpack) | Jetpack components |
| [Room Docs](https://developer.android.com/training/data-storage/room) | Room Database |

---

**Happy coding! 🚀**
