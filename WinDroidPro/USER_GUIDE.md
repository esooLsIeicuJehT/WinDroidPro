# WinDroid Pro User Guide

## Overview
WinDroid Pro allows you to run Windows applications on your Android device using Wine and Box64.

## Features

### Container Management
- **Create:** Set up isolated Windows environments.
- **Configure:** Adjust screen resolution, Wine version, and drivers (DXVK/VKD3D).
- **Shortcuts:** Create desktop shortcuts for frequent apps.

### USB Device Support
WinDroid Pro supports USB Passthrough for connecting external devices to Windows apps.
1.  Connect your USB device (Flash Drive, Serial Adapter, Mobile Device).
2.  A popup will ask for permission. Tap **OK**.
3.  Go to **USB Devices** in the app menu.
4.  Bind the detected device to your active container.

### Performance Tuning
You can adjust the **Box64 Preset** in Container Settings:
- **Performance:** Aggressive optimizations. Best for gaming.
- **Balanced:** Good trade-off between speed and compatibility.
- **Stability:** Conservative settings. Use if apps crash.

### Advanced Features
- **FRP/iOS Tools:** Run specialized repair tools. Ensure USB drivers are installed within the container if needed (though direct raw USB access is handled by the bridge).
- **Services:** Manage background services in the container.

## FAQ

**Q: Can I run GTA V?**
A: It depends on your device hardware. Heavy 3D games require powerful GPUs and active cooling.

**Q: How do I access files?**
A: Your Android `Download` folder is mapped to the `D:` drive inside the container.

**Q: My mouse is not working.**
A: Use the touch screen capabilities or connect a Bluetooth mouse/keyboard.
