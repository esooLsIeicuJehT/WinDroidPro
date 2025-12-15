# WinDroid Pro - Build Instructions

## Prerequisites

### Required Software
1. **Android Studio** (Hedgehog 2023.1.1 or later)
   - Download from: https://developer.android.com/studio
   
2. **Android NDK** (r26 or later)
   - Install via Android Studio SDK Manager
   - Or download from: https://developer.android.com/ndk/downloads
   
3. **CMake** (3.22.1 or later)
   - Install via Android Studio SDK Manager
   
4. **Git**
   - Download from: https://git-scm.com/downloads

### System Requirements
- **OS**: Windows 10/11, macOS 10.14+, or Linux (Ubuntu 20.04+)
- **RAM**: 8GB minimum, 16GB recommended
- **Storage**: 10GB free space
- **Java**: JDK 17 (included with Android Studio)

## Build Steps

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/windroidpro.git
cd windroidpro/WinDroidPro
```

### 2. Open in Android Studio

```bash
# On macOS/Linux
studio .

# On Windows
start studio .
```

Or manually:
1. Open Android Studio
2. File → Open
3. Navigate to `WinDroidPro` folder
4. Click "Open"

### 3. Configure SDK and NDK

1. Open SDK Manager (Tools → SDK Manager)
2. Install required components:
   - Android SDK Platform 34
   - Android SDK Build-Tools 34.0.0
   - NDK (Side by side) version 26.x
   - CMake version 3.22.1

### 4. Sync Gradle

1. Wait for Gradle sync to complete automatically
2. Or manually: File → Sync Project with Gradle Files

### 5. Build the Project

#### Debug Build (for testing)
```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

#### Release Build (for distribution)
```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

### 6. Sign the APK (Release Only)

#### Generate Keystore (first time only)
```bash
keytool -genkey -v -keystore windroidpro.keystore -alias windroidpro -keyalg RSA -keysize 2048 -validity 10000
```

#### Sign the APK
```bash
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore windroidpro.keystore app/build/outputs/apk/release/app-release-unsigned.apk windroidpro
```

#### Align the APK
```bash
zipalign -v 4 app/build/outputs/apk/release/app-release-unsigned.apk app/build/outputs/apk/release/WinDroidPro.apk
```

## Build Variants

### Debug Build
- Includes debug symbols
- Logging enabled
- No code obfuscation
- Larger APK size
- Use for development and testing

### Release Build
- Optimized code
- ProGuard/R8 enabled
- Logging removed
- Smaller APK size
- Use for distribution

## Troubleshooting

### Gradle Sync Failed
**Solution**: 
1. Check internet connection
2. File → Invalidate Caches / Restart
3. Delete `.gradle` folder and sync again

### NDK Not Found
**Solution**:
1. Open SDK Manager
2. Install NDK (Side by side)
3. Update `local.properties`:
   ```
   ndk.dir=/path/to/Android/sdk/ndk/26.x.xxxxx
   ```

### CMake Error
**Solution**:
1. Install CMake via SDK Manager
2. Update `build.gradle` CMake version if needed

### Out of Memory
**Solution**:
Update `gradle.properties`:
```
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

### Build Too Slow
**Solution**:
Enable parallel builds in `gradle.properties`:
```
org.gradle.parallel=true
org.gradle.caching=true
```

## Advanced Build Options

### Custom Build Configuration

Edit `app/build.gradle` to customize:

```gradle
android {
    defaultConfig {
        // Change package name
        applicationId "com.yourcompany.windroidpro"
        
        // Change version
        versionCode 2
        versionName "1.1.0"
        
        // Target specific ABIs
        ndk {
            abiFilters 'arm64-v8a'  // Only ARM64
        }
    }
}
```

### Build Specific ABI

```bash
# ARM64 only
./gradlew assembleRelease -Pandroid.injected.build.abi=arm64-v8a

# ARMv7 only
./gradlew assembleRelease -Pandroid.injected.build.abi=armeabi-v7a
```

### Clean Build

```bash
./gradlew clean
./gradlew assembleRelease
```

## CI/CD Integration

### GitHub Actions

Create `.github/workflows/build.yml`:

```yaml
name: Build APK

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

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
      
    - name: Build with Gradle
      run: |
        cd WinDroidPro
        chmod +x gradlew
        ./gradlew assembleRelease
        
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-release
        path: WinDroidPro/app/build/outputs/apk/release/*.apk
```

## Build Optimization

### Reduce APK Size

1. Enable R8 full mode (already enabled)
2. Remove unused resources:
   ```gradle
   android {
       buildTypes {
           release {
               shrinkResources true
               minifyEnabled true
           }
       }
   }
   ```

3. Use APK splits:
   ```gradle
   android {
       splits {
           abi {
               enable true
               reset()
               include 'arm64-v8a', 'armeabi-v7a'
               universalApk false
           }
       }
   }
   ```

### Speed Up Build

1. Use Gradle daemon (enabled by default)
2. Enable parallel execution
3. Use build cache
4. Increase heap size

## Verification

### Test the APK

```bash
# Install on connected device
adb install app/build/outputs/apk/release/WinDroidPro.apk

# Check logs
adb logcat | grep WinDroid
```

### Verify Signature

```bash
jarsigner -verify -verbose -certs WinDroidPro.apk
```

## Distribution

### Google Play Store
1. Create app bundle:
   ```bash
   ./gradlew bundleRelease
   ```
2. Upload to Play Console

### Direct Distribution
1. Sign the APK
2. Upload to GitHub Releases
3. Share download link

## Support

For build issues:
- Check [GitHub Issues](https://github.com/yourusername/windroidpro/issues)
- Read [Documentation](https://github.com/yourusername/windroidpro/wiki)
- Ask in [Discussions](https://github.com/yourusername/windroidpro/discussions)

---

**Happy Building! 🚀**