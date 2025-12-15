@echo off
REM WinDroid Pro - Build Script for Windows
REM This script builds the WinDroid Pro APK on Windows

setlocal enabledelayedexpansion

echo ==========================================
echo   WinDroid Pro - Build Script
echo ==========================================
echo.

REM Check if we're in the right directory
if not exist "settings.gradle" (
    echo Error: Please run this script from the WinDroidPro directory
    exit /b 1
)

echo Checking prerequisites...

REM Check for Java
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed. Please install JDK 17 or later.
    exit /b 1
)
echo [OK] Java found

REM Check for Android SDK
if "%ANDROID_HOME%"=="" if "%ANDROID_SDK_ROOT%"=="" (
    echo Error: Android SDK not found. Please set ANDROID_HOME or ANDROID_SDK_ROOT environment variable.
    exit /b 1
)
echo [OK] Android SDK found

echo.
echo ==========================================
echo   Build Configuration
echo ==========================================
echo.

REM Ask for build type
echo Select build type:
echo 1) Debug (faster build, includes debug symbols)
echo 2) Release (optimized, smaller APK)
set /p build_choice="Enter choice [1-2]: "

if "%build_choice%"=="1" (
    set BUILD_TYPE=Debug
    set GRADLE_TASK=assembleDebug
    set APK_PATH=app\build\outputs\apk\debug\app-debug.apk
) else if "%build_choice%"=="2" (
    set BUILD_TYPE=Release
    set GRADLE_TASK=assembleRelease
    set APK_PATH=app\build\outputs\apk\release\app-release-unsigned.apk
) else (
    echo Invalid choice. Defaulting to Debug build.
    set BUILD_TYPE=Debug
    set GRADLE_TASK=assembleDebug
    set APK_PATH=app\build\outputs\apk\debug\app-debug.apk
)

echo Building %BUILD_TYPE% version...
echo.

REM Clean previous builds
echo Cleaning previous builds...
call gradlew.bat clean
if errorlevel 1 (
    echo Error: Clean failed
    exit /b 1
)
echo [OK] Clean completed
echo.

REM Build the APK
echo Building APK...
echo This may take several minutes on first build...
echo.

call gradlew.bat %GRADLE_TASK%
if errorlevel 1 (
    echo Error: Build failed. Check the error messages above.
    exit /b 1
)

echo.
echo [OK] Build completed successfully!
echo.

REM Check if APK exists
if exist "%APK_PATH%" (
    echo ==========================================
    echo   Build Summary
    echo ==========================================
    echo Build Type: %BUILD_TYPE%
    echo APK Location: %APK_PATH%
    
    REM Get file size
    for %%A in ("%APK_PATH%") do set APK_SIZE=%%~zA
    set /a APK_SIZE_MB=!APK_SIZE! / 1048576
    echo APK Size: !APK_SIZE_MB! MB
    echo.
    
    if "%BUILD_TYPE%"=="Release" (
        echo Note: Release APK is unsigned. You need to sign it before distribution.
        echo.
        echo To sign the APK:
        echo 1. Create a keystore (if you don't have one):
        echo    keytool -genkey -v -keystore windroidpro.keystore -alias windroidpro -keyalg RSA -keysize 2048 -validity 10000
        echo.
        echo 2. Sign the APK:
        echo    jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore windroidpro.keystore %APK_PATH% windroidpro
        echo.
        echo 3. Align the APK:
        echo    zipalign -v 4 %APK_PATH% app-release-signed.apk
    )
    
    echo.
    echo [OK] Build process completed!
    
    REM Ask if user wants to install
    if "%BUILD_TYPE%"=="Debug" (
        echo.
        set /p install_choice="Do you want to install the APK on a connected device? (y/n): "
        if /i "!install_choice!"=="y" (
            where adb >nul 2>&1
            if errorlevel 1 (
                echo Error: ADB not found. Please install Android SDK Platform Tools.
            ) else (
                echo Installing APK...
                adb install -r "%APK_PATH%"
                if errorlevel 1 (
                    echo Error: Installation failed. Make sure USB debugging is enabled.
                ) else (
                    echo [OK] APK installed successfully!
                )
            )
        )
    )
) else (
    echo Error: APK not found at expected location: %APK_PATH%
    exit /b 1
)

echo.
echo ==========================================
echo   Thank you for using WinDroid Pro!
echo ==========================================

endlocal
pause