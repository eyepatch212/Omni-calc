# OmniCalc 🧮 🔢

**OmniCalc** is a modern, versatile Android calculator and conversion application built using Kotlin and Material You design guidelines. It combines standard arithmetic, complex scientific evaluations, calculation history tracking, and dynamic multi-unit converters into a single unified utility.

---

## ✨ Features

### 🧮 Calculator Modes
* **Basic Calculator:** Quick daily calculations, clean layout, essential arithmetic operations, and clear actions.
* **Scientific Calculator:** Trigonometric functions ($\sin$, $\cos$, $\tan$), natural ($\ln$) and base-10 ($\log$) logarithms, exponential powers ($e^x$, $10^x$, $x^y$), factorials ($n!$), and mathematical constants ($\pi$, $e$).
* **Calculation History:** Persisted calculation log with past inputs and results for instant recall.

### 🔄 Converters
* **Dynamic Currency Converter:** Live exchange rate conversions across global currencies.
* **Unit Conversions:** Length, Mass, Area, Volume, Temperature, Speed, Time, Molarity, and Numeral Systems (Binary, Octal, Decimal, Hexadecimal).
* **Financial & Health Calculators:** Mortgage Calculator, Income Tax Estimator, and Body Mass Index (BMI).

### 🎨 Design & Experience
* **Material You (Material 3):** Full dynamic color theming based on Android wallpaper and system palette preferences.
* **Adaptive UI:** Responsive layout supporting portrait, landscape, and foldable screen orientations.
* **Haptic Feedback:** Tactile responses for keypresses and operation triggers.

---

## 📥 Download & Installation

### Option 1: Direct Download (GitHub Releases)
1. Go to the **Releases** tab on this repository.
2. Under the latest release, expand the **Assets** section.
3. Tap `app-debug.apk` to download directly to your Android device.
4. Open the downloaded file and select **Install** (enable *"Allow from this source"* if prompted).

### Option 2: Automated CI/CD Artifacts
Every commit pushed to `main` triggers an automatic GitHub Actions build:
1. Navigate to the **Actions** tab.
2. Select the latest successful workflow run.
3. Scroll down to **Artifacts** and download `app-debug`.
4. Extract the `.zip` archive and install the extracted `.apk` file.

---

## 🛠️ Tech Stack & Architecture

* **Language:** Kotlin
* **UI Framework:** Android Jetpack / Material Components 3 (Material You)
* **Architecture Pattern:** MVVM (Model-View-ViewModel)
* **Database & Storage:** Android Room / SQLite & DataStore Preferences
* **Build System:** Gradle (AGP 9.1.x / Gradle 9.3+)
* **CI/CD:** GitHub Actions (Automated headless building via `xvfb-run` and artifact publishing)

---

## 🚀 Building from Source

### Prerequisites
* Android Studio (Ladybug / Iguana or newer recommended)
* JDK: OpenJDK 17 or 21
* Android SDK: API Level 34+ (Target), API Level 24+ (Minimum)

### Local Build Steps
1. Clone the repository:
   ```bash
   git clone [https://github.com/eyepatch212/Omni-calc.git](https://github.com/eyepatch212/Omni-calc.git)
   cd Omni-calc
