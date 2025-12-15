# WinDroid Pro - Quick Start Guide

## 🚀 Getting Started in 5 Minutes

This guide will help you build WinDroid Pro APK quickly.

## Prerequisites

### Required Software

1. **Android Studio** (Hedgehog 2023.1.1 or later)
   - Download: https://developer.android.com/studio
   - Includes: Android SDK, Gradle, JDK

2. **Android NDK** (r26 or later)
   - Install via Android Studio SDK Manager
   - Tools → SDK Manager → SDK Tools → NDK (Side by side)

3. **CMake** (3.22.1 or later)
   - Install via Android Studio SDK Manager
   - Tools → SDK Manager → SDK Tools → CMake

### System Requirements
- **OS**: Windows 10/11, macOS 10.14+, or Linux (Ubuntu 20.04+)
- **RAM**: 8GB minimum (16GB recommended)
- **Storage**: 10GB free space
- **Internet**: For downloading dependencies

## Method 1: Build with Android Studio (Recommended)

### Step 1: Extract the Project

```bash
# Extract the ZIP file
unzip WinDroidPro-Complete.zip
cd WinDroidPro
```

### Step 2: Open in Android Studio

1. Launch Android Studio
2. Click "Open" on the welcome screen
3. Navigate to the `WinDroidPro` folder
4. Click "OK"
5. Wait for Gradle sync to complete (may take 5-10 minutes first time)

### Step 3: Configure SDK and NDK

1. Go to **File → Project Structure**
2. Under **SDK Location**, verify:
   - Android SDK location is set
   - Android NDK location is set
3. Click "OK"

### Step 4: Build the APK

#### For Debug Build (Testing):
1. Go to **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Wait for build to complete
3. Click "locate" in the notification to find the APK
4. APK location: `app/build/outputs/apk/debug/app-debug.apk`

#### For Release Build (Distribution):
1. Go to **Build → Generate Signed Bundle / APK**
2. Select "APK" and click "Next"
3. Create a new keystore or use existing one
4. Fill in keystore details
5. Click "Next" and then "Finish"
6. APK location: `app/build/outputs/apk/release/app-release.apk`

### Step 5: Install on Device

1. Connect your Android device via USB
2. Enable USB debugging on your device
3. In Android Studio, click the "Run" button (green play icon)
4. Select your device from the list
5. App will be installed and launched

## Method 2: Build with Command Line

### On Linux/macOS:

```bash
# Extract the project
unzip WinDroidPro-Complete.zip
cd WinDroidPro

# Make build script executable
chmod +x build.sh

# Run the build script
./build.sh

# Follow the prompts to select build type
```

### On Windows:

```cmd
# Extract the project
# Navigate to WinDroidPro folder in Command Prompt

# Run the build script
build.bat

# Follow the prompts to select build type
```

### Manual Command Line Build:

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Clean build
./gradlew clean assembleRelease
```

## Method 3: Build on Android Device (Termux)

Yes, you can build on Android itself using Termux!

### Step 1: Install Termux

Download Termux from F-Droid: https://f-droid.org/packages/com.termux/

### Step 2: Setup Build Environment

```bash
# Update packages
pkg update && pkg upgrade

# Install required packages
pkg install openjdk-17 gradle git wget unzip

# Install Android SDK (optional, for full build)
# This is complex and may not work on all devices
```

### Step 3: Build (Limited)

```bash
# Extract project
unzip WinDroidPro-Complete.zip
cd WinDroidPro

# Try to build (may fail due to NDK requirements)
gradle assembleDebug
```

**Note**: Building on Android is experimental and may not work due to NDK requirements. Use a PC for reliable builds.

## Troubleshooting

### Problem: Gradle Sync Failed

**Solution**:
1. Check internet connection
2. File → Invalidate Caches / Restart
3. Delete `.gradle` folder in project root
4. Sync again

### Problem: NDK Not Found

**Solution**:
1. Open SDK Manager (Tools → SDK Manager)
2. Go to SDK Tools tab
3. Check "NDK (Side by side)"
4. Click "Apply" to install
5. Restart Android Studio

### Problem: Build Failed - Out of Memory

**Solution**:
1. Edit `gradle.properties` in project root
2. Add or modify:
   ```
   org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
   ```
3. Restart Android Studio and rebuild

### Problem: CMake Error

**Solution**:
1. Open SDK Manager
2. Install CMake from SDK Tools
3. Verify CMake version is 3.22.1 or later
4. Sync project again

### Problem: APK Won't Install

**Solution**:
1. Enable "Install from Unknown Sources" in Android settings
2. Uninstall any previous version
3. Check Android version (minimum 8.0 required)
4. Try installing via ADB:
   ```bash
   adb install -r app-debug.apk
   ```

## Build Output Locations

### Debug Build:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Release Build (Unsigned):
```
app/build/outputs/apk/release/app-release-unsigned.apk
```

### Release Build (Signed):
```
app/build/outputs/apk/release/app-release.apk
```

## APK Size

- **Debug APK**: ~50-80 MB (without Wine/Box64 binaries)
- **Release APK**: ~30-50 MB (without Wine/Box64 binaries)
- **With Binaries**: ~150-200 MB (estimated)

## Next Steps After Building

### 1. Test the APK

```bash
# Install on connected device
adb install -r app-debug.apk

# Check logs
adb logcat | grep WinDroid
```

### 2. Add Wine/Box64 Binaries

To make the app fully functional, you need to add:
- Wine 9.x ARM64 binaries
- Box64 ARM64 binaries
- Mesa/Graphics libraries

Place them in:
```
app/src/main/jniLibs/arm64-v8a/
```

### 3. Customize

- Change app name in `res/values/strings.xml`
- Change package name in `build.gradle`
- Add app icon in `res/mipmap/`
- Modify colors in `res/values/colors.xml`

### 4. Sign for Release

```bash
# Create keystore
keytool -genkey -v -keystore windroidpro.keystore -alias windroidpro -keyalg RSA -keysize 2048 -validity 10000

# Sign APK
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore windroidpro.keystore app-release-unsigned.apk windroidpro

# Align APK
zipalign -v 4 app-release-unsigned.apk app-release.apk
```

## Build Time Estimates

- **First Build**: 10-15 minutes (downloading dependencies)
- **Subsequent Builds**: 2-5 minutes
- **Clean Build**: 5-10 minutes
- **Incremental Build**: 30 seconds - 2 minutes

## Build Optimization Tips

### Speed Up Builds

1. **Enable Gradle Daemon** (in `gradle.properties`):
   ```
   org.gradle.daemon=true
   ```

2. **Enable Parallel Builds**:
   ```
   org.gradle.parallel=true
   ```

3. **Enable Build Cache**:
   ```
   org.gradle.caching=true
   ```

4. **Increase Heap Size**:
   ```
   org.gradle.jvmargs=-Xmx4096m
   ```

### Reduce APK Size

1. **Enable R8 Optimization** (already enabled)
2. **Remove Unused Resources**:
   ```gradle
   buildTypes {
       release {
           shrinkResources true
           minifyEnabled true
       }
   }
   ```

3. **Use APK Splits** for different architectures

## Getting Help

### Resources
- **Documentation**: Read all .md files in the project
- **Build Logs**: Check `app/build/outputs/logs/`
- **Gradle Output**: Look for error messages in build output

### Common Issues
- Check `BUILD_INSTRUCTIONS.md` for detailed troubleshooting
- Review `DEPLOYMENT_GUIDE.md` for release process
- Read `TECHNICAL_SPECIFICATION.md` for architecture details

## Success Checklist

- [ ] Android Studio installed
- [ ] SDK and NDK configured
- [ ] Project opened in Android Studio
- [ ] Gradle sync completed
- [ ] Build successful
- [ ] APK generated
- [ ] APK installed on device
- [ ] App launches without crashes

## Congratulations! 🎉

You've successfully built WinDroid Pro! 

Next steps:
1. Test the app thoroughly
2. Add Wine/Box64 binaries
3. Customize branding
4. Deploy to users

---

**Need more help?** Check the comprehensive documentation:
- `BUILD_INSTRUCTIONS.md` - Detailed build guide
- `DEPLOYMENT_GUIDE.md` - Release and distribution
- `TECHNICAL_SPECIFICATION.md` - Architecture details
- `README.md` - User documentation

**Happy Building! 🚀**