# CustomizeMyPoco 🚀

> **The ultimate customization suite & LSPosed module for POCO smartphones running Xiaomi HyperOS.**  
> Liquid Glass Gaussian blur, giant icons (up to 200%), 120Hz unlock, and flagship feature restoration on POCO devices.

---

## 🌟 Key Features

### 1. 🔍 Extreme Icon Scaling (50% – 200%)
* Removes vendor restrictions on icon sizes in POCO Launcher (`com.mi.android.globallauncher`).
* Smooth slider allows scaling icons up to **2× original size**.
* **Hide Icon Labels:** One-tap toggle to hide text labels under icons for a clean, iOS-style / minimalist aesthetic.
* Dynamic cell layout adjustments so giant icons never get clipped.

### 2. 🪟 Liquid Glass & Real-Time Gaussian Blur
* Replaces the flat gray background in the Control Center and Notification Shade with **real-time Gaussian blur**.
* Tunable blur radius slider (10px – 100px).
* Restores flagship-grade aesthetics previously reserved for Xiaomi/Redmi K-series flagships.

### 3. 📂 Super Icons & XXL 3×3 Large Folders
* Unlocks large folders and Super Icons natively in POCO Launcher.
* Enables fluid open/close folder transitions.

### 4. ⚡ 120 FPS / Refresh Rate Lock Release
* Fixes the HyperOS behavior where opening notifications drops display refresh rate from 120Hz to 60Hz.

### 5. 👑 Flagship Profile Spoofing (`deviceLevel = 3`)
* Hooks `miui.os.Build` flags (`IS_POCO = false`, `DEVICE_LEVEL = 3`).
* Forces HyperOS to unlock full shader pipelines, advanced textures, and flagship animation physics.

---

## 📱 Compatibility
* **Target Devices:** POCO Series (POCO X7 Pro, X6 Pro, F6, F6 Pro, F5, X5 Pro, etc.).
* **OS:** Xiaomi HyperOS 1.0, 2.0, 3.0+ (Android 14, 15, 16).
* **Launchers:** POCO Launcher (`com.mi.android.globallauncher`) and HyperOS Home (`com.miui.home`).

---

## 🛠️ Installation & Setup

1. **Prerequisites:**
   * Root via **KernelSU**, **Magisk**, or **APatch**.
   * Working **LSPosed** environment (Zygisk-Next / LSPosed_mod).

2. **Installation:**
   * Download the latest `CustomizeMyPoco.apk` from the [Releases](../../releases) tab or build using GitHub Actions.
   * Install the APK on your device.
   * Open the **LSPosed** app:
     * Find **CustomizeMyPoco** in modules.
     * Enable it.
     * Recommended scopes are pre-selected: `System Framework (android)`, `System UI`, `System UI Plugin`, `POCO Launcher`.
   * Open **CustomizeMyPoco**, configure your preferences, and restart your launcher / SystemUI.

---

## 🏗️ Building from Source

```bash
git clone https://github.com/<your-username>/CustomizeMyPoco.git
cd CustomizeMyPoco
chmod +x gradlew
./gradlew assembleDebug
```

The compiled APK will be in `app/build/outputs/apk/debug/app-debug.apk`.  
GitHub Actions also compiles every push automatically.

---

## 📄 License
Licensed under the [MIT License](LICENSE).
