# 📱 DriveCosts - Детальний Мануал Збирання APK

**Для: Solo-Developer**  
**Версія: 1.0.0**  
**Дата: 2024**

---

## 📋 ПЕРЕДУМОВИ

### 1.1 Установити необхідне ПО

#### Windows / macOS / Linux

1. **Android Studio** (остання версія)
   - Завантажити з: https://developer.android.com/studio
   - Під час установки обрати:
     - ✅ Android SDK
     - ✅ Android SDK Platform-Tools
     - ✅ Android Emulator
     - ✅ NDK (опціонально)

2. **Java Development Kit (JDK) 17+**
   - Установити автоматично через Android Studio
   - Або вручну з: https://www.oracle.com/java/technologies/downloads/

3. **Git** (версіонування)
   - Windows: https://git-scm.com/download/win
   - macOS: `brew install git`
   - Linux: `sudo apt install git`

### 1.2 Перевірити установку

```bash
# Перевірити Java
java -version
# Очікуваний результат: openjdk 17.x.x або вище

# Перевірити Android SDK
echo $ANDROID_SDK_ROOT
# На Windows: C:\Users\YourUser\AppData\Local\Android\Sdk
# На macOS: ~/Library/Android/sdk
# На Linux: ~/Android/Sdk
```

---

## 🔐 2. НАЛАШТУВАННЯ ПІДПИСУ (SIGNING)

**КРИТИЧНО для Google Play публікації!**

### 2.1 Створити Keystore (один раз)

```bash
# На Windows (Command Prompt як Administrator)
keytool -genkey -v -keystore %USERPROFILE%\.android\drivecosts.keystore ^
  -keyalg RSA -keysize 2048 -validity 10000 ^
  -alias drivecosts_key

# На macOS / Linux (Terminal)
keytool -genkey -v -keystore ~/.android/drivecosts.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias drivecosts_key
```

**Коли запросить інформацію:**
```
Keystore password: [введи складний пароль, збережи його!]
First and last name: Your Name
Organizational unit: Mobile Apps
Organization: Your Company
City or Locality: Kyiv
State or Province: Kyiv
Country code: UA
```

**⚠️ ВАЖЛИВО: Збережи цей пароль у безпечному місці!**

### 2.2 Налаштувати Gradle Signing

Створи файл `keystore.properties` у корені проєкту:

```properties
# keystore.properties
storeFile=~/.android/drivecosts.keystore
storePassword=ТВЙ_ПАРОЛЬ_KEYSTORE
keyAlias=drivecosts_key
keyPassword=ТВЙ_ПАРОЛЬ_КЛЮЧА
```

**⚠️ ВАЖЛИВО: Додай `keystore.properties` у `.gitignore`!**

```bash
echo "keystore.properties" >> .gitignore
```

### 2.3 Оновити `app/build.gradle.kts`

```kotlin
android {
    // ... інший конфіг ...

    signingConfigs {
        release {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            val keystoreProperties = Properties()
            keystoreProperties.load(keystorePropertiesFile.inputStream())

            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

---

## 📦 3. ПОДГОТОВКА КОДУ

### 3.1 Клонування репозиторію

```bash
git clone https://github.com/YOUR_USERNAME/drivecosts.git
cd drivecosts
```

### 3.2 Налаштувати AdMob Keys

1. Відкрий `gradle.properties`
2. Заповни свої AdMob ID:

```properties
ADMOB_APP_ID=ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy
ADMOB_BANNER_ID=ca-app-pub-3940256099942544/6300978111
ADMOB_INTERSTITIAL_ID=ca-app-pub-3940256099942544/1033173712
```

**Де знайти:**
- Зайти на https://admob.google.com
- Додати додаток
- Скопіювати ID з Dashboard

### 3.3 Синхронізувати Gradle

```bash
# Windows
gradlew sync

# macOS / Linux
./gradlew sync
```

Або у Android Studio: **File → Sync Now**

---

## 🔢 4. АПДЕЙТ ВЕРСІЇ

### 4.1 Використання Script

**Перед кожною публікацією:**

```bash
# Інкрементувати patch версію (1.0.0 → 1.0.1)
./version-bump.sh patch

# Інкрементувати minor (1.0.1 → 1.1.0)
./version-bump.sh minor

# Інкрементувати major (1.1.0 → 2.0.0)
./version-bump.sh major
```

### 4.2 Ручний апдейт

Відкрий `gradle.properties` та змініть:

```properties
VERSION_MAJOR=1
VERSION_MINOR=0
VERSION_PATCH=2
VERSION_NAME=1.0.2
VERSION_CODE=2
```

**VERSION_CODE = MAJOR * 10000 + MINOR * 100 + PATCH**

---

## 🏗️ 5. ЗБИРАННЯ APK

### 5.1 OPTION A: Через Android Studio (найлегше для новачків)

1. Відкрий проєкт у Android Studio
2. **Build → Build Bundle(s)/APK(s) → Build APK(s)**
3. Чекай ~2-5 хвилин
4. Натисни **Locate** для відкриття папки з APK

📂 Результат: `app/build/outputs/apk/release/app-release.apk`

### 5.2 OPTION B: Через Command Line (рекомендується для CI/CD)

```bash
# Windows
gradlew assembleRelease

# macOS / Linux
./gradlew assembleRelease
```

**Результат:**
```
✅ BUILD SUCCESSFUL
app/build/outputs/apk/release/app-release.apk
```

### 5.3 OPTION C: Через GitHub Actions (автоматично)

1. Запушити код на GitHub:
```bash
git add .
git commit -m "feat: initial release v1.0.0"
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin main --tags
```

2. Перейти на GitHub → **Actions**
3. Спостерігати за build процесом
4. Завантажити готовий APK з Releases

---

## ✅ 6. ПЕРЕВІРКА APK ПЕРЕД ПУБЛІКАЦІЄЮ

### 6.1 Підпис APK

```bash
# Windows
keytool -printcert -jarfile app\build\outputs\apk\release\app-release.apk

# macOS / Linux
keytool -printcert -jarfile app/build/outputs/apk/release/app-release.apk
```

**Очікуваний результат:**
```
Owner: CN=Your Name, OU=Mobile Apps, O=Your Company, L=Kyiv, ST=Kyiv, C=UA
Issuer: CN=Your Name, OU=Mobile Apps, O=Your Company, L=Kyiv, ST=Kyiv, C=UA
```

### 6.2 Розмір APK

```bash
# Розмір не повинен перевищувати 100 MB
ls -lh app/build/outputs/apk/release/app-release.apk
# Очікуваний результат: ~15-30 MB
```

### 6.3 Тестування на пристрої

```bash
# Установити APK на підключений пристрій/емулятор
adb install -r app/build/outputs/apk/release/app-release.apk

# Або через Android Studio:
# Run → Select Device → app
```

---

## 📱 7. ПУБЛІКАЦІЯ НА GOOGLE PLAY

### 7.1 Підготовка Google Play Console

1. Перейти на https://play.google.com/console
2. Створити новий додаток:
   - **Create app**
   - **App name**: DriveCosts
   - **Default language**: Ukrainian (Українська)
   - **App type**: Application
   - **Category**: Lifestyle
   - **Content rating**: заповнити questionnaire

### 7.2 Заповнити Listing

**Store Listing → Main store listing:**

- **Short description**: "Контролюйте витрати на паливо та ремонт автомобіля"
- **Full description**: 
  ```
  DriveCosts - це простий та зручний додаток для контролю витрат на пальне, 
  ремонт та обслуговування автомобіля.
  
  ✅ Функціональність:
  • Облік витрат палива з розрахунком витрати (л/100км)
  • Контроль витрат на ремонт і запчастини
  • Нагадування про ТО, страхування, техогляд
  • Push-сповіщення про найменування
  • Звітність по місяцях
  • Поддержка декількох автомобілів
  • Мультивалютність (UAH, USD, EUR)
  • Багатомовність (Українська, Англійська)
  • 100% Офлайн - працює без інтернету
  • Абсолютно безплатно!
  ```

- **Screenshots** (мін. 2, макс. 8):
  - Home Screen
  - Add Fuel Screen
  - Monthly Report
  - Reminders

- **Feature graphic** (1024x500 px)

### 7.3 Завантажити APK

**App releases → Production:**

1. **Create new release**
2. **Upload APK** → Select `app-release.apk`
3. **App version name**: 1.0.0
4. **Release notes**: "Initial release"
5. **Save and review**

### 7.4 Заповнити Questionnaire

**Policies → App content:**

- Content rating questionnaire
- Privacy policy URL
- Terms of service (якщо є)

**App permissions:**
- Camera: No
- Location: No
- Contacts: No

### 7.5 Вартість

**Pricing & Distribution:**
- **Price**: Free
- **Countries**: Select all (або вибрати вибірково)

### 7.6 SUBMIT FOR REVIEW

1. Переконатися що все заповнено (зелені галочки)
2. **Review and publish**
3. Натиснути **Publish** 🚀

**Очікуваний час модерації:** 2-4 години (ночами до 12+ годин)

---

## 🔄 8. ОНОВЛЕННЯ ДОДАТКУ

### 8.1 Налаштування нової версії

```bash
# Інкрементувати patch
./version-bump.sh patch

# Результат: 1.0.0 → 1.0.1

git add gradle.properties
git commit -m "chore: bump version to 1.0.1"
```

### 8.2 Зібрати новий APK

```bash
./gradlew clean assembleRelease
```

### 8.3 Завантажити в Google Play

1. Google Play Console → Production
2. **Create new release**
3. Upload нового APK
4. Write release notes
5. **Publish**

---

## 🐛 9. TROUBLESHOOTING

### Помилка: "Gradle sync failed"

```bash
# Рішення:
./gradlew clean
./gradlew sync
```

### Помилка: "Keystore not found"

```bash
# Переконатися що keystore.properties існує
# та шляхи правильні
cat keystore.properties
```

### Помилка: "Version code must be higher than X"

- Кожна нова версія повинна мати більший VERSION_CODE
- Видалити старі APK з Google Play перед повторною публікацією

### Помилка: "App not optimized for tablets"

- Додати в `AndroidManifest.xml`:
```xml
<uses-feature android:name="android.hardware.phone" android:required="false" />
```

---

## 📊 10. ВЕРСІОНУВАННЯ ДЕМОНСТРАЦІЯ

```bash
# Стан 1: Початкова версія
VERSION_MAJOR=1
VERSION_MINOR=0
VERSION_PATCH=0
VERSION_CODE=10000  # 1*10000 + 0*100 + 0

# Після першого баг-фіксу
./version-bump.sh patch
VERSION_MAJOR=1
VERSION_MINOR=0
VERSION_PATCH=1
VERSION_CODE=10001

# Після додання нової функції
./version-bump.sh minor
VERSION_MAJOR=1
VERSION_MINOR=1
VERSION_PATCH=0
VERSION_CODE=10100

# Після повного редизайну
./version-bump.sh major
VERSION_MAJOR=2
VERSION_MINOR=0
VERSION_PATCH=0
VERSION_CODE=20000
```

---

## 📈 11. CONTINUOUS INTEGRATION (опціонально)

Якщо налаштовано GitHub Actions:

```bash
# При кожному push з тегом версії
git tag v1.0.1
git push origin main --tags

# GitHub Actions автоматично:
# 1. Синхронізує dependencies
# 2. Компілює проєкт
# 3. Будує Release APK
# 4. Завантажує на GitHub Releases
# 5. Відправляє notification (опціонально)
```

---

## 🎯 QUICK START ЧЕК-ЛИСТ

- [ ] Установлено Android Studio + JDK 17
- [ ] Клоновано проєкт з GitHub
- [ ] Створено keystore.properties
- [ ] Налаштовано AdMob ID в gradle.properties
- [ ] Виконано `./gradlew sync`
- [ ] Встановлено version у gradle.properties
- [ ] Зібрано APK: `./gradlew assembleRelease`
- [ ] Підписано APK правильно (перевірено keytool)
- [ ] Тестовано на пристрої/емуляторі
- [ ] Створено Google Play Console акаунт
- [ ] Завантажено перший APK на Google Play
- [ ] Опубліковано додаток 🚀

---

## 📞 КОНТАКТИ ПІДТРИМКИ

- **Android Developer Docs**: https://developer.android.com/docs
- **Google Play Console Help**: https://support.google.com/googleplay
- **Kotlin Documentation**: https://kotlinlang.org/docs
- **Android Jetpack**: https://developer.android.com/jetpack

---

**Версія документу: 1.0.0**  
**Остання оновлення: 2024**
