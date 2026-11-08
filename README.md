# XAUUSD Smart Signal (clean rebuild)

A minimal native Android app for manually entered XAUUSD multi-timeframe analysis.

## Requirements
- Android Studio / Android SDK 35 for local builds, or GitHub Actions.
- JDK 17 and Gradle 8.9.
- Android 8.0+ (minSdk 26); target SDK 35.

## GitHub Actions build
1. Upload the contents of this folder to the root of a GitHub repository (not the enclosing folder).
2. Open **Actions** → **Build Android APK**.
3. Choose **Run workflow** (or push to `main`).
4. When successful, open the run → **Artifacts** → download `XAUUSD-Smart-Signal-debug-apk`.
5. Extract the ZIP and install `app-debug.apk` on the Android phone. Allow installation from the browser/files app if Android asks.

APK output: `android/app/build/outputs/apk/debug/app-debug.apk`.

## Current scope
- Manual input for current price, M15 ATR and RSI, plus H4/H1/M15/M5 context.
- Rule-based BUY/SELL/WAIT decision, reference entry, ATR-based SL and 1:2 TP.
- No live market feed, broker connection, automatic order placement, or guaranteed performance.

This is a starter implementation for testing and iteration, not financial advice.
