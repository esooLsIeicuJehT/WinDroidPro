# WinDroid Pro Installation Guide

## Prerequisites
- **Android Device:** ARM64 processor (Snapdragon 845+ recommended).
- **Android Version:** Android 10 or higher.
- **RAM:** 8GB recommended (4GB minimum).
- **Storage:** At least 4GB free space.

## Installation Steps

1.  **Download APK:**
    - Get the latest `release` APK from the [Releases] page.

2.  **Install APK:**
    - Open the APK file on your Android device.
    - Allow installation from unknown sources if prompted.
    - Tap "Install".

3.  **Initial Setup:**
    - Launch **WinDroid Pro**.
    - Grant necessary permissions (Storage, Notification).
    - The app will extract necessary assets (Wine, Box64, DXVK). This may take a few minutes.

4.  **Create a Container:**
    - Tap the "+" button to create a new container.
    - Choose a name (e.g., "Win11").
    - Select **Box64 Preset**: "Performance" (recommended for games) or "Stability".
    - Tap "Create".

5.  **Run Windows Apps:**
    - Tap the "Run" button on your container.
    - Use the File Explorer to navigate to your `.exe` files (usually in `D:` drive which maps to Android Download folder).

## Troubleshooting

- **App Crashes:** Try changing the Box64 Preset to "Stability".
- **USB Not Detected:** Ensure OTG is enabled in Android settings and grant USB permissions when prompted.
- **Performance Issues:** Close other background apps.
