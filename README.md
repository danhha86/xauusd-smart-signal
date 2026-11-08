# XAUUSD Smart Signal — starter project

This is a starter engineering prototype, **not a compiled APK** and not a validated profitable trading system.

## What is included
- `android/`: Android app (Kotlin + Jetpack Compose), target Android 14 / API 34. It polls the backend for the latest signal.
- `backend/`: Python standard-library HTTP service that accepts OHLC bars from MT4 and calculates a basic multi-timeframe setup.
- `mt4/`: MQL4 Expert Advisor that exports closed XAUUSD candles for H4/H1/M15/M5. It does not place trades.
- `.github/workflows/build-apk.yml`: GitHub Actions workflow to compile a debug APK in the cloud.

## Build the APK without installing Android Studio on your computer
1. Sign in to GitHub and create a **private** repository, for example `xauusd-smart-signal`.
2. Upload the contents of this project (the files inside this folder, not the enclosing folder) to the repository. Keep `.github/workflows/build-apk.yml` in place.
3. Open the repository's **Actions** tab and select **Build Android APK**.
4. Choose **Run workflow** if it has not started automatically, then wait for the run to finish successfully.
5. Open the completed run, scroll to **Artifacts**, and download `XAUUSD-Smart-Signal-debug-apk`.
6. Extract the ZIP on your phone or computer. Install `app-debug.apk` on the Android 14 phone. Android may ask you to allow installation from that source.

The workflow builds an unsigned-for-release **debug APK** that is suitable for personal testing. It is not a Play Store release. A release build should be signed securely before distribution.

## Run the backend
The backend must run on a reachable computer/server; it does not run inside the APK.

```bash
cd backend
API_TOKEN='replace-with-a-long-random-token' python app.py
```

For a phone outside the same computer, use a reachable HTTPS server URL in the app. Do not expose the backend publicly over plain HTTP. Put it behind HTTPS and a firewall/reverse proxy. The sample token is for development only.

## Configure MT4
1. Copy `mt4/XAUUSD_SignalBridge.mq4` into the terminal's `MQL4/Experts/` folder and compile it in MetaEditor.
2. Set `ApiBaseUrl` to the backend URL and `ApiToken` to the same token used by the backend.
3. In MT4, open Tools → Options → Expert Advisors and allow WebRequest for that exact backend base URL.
4. Attach the EA to an XAUUSD chart. Ensure the symbol input matches the broker symbol (some accounts use a suffix such as `XAUUSD.` or `GOLD`).
5. Enable AutoTrading only as required for the EA to run; this EA transmits candles and contains no order-placement logic.

## Important limitations and safety
- This is a prototype. Its EMA/sweep/structure rules are simplified and have not been backtested or forward-tested on HFM data. Do not rely on its signals for live trading.
- The backend requires at least 205 bars per timeframe before it can evaluate EMA 200. Wait for MT4 history to download.
- The Android app's default URL (`10.0.2.2`) is for an Android emulator only. On a physical phone, enter the backend's reachable HTTPS URL and the matching API token.
- The sample app currently keeps API settings in memory only; they reset when the app process restarts.
- HFM contract size, tick value, tick size, minimum lot and lot step must be verified in the broker's MT4 symbol specification before using any risk estimate. With a $15 balance, the minimum lot may exceed a conservative risk budget; in that case, skip the trade.
- The app is an analysis/alert tool. It does not connect to the MT4 account for order placement and does not guarantee profit.
