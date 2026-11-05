# MediaBackup 📷🎥

A native Android application built with **Kotlin** and **Jetpack Compose** designed for date-filtered, bi-directional media file synchronization between internal Android storage and external USB OTG storage (e.g., USB drives, M.2 SSD enclosures).

---

## Features

* **Bi-Directional Transfer:** Copy media files from internal storage to USB OTG, or restore from USB OTG back to internal storage.
* **Date & Time Range Filter:** Filter media files by specific start and end timestamps (`YYYY-MM-DD HH:mm`) down to the minute.
* **Smart Filename Timestamp Parsing:** Automatically parses dates from standard camera filename formats (`IMG_YYYYMMDD_HHMMSS` and `VID_YYYYMMDD_HHMMSS`), falling back to file modification time if needed.
* **Storage Access Framework (SAF):** Works on unrooted Android devices across standard external storage volumes and physical USB OTG drives.
* **Duplicate Skip:** Automatically skips existing target files to avoid redundant operations and unnecessary wear on flash storage.
* **Modern Black & Crimson UI:** Built with Jetpack Compose featuring a sleek dark theme.

---

## Tech Stack

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Material 3 with custom dark theme)
* **Async & Concurrency:** Kotlin Coroutines (`Dispatchers.IO`)
* **Storage API:** Android Storage Access Framework (`androidx.documentfile`)

---

## Setup & Execution

### Prerequisites
* Android 8.0 (API Level 26) or higher.
* USB OTG adapter/cable and a compatible USB storage device (exFAT file system recommended for cross-platform compatibility with Windows).

### How to Build & Run
1. Clone this repository:
   ```bash
   git clone [https://github.com/RifqiHarip/MediaBackup.git](https://github.com/RifqiHarip/MediaBackup.git)
