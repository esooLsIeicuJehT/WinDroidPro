# WinDroid Pro - Deployment Guide

## Overview

This guide covers the complete deployment process for WinDroid Pro, from building the APK to distributing it to users.

## Pre-Deployment Checklist

### Code Quality
- [ ] All unit tests passing
- [ ] Integration tests passing
- [ ] No critical bugs
- [ ] Code reviewed and approved
- [ ] Documentation updated

### Build Configuration
- [ ] Version code incremented
- [ ] Version name updated
- [ ] ProGuard rules verified
- [ ] Native libraries included
- [ ] Assets packaged correctly

### Testing
- [ ] Tested on multiple devices
- [ ] Tested on different Android versions
- [ ] USB OTG functionality verified
- [ ] Performance benchmarks met
- [ ] Memory leaks checked

## Building Release APK

### Step 1: Update Version

Edit `app/build.gradle`:

```gradle
android {
    defaultConfig {
        versionCode 2  // Increment for each release
        versionName "1.0.1"  // Update version string
    }
}
```

### Step 2: Generate Keystore (First Time Only)

```bash
keytool -genkey -v \
  -keystore windroidpro-release.keystore \
  -alias windroidpro \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

**Important**: Store keystore securely and backup!

### Step 3: Configure Signing

Create `keystore.properties` in project root:

```properties
storePassword=YOUR_STORE_PASSWORD
keyPassword=YOUR_KEY_PASSWORD
keyAlias=windroidpro
storeFile=../windroidpro-release.keystore
```

Add to `.gitignore`:
```
keystore.properties
*.keystore
```

Update `app/build.gradle`:

```gradle
def keystorePropertiesFile = rootProject.file("keystore.properties")
def keystoreProperties = new Properties()
keystoreProperties.load(new FileInputStream(keystorePropertiesFile))

android {
    signingConfigs {
        release {
            keyAlias keystoreProperties['keyAlias']
            keyPassword keystoreProperties['keyPassword']
            storeFile file(keystoreProperties['storeFile'])
            storePassword keystoreProperties['storePassword']
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

### Step 4: Build Release APK

```bash
cd WinDroidPro
./gradlew clean
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

### Step 5: Verify APK

```bash
# Check signature
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk

# Check APK contents
unzip -l app/build/outputs/apk/release/app-release.apk

# Get APK info
aapt dump badging app/build/outputs/apk/release/app-release.apk
```

## Distribution Methods

### Method 1: GitHub Releases

#### Create Release

1. Go to GitHub repository
2. Click "Releases" → "Create a new release"
3. Tag version: `v1.0.1`
4. Release title: `WinDroid Pro v1.0.1`
5. Description:
   ```markdown
   ## What's New
   - Feature 1
   - Feature 2
   - Bug fixes
   
   ## Installation
   1. Download APK
   2. Enable "Install from Unknown Sources"
   3. Install APK
   
   ## Requirements
   - Android 8.0+
   - ARM64 processor
   - 4GB RAM
   ```
6. Upload APK
7. Publish release

#### Automated Release (GitHub Actions)

Create `.github/workflows/release.yml`:

```yaml
name: Release

on:
  push:
    tags:
      - 'v*'

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
    
    - name: Decode Keystore
      run: |
        echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > windroidpro-release.keystore
    
    - name: Build Release APK
      env:
        KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
        KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
      run: |
        cd WinDroidPro
        ./gradlew assembleRelease
    
    - name: Create Release
      uses: softprops/action-gh-release@v1
      with:
        files: WinDroidPro/app/build/outputs/apk/release/*.apk
        body_path: CHANGELOG.md
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

### Method 2: Direct Download

#### Set Up Web Server

1. Upload APK to web server
2. Create download page:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Download WinDroid Pro</title>
</head>
<body>
    <h1>WinDroid Pro v1.0.1</h1>
    <a href="WinDroidPro-v1.0.1.apk" download>
        Download APK (150 MB)
    </a>
    
    <h2>Installation Instructions</h2>
    <ol>
        <li>Download the APK</li>
        <li>Enable "Install from Unknown Sources"</li>
        <li>Open the APK file</li>
        <li>Follow installation prompts</li>
    </ol>
</body>
</html>
```

### Method 3: Google Play Store (Future)

#### Requirements
- Google Play Developer account ($25 one-time fee)
- Privacy policy
- App content rating
- Store listing assets

#### Steps
1. Create app bundle:
   ```bash
   ./gradlew bundleRelease
   ```

2. Upload to Play Console
3. Complete store listing
4. Submit for review

## Update Mechanism

### In-App Update Check

Create update checker:

```kotlin
class UpdateChecker(private val context: Context) {
    
    private val currentVersion = BuildConfig.VERSION_CODE
    private val updateUrl = "https://api.windroidpro.com/latest"
    
    suspend fun checkForUpdates(): UpdateInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val response = URL(updateUrl).readText()
                val json = JSONObject(response)
                val latestVersion = json.getInt("version_code")
                val downloadUrl = json.getString("download_url")
                
                if (latestVersion > currentVersion) {
                    UpdateInfo(
                        version = json.getString("version_name"),
                        downloadUrl = downloadUrl,
                        changelog = json.getString("changelog")
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to check for updates")
                null
            }
        }
    }
}

data class UpdateInfo(
    val version: String,
    val downloadUrl: String,
    val changelog: String
)
```

### Update API Endpoint

Create `latest.json` on server:

```json
{
  "version_code": 2,
  "version_name": "1.0.1",
  "download_url": "https://github.com/user/windroidpro/releases/download/v1.0.1/WinDroidPro-v1.0.1.apk",
  "changelog": "- Bug fixes\n- Performance improvements",
  "min_supported_version": 1,
  "force_update": false
}
```

## Post-Deployment

### Monitor Crashes

Use crash reporting:

```kotlin
// In Application class
class WinDroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Set up crash handler
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Timber.e(throwable, "Uncaught exception in thread: ${thread.name}")
            // Send crash report
            sendCrashReport(throwable)
        }
    }
}
```

### Collect Analytics

Track key metrics:
- Installation count
- Active users
- Feature usage
- Performance metrics
- Crash rate

### User Feedback

Set up feedback channels:
- GitHub Issues
- Email support
- In-app feedback form
- Community forum

## Rollback Plan

If critical issues found:

1. **Immediate Actions**
   - Remove download links
   - Post warning on website
   - Notify users via social media

2. **Fix and Redeploy**
   - Identify and fix issue
   - Test thoroughly
   - Deploy hotfix version
   - Increment version code

3. **Communication**
   - Explain issue to users
   - Provide workaround if available
   - Announce fix availability

## Version Numbering

Follow Semantic Versioning:

```
MAJOR.MINOR.PATCH

1.0.0 - Initial release
1.0.1 - Bug fixes
1.1.0 - New features
2.0.0 - Breaking changes
```

Version code (integer):
```
1 -> 1.0.0
2 -> 1.0.1
10 -> 1.1.0
100 -> 2.0.0
```

## Security Considerations

### APK Signing
- Use strong passwords
- Store keystore securely
- Never commit keystore to git
- Backup keystore safely

### Code Obfuscation
- Enable ProGuard/R8
- Test obfuscated builds
- Keep mapping files for crash reports

### Permissions
- Request only necessary permissions
- Explain permission usage to users
- Handle permission denials gracefully

## Legal Requirements

### Privacy Policy
Create privacy policy covering:
- Data collection
- Data usage
- Third-party services
- User rights

### Terms of Service
Define:
- Acceptable use
- Liability limitations
- Warranty disclaimers
- License terms

### Open Source Licenses
Include licenses for:
- Wine
- Box86/Box64
- Mesa
- DXVK/VKD3D
- Other dependencies

## Marketing

### Launch Announcement
- Reddit (r/Android, r/EmulationOnAndroid)
- XDA Developers
- Twitter/X
- YouTube demo video

### Documentation
- User guide
- FAQ
- Video tutorials
- Troubleshooting guide

### Community Building
- Discord server
- GitHub Discussions
- Subreddit
- Telegram group

## Maintenance Schedule

### Regular Updates
- Bug fixes: As needed
- Security patches: Monthly
- Feature updates: Quarterly
- Major versions: Annually

### Support
- Monitor GitHub Issues daily
- Respond to emails within 48 hours
- Update documentation as needed
- Release hotfixes for critical bugs

## Success Metrics

Track:
- Download count
- Active users (DAU/MAU)
- Retention rate
- Crash-free rate (target: >99%)
- Average rating
- User feedback sentiment

## Conclusion

Following this deployment guide ensures:
- Professional release process
- User trust and satisfaction
- Easy maintenance and updates
- Scalable distribution
- Legal compliance

---

**Good luck with your deployment! 🚀**