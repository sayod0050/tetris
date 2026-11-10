# Block Drop: Release & Publishing Guide

This guide covers building, testing, packaging, and publishing Block Drop to the Google Play Console.

---

## 1. Technical Architecture & Technology Comparison
### Why Native Kotlin + Jetpack Compose?
- **Silky 60+ FPS:** Native Hardware Canvas rendering provides high-frame-rate animations without garbage collection stutter or frame drops.
- **Ultra-low Input Latency:** Direct Android pointer input and gesture detection ensures instantaneous response for tournament-level falling-block puzzle play.
- **Google Play Compliance:** Fully complies with target API 34+ / 36 requirements, 64-bit architecture, scoped storage, and Android vitals.
- **Lightweight APK:** Clean footprint without heavy Python runtimes or unpack delays.

---

## 2. Building & Packaging Commands

### Running Unit & Robolectric Tests
To run all automated engine tests (collision, SRS wall-kick rotations, line-clears, 7-bag distribution, preferences):
```bash
gradle :app:testDebugUnitTest
```

### Compiling Debug APK
To generate a debug APK for local device installation and testing:
```bash
gradle :app:assembleDebug
```
Output path: `app/build/outputs/apk/debug/app-debug.apk`

### Compiling Signed Release APK
To build the signed release APK:
```bash
gradle :app:assembleRelease
```
Output path: `app/build/outputs/apk/release/app-release.apk`

### Compiling Android App Bundle (.aab) for Google Play
Google Play requires `.aab` (Android App Bundle) format for production publishing:
```bash
gradle :app:bundleRelease
```
Output path: `app/build/outputs/bundle/release/app-release.aab`

---

## 3. Google Play Console Submission Checklist
1. **App Identity:**
   - Package Name / Application ID: `com.aistudio.blockdrop.puzzle`
   - Version Code: `1`
   - Version Name: `1.0`
   - Title: `Block Drop: Falling Puzzle` (27 chars, <= 30 chars limit)
2. **App Icon & Graphics:**
   - Adaptive Launcher Icon: centered in 66dp safe zone on #111827 background.
   - High-Res 512x512 Icon: exported from `app/src/main/res/drawable/app_icon_block_drop_*.jpg`.
   - Feature Graphic (1024x500): `app/src/main/res/drawable/feature_graphic_block_drop_*.jpg`.
3. **App Content Declarations:**
   - Privacy Policy: Hosted URL pointing to `docs/privacy_policy.md`.
   - Ads: Select "No, my app does not contain ads".
   - App Access: Select "All functionality is available without special access".
   - Target Audience: Select "All ages" (or 13+).
   - Data Safety: Select "No data collected", "No data shared".
4. **Permissions Declaration:**
   - Only `android.permission.VIBRATE` (normal install-time permission, no runtime prompt required).
