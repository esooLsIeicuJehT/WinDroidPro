# WinDroid Pro - Advanced Windows Emulator for Android

![WinDroid Pro Logo](logo.png)

## Overview

WinDroid Pro is a next-generation Windows application emulator for Android that surpasses existing solutions through advanced features and optimizations.

## Key Features

### 🚀 Core Capabilities
- **Full Windows Application Support**: Run x86 and x64 Windows applications on ARM Android devices
- **USB OTG Support**: Full USB device passthrough with driver emulation
- **WSL Integration**: Built-in Windows Subsystem for Linux support
- **Container Management**: Create and manage isolated Windows environments
- **Modern UI**: Material Design 3 interface with dark/light themes

### 🎮 Graphics & Performance
- **DirectX Support**: DirectX 9/10/11/12 via DXVK/VKD3D translation
- **Vulkan Backend**: Native Vulkan support with Mesa Turnip/Zink
- **OpenGL 4.6**: Full OpenGL support via Zink
- **Optimized Translation**: Custom Box64 optimizations for ARM

### 🔌 USB Device Support
- USB Mass Storage (Flash drives, HDDs)
- USB Serial (COM ports)
- USB HID (Keyboards, mice, gamepads)
- USB Audio (Microphones, speakers)
- Custom USB devices with driver support

### 📦 Container Features
- Multiple Wine versions (8.x, 9.x, Staging)
- Snapshot and restore functionality
- Resource limit controls
- Environment variable management
- DLL override configuration

## System Requirements

### Minimum Requirements
- Android 8.0 (API 26) or higher
- ARM64 (aarch64) processor
- 4GB RAM
- 2GB free storage
- OpenGL ES 3.0 support

### Recommended Requirements
- Android 11+ (API 30+)
- Snapdragon 865+ or equivalent
- 6GB+ RAM
- 4GB+ free storage
- Vulkan 1.3 support
- USB OTG support

## Installation

1. Download the latest APK from the [Releases](releases) page
2. Enable "Install from Unknown Sources" in Android settings
3. Install the APK
4. Launch WinDroid Pro
5. Wait for initial setup to complete

## Usage

### Creating a Container

1. Open WinDroid Pro
2. Tap "Create New Container"
3. Configure container settings:
   - Name and description
   - Wine version
   - Architecture (32-bit or 64-bit)
   - Screen resolution
   - Graphics options (DXVK, VKD3D)
4. Tap "Create"

### Running Windows Applications

1. Select a container
2. Tap "Add Application"
3. Browse to the Windows executable (.exe)
4. Configure launch options
5. Tap "Launch"

### USB Device Management

1. Connect USB device via OTG adapter
2. Open "USB Devices" tab
3. Grant USB permissions when prompted
4. Select device and tap "Attach to Container"
5. Device will be available in Windows environment

## Supported Applications

### FRP Bypass Tools
- Samsung FRP Tools
- LG FRP Bypass
- Huawei FRP Unlock
- Generic Android FRP utilities

### iOS Bypass Tools
- iCloud Activation Lock tools
- iOS backup extractors
- iTunes alternatives
- Device management utilities

### General Applications
- Office suites (MS Office, LibreOffice)
- Development tools (IDEs, compilers)
- System utilities (disk tools, diagnostics)
- Productivity apps (note-taking, PDF editors)

## Architecture

WinDroid Pro uses a multi-layered architecture:

```
┌─────────────────────────────────────────┐
│     Android Application (Kotlin)        │
├─────────────────────────────────────────┤
│     Native Bridge (JNI/C++)             │
├─────────────────────────────────────────┤
│  Wine 9.x + Box64 + USB Passthrough     │
├─────────────────────────────────────────┤
│  Mesa (Turnip/Zink) + DXVK + VKD3D      │
└─────────────────────────────────────────┘
```

## Performance Optimization

### Box64 Presets
- **Performance**: Maximum speed, less compatibility
- **Balanced**: Good balance of speed and compatibility (default)
- **Stability**: Maximum compatibility, slower performance

### Graphics Options
- **DXVK**: DirectX 9/10/11 to Vulkan translation
- **VKD3D**: DirectX 12 to Vulkan translation
- **Zink**: OpenGL to Vulkan translation

## Troubleshooting

### Application Won't Launch
1. Check Wine version compatibility
2. Try different Box64 preset
3. Disable DXVK/VKD3D if graphics issues
4. Check logs in Settings > Logs

### USB Device Not Detected
1. Ensure USB OTG is enabled
2. Grant USB permissions
3. Try different USB cable/adapter
4. Check device compatibility

### Performance Issues
1. Switch to Performance Box64 preset
2. Lower screen resolution
3. Disable unnecessary graphics features
4. Close background apps

## Building from Source

### Prerequisites
- Android Studio Arctic Fox or later
- Android NDK r26+
- CMake 3.22+
- Git

### Build Steps

```bash
# Clone repository
git clone https://github.com/yourusername/windroidpro.git
cd windroidpro

# Open in Android Studio
# Build > Make Project

# Or build from command line
./gradlew assembleRelease
```

## Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file for details.

## Credits

### Third-Party Components
- **Wine**: Windows API implementation ([winehq.org](https://www.winehq.org/))
- **Box86/Box64**: x86/x64 translation by [ptitSeb](https://github.com/ptitSeb)
- **Mesa**: Graphics drivers ([mesa3d.org](https://www.mesa3d.org))
- **DXVK**: DirectX to Vulkan translation ([github.com/doitsujin/dxvk](https://github.com/doitsujin/dxvk))
- **VKD3D**: Direct3D 12 to Vulkan ([gitlab.winehq.org/wine/vkd3d](https://gitlab.winehq.org/wine/vkd3d))

### Special Thanks
- Termux team for GLIBC patches
- Wine community for continuous development
- Box86/Box64 contributors
- Mesa developers

## Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/windroidpro/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/windroidpro/discussions)
- **Email**: support@windroidpro.com

## Roadmap

### Version 1.1
- [ ] Cloud sync for containers
- [ ] Scripting automation (Lua)
- [ ] Performance profiler
- [ ] App templates

### Version 1.2
- [ ] Network bridge mode
- [ ] Audio routing improvements
- [ ] Input mapping customization
- [ ] Batch operations

### Version 2.0
- [ ] Multi-container support
- [ ] Remote desktop integration
- [ ] Advanced driver emulation
- [ ] Plugin system

## Disclaimer

WinDroid Pro is provided "as is" without warranty of any kind. Use at your own risk. The developers are not responsible for any damage or data loss.

## Legal

This software is for educational purposes only. Users are responsible for complying with all applicable laws and software licenses.

---

**Made with ❤️ by the WinDroid Pro Team**