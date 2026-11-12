# 🧮 Calculator

Moderni Android skaičiuotuvo programėlė, įkvėpta **Windows 11** standartinio skaičiuotuvo dizaino. Sukurta naudojant **Jetpack Compose**, **Material 3** ir **BigDecimal** tikslią aritmetiką.

## ✨ Funkcijos

| Kategorija | Galimybės |
|---|---|
| **Aritmetika** | Sudėtis (`+`), atimtis (`−`), daugyba (`×`), dalyba (`÷`) su algebrine operacijų tvarka |
| **Vienetinės operacijos** | Kvadratas (`x²`), kvadratinė šaknis (`√x`), atvirkštinis skaičius (`1/x`), ženklo keitimas (`+/−`) |
| **Procentai** | Kontekstiniai procentai pagal Windows Calculator logiką (pvz., `200 + 10% = 220`) |
| **Tikslumas** | `BigDecimal` aritmetika – iki 16 skaitmenų be slankiojo kablelio paklaidų |
| **Istorija** | Skaičiavimų istorija su galimybe atkurti ankstesnį rezultatą |
| **Tema** | Windows 11 Fluent stilius, šviesi ir tamsi tema (pagal sistemos nustatymus) |
| **Lokalizacija** | Lietuvių ir anglų kalbos, lokalizuotas dešimtainis skyriklis (`,`) |
| **Prieinamumas** | TalkBack turinio aprašymai visiems mygtukams |

## 📋 Sistemos reikalavimai

| Parametras | Reikšmė |
|---|---|
| Minimali Android versija | 7.0 Nougat (API 24) |
| Tikslinė Android versija | API 37 |
| Kotlin | 2.2.10 |
| Android Gradle Plugin | 9.4.1 |
| Java | 11 |

## 🚀 Paleidimas

### Būtinosios sąlygos

- [Android Studio](https://developer.android.com/studio) (rekomenduojama Meerkat arba naujesnė versija)
- JDK 17 arba naujesnė
- Android SDK su API 37

### Žingsniai

1. **Klonuoti repozitoriją:**

   ```bash
   git clone <repozitorijos-url>
   cd Calculator
   ```

2. **Atidaryti projektą** Android Studio aplinkoje.

3. **Sinchronizuoti Gradle** – Android Studio automatiškai pasiūlys sinchronizuoti projektą.

4. **Paleisti programėlę:**
   - Pasirinkti emuliatorių arba prijungtą fizinį įrenginį.
   - Spausti **Run ▶** arba naudoti spartųjį klavišą `Shift + F10`.

### Paleidimas per komandinę eilutę

```bash
# Sukompiliuoti debug APK
./gradlew assembleDebug

# Paleisti testus
./gradlew test
```

## 🏗️ Architektūra

Projektas naudoja **Clean Architecture + MVVM** šabloną. Verslo logika visiškai atskirta nuo Android karkaso.

```
com.example.calculator
├── domain/                    # Verslo logika (grynas Kotlin, be Android priklausomybių)
│   ├── CalculatorEngine.kt    # Skaičiavimų variklis (BigDecimal, Shunting-Yard algoritmas)
│   ├── HistoryItem.kt         # Istorijos įrašo modelis
│   └── Operation.kt           # BinaryOperator ir UnaryOperator enum
├── ui/                        # Vartotojo sąsaja
│   ├── CalculatorScreen.kt    # Pagrindinis Compose ekranas (klaviatūra, ekranas, istorija)
│   ├── CalculatorViewModel.kt # ViewModel su StateFlow
│   └── theme/                 # Windows 11 stiliaus tema
│       ├── Color.kt           # Šviesi / tamsi spalvų paletė
│       └── Theme.kt           # Material 3 temos konfigūracija
└── MainActivity.kt            # Programėlės įeities taškas (Single-Activity)
```

### Komponentų aprašymas

| Komponentas | Atsakomybė |
|---|---|
| `CalculatorEngine` | Grynoji skaičiavimo logika: tokenizuotas išraiškų apdorojimas dviejų stekų (*Shunting-Yard*) algoritmu su algebrine operacijų tvarka. Naudoja `BigDecimal` tiksliai aritmetikai. |
| `CalculatorViewModel` | Tarpinis MVVM sluoksnis – perduoda UI įvykius varikliui ir eksponuoja `StateFlow<CalculatorUiState>` reaktyviai UI. |
| `CalculatorScreen` | Jetpack Compose UI: 6×4 mygtukų tinklelis, dinaminis šrifto dydis pagal teksto ilgį, istorijos `ModalBottomSheet`. |
| `BinaryOperator` | Enum su operatoriaus simboliu ir precedencija (`+` `−` = 1, `×` `÷` = 2). |
| `UnaryOperator` | Enum vienetinėms operacijoms: `SQUARE`, `SQRT`, `RECIPROCAL`, `NEGATE`. |
| `HistoryItem` | Duomenų klasė su unikaliu `UUID`, išraiška, rezultatu ir laiko žyma. |

## 🎮 Naudojimas

### Mygtukų išdėstymas

```
┌─────┬─────┬─────┬─────┐
│  %  │ CE  │  C  │  ⌫  │
├─────┼─────┼─────┼─────┤
│ 1/x │ x²  │ √x  │  ÷  │
├─────┼─────┼─────┼─────┤
│  7  │  8  │  9  │  ×  │
├─────┼─────┼─────┼─────┤
│  4  │  5  │  6  │  −  │
├─────┼─────┼─────┼─────┤
│  1  │  2  │  3  │  +  │
├─────┼─────┼─────┼─────┤
│ +/- │  0  │  ,  │  =  │
└─────┴─────┴─────┴─────┘
```

### Pagrindinės operacijos

| Veiksmas | Aprašymas |
|---|---|
| **Paprastas skaičiavimas** | Įveskite skaičių → pasirinkite operaciją → įveskite antrą skaičių → `=` |
| **Algebrinė tvarka** | Daugyba ir dalyba atliekamos pirma, pvz. `2 + 3 × 4 = 14` |
| **Grandininiai skaičiavimai** | Po `=` galite iš karto pasirinkti kitą operaciją |
| **Procentai** | `200 + 10%` → apskaičiuoja 10% nuo 200 ir prideda (= 220) |
| **Kvadratas / šaknis** | Paspauskite `x²` arba `√x` – vienetinė operacija taikoma dabartiniam skaičiui |
| **Atvirkštinis skaičius** | `1/x` – apskaičiuoja $\frac{1}{x}$ |
| **Ženklo keitimas** | `+/−` – pakeičia skaičiaus ženklą |
| **CE vs C** | `CE` ištrina tik dabartinę įvestį (operacija lieka), `C` išvalo viską |
| **Trynimas** | `⌫` ištrina paskutinį skaitmenį |
| **Istorija** | Atidarykite istoriją viršutiniame dešiniajame kampe; spustelėkite įrašą, kad atkurtumėte rezultatą |

## 🧪 Testavimas

Projektas turi vienetinius testus, aprėpiančius pagrindinę skaičiavimo logiką:

```bash
# Paleisti visus testus
./gradlew test
```

### Testuojami scenarijai

| Testas | Ką tikrina |
|---|---|
| `testSimpleAddition` | `5 + 7 = 12` ir istorijos saugojimas |
| `testAlgebraicPrecedence` | Operacijų tvarka: `2 + 3 × 4 = 14` |
| `testSubtractionAndDivision` | `20 − 10 ÷ 2 = 15` |
| `testSquare` | `6² = 36` |
| `testSquareRoot` | `√81 = 9` |
| `testReciprocal` | `1/4 = 0,25` |
| `testNegate` | Ženklo keitimas `+/−` |
| `testPercentWithAddition` | `200 + 10% = 220` |
| `testPercentWithMultiplication` | `200 × 10% = 20` |
| `testBackspace` | `123 → 12 → 1 → 0` |
| `testClearEntryVsClear` | CE ir C skirtumas |
| `testDivideByZero` | Dalyba iš nulio → klaidos pranešimas |
| `testHistoryRecallAndClear` | Istorijos atkūrimas ir valymas |

## 🛠️ Technologijos

| Technologija | Paskirtis |
|---|---|
| [Kotlin](https://kotlinlang.org/) | Programavimo kalba |
| [Jetpack Compose](https://developer.android.com/jetpack/compose) | Deklaratyvi UI sistema |
| [Material 3](https://m3.material.io/) | Dizaino sistema |
| [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) | Būsenos valdymas, atsparumas konfigūracijos pakeitimams |
| [StateFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/) | Reaktyvūs duomenų srautai (UDF) |
| [BigDecimal](https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html) | Tikslios aritmetikos skaičiavimai |
| [Material Icons Extended](https://developer.android.com/reference/kotlin/androidx/compose/material/icons/package-summary) | Piktogramų rinkinys |
| [JUnit 4](https://junit.org/junit4/) | Vienetinis testavimas |

## 📁 Projekto struktūra

```
Calculator/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/calculator/
│   │   │   │   ├── domain/          # Verslo logika
│   │   │   │   ├── ui/              # Compose UI + ViewModel
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/
│   │   │   │   ├── values/          # Angliški string'ai
│   │   │   │   └── values-lt/       # Lietuviški string'ai
│   │   │   └── AndroidManifest.xml
│   │   └── test/                    # Vienetiniai testai
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml           # Versijų katalogas
├── build.gradle.kts                 # Root Gradle konfigūracija
├── settings.gradle.kts
└── README.md
```

## 📄 Licencija

Šis projektas yra atviro kodo. Naudojimas ir modifikavimas leidžiamas laisvai.
