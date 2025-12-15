# Building WinDroid Pro on Android (Termux)

## ⚠️ Important Notice

**Building on Android is EXPERIMENTAL and has significant limitations.**

The main challenge is that WinDroid Pro requires:
- Android NDK (Native Development Kit) for C++ compilation
- CMake for native build system
- Large amounts of RAM (4GB+ recommended)
- ARM64 device with good performance

## Why It's Difficult

1. **NDK Requirement**: The project uses native C++ code that requires Android NDK, which is difficult to set up on Android itself
2. **Memory Requirements**: Building requires 2-4GB of RAM, which many Android devices don't have available
3. **CMake**: Native build tools are not easily available on Android
4. **Gradle**: Full Gradle builds are resource-intensive

## Possible Approaches

### Approach 1: Termux (Limited Success)

You can try building in Termux, but it will likely fail at the native compilation step.

#### Step 1: Install Termux

Download from F-Droid: https://f-droid.org/packages/com.termux/

#### Step 2: Setup Environment

```bash
# Update packages
pkg update && pkg upgrade -y

# Install basic tools
pkg install -y git wget curl unzip

# Install Java (required for Gradle)
pkg install -y openjdk-17

# Install build tools
pkg install -y gradle

# Install additional tools
pkg install -y clang make cmake
```

#### Step 3: Extract Project

```bash
# Copy the ZIP to Termux storage
# Usually in ~/storage/downloads/

cd ~
unzip ~/storage/downloads/WinDroidPro-Complete.zip
cd WinDroidPro
```

#### Step 4: Attempt Build (Will Likely Fail)

```bash
# Try to build (will probably fail at native compilation)
gradle assembleDebug
```

**Expected Result**: Build will fail because:
- Android NDK is not available in Termux
- CMake cannot find required Android build tools
- Native compilation requires Android-specific toolchains

### Approach 2: Build Java/Kotlin Only (Partial)

You can build the Java/Kotlin parts, but not the native C++ code.

```bash
# Disable native build temporarily
# Edit app/build.gradle and comment out:
# externalNativeBuild {
#     cmake {
#         path file('src/main/cpp/CMakeLists.txt')
#     }
# }

# Then try building
gradle assembleDebug
```

**Result**: You'll get an APK, but it won't work because the native libraries are missing.

### Approach 3: Remote Build (Recommended)

The best approach is to use a remote build service:

#### Option A: GitHub Actions (Free)

1. Push the project to GitHub
2. Use GitHub Actions to build
3. Download the APK from Actions artifacts

Create `.github/workflows/build.yml`:

```yaml
name: Build APK

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Setup Android SDK
      uses: android-actions/setup-android@v2
    
    - name: Build APK
      run: |
        chmod +x gradlew
        ./gradlew assembleDebug
    
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/*.apk
```

#### Option B: Cloud Build Services

Use services like:
- **Bitrise** (free tier available)
- **CircleCI** (free tier available)
- **Travis CI** (free for open source)

#### Option C: Remote Desktop

Use a cloud PC service:
- **AWS WorkSpaces**
- **Azure Virtual Desktop**
- **Google Cloud Workstations**

Install Android Studio on the cloud PC and build there.

## Alternative: Use Pre-built APK

The easiest solution is to build on a PC and transfer the APK to your Android device:

### On PC:
```bash
cd WinDroidPro
./gradlew assembleDebug
```

### Transfer to Android:
```bash
# Via ADB
adb push app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/

# Or upload to cloud storage (Google Drive, Dropbox, etc.)
```

### On Android:
1. Open file manager
2. Navigate to Downloads
3. Tap the APK to install

## Termux Build Script (Experimental)

If you still want to try, here's a script:

```bash
#!/data/data/com.termux/files/usr/bin/bash

echo "WinDroid Pro - Termux Build (Experimental)"
echo "==========================================="
echo ""
echo "WARNING: This will likely fail due to NDK requirements"
echo ""

# Check if in correct directory
if [ ! -f "settings.gradle" ]; then
    echo "Error: Run this from WinDroidPro directory"
    exit 1
fi

# Install dependencies
echo "Installing dependencies..."
pkg install -y openjdk-17 gradle git

# Set Java home
export JAVA_HOME=$PREFIX/opt/openjdk

# Disable native build (comment out in build.gradle)
echo "Disabling native build..."
sed -i 's/externalNativeBuild {/\/\/ externalNativeBuild {/g' app/build.gradle
sed -i 's/cmake {/\/\/ cmake {/g' app/build.gradle
sed -i 's/path file/\/\/ path file/g' app/build.gradle

# Try to build
echo "Attempting build..."
gradle assembleDebug

if [ $? -eq 0 ]; then
    echo "Build succeeded (but native libraries are missing)"
    echo "APK location: app/build/outputs/apk/debug/app-debug.apk"
else
    echo "Build failed (expected)"
    echo "Please build on a PC with Android Studio"
fi
```

## Recommended Solution

**Build on a PC or use cloud build services.**

### Why?
1. ✅ Faster build times
2. ✅ Better debugging
3. ✅ Full NDK support
4. ✅ More reliable
5. ✅ Easier troubleshooting

### Minimum PC Requirements:
- Windows 10/11, macOS 10.14+, or Linux
- 8GB RAM
- 10GB free storage
- Android Studio installed

## Summary

| Method | Success Rate | Difficulty | Recommended |
|--------|-------------|------------|-------------|
| Termux | 10% | Very Hard | ❌ No |
| Remote Build | 100% | Easy | ✅ Yes |
| Cloud PC | 100% | Medium | ✅ Yes |
| Local PC | 100% | Easy | ✅ Yes |

## Conclusion

**Building on Android is NOT recommended.**

Instead:
1. ✅ Build on a PC (Windows/Mac/Linux)
2. ✅ Use GitHub Actions (free cloud build)
3. ✅ Use cloud build services
4. ✅ Use remote desktop to a PC

Then transfer the APK to your Android device.

---

**Bottom Line**: While technically possible to attempt building in Termux, it's not practical due to NDK requirements. Use a PC or cloud build service for reliable results.

## Need Help?

If you don't have access to a PC:
1. Use GitHub Actions (completely free)
2. Ask a friend with a PC to build it
3. Use a cloud PC service (some have free trials)
4. Use online Android build services

---

**For best results, follow the standard build instructions on a PC!**