#!/bin/bash

# WinDroid Pro - Build Script
# This script builds the WinDroid Pro APK

set -e  # Exit on error

echo "=========================================="
echo "  WinDroid Pro - Build Script"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if we're in the right directory
if [ ! -f "settings.gradle" ]; then
    echo -e "${RED}Error: Please run this script from the WinDroidPro directory${NC}"
    exit 1
fi

# Function to print colored messages
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Check for required tools
echo "Checking prerequisites..."

# Check for Java
if ! command -v java &> /dev/null; then
    print_error "Java is not installed. Please install JDK 17 or later."
    exit 1
fi
print_success "Java found: $(java -version 2>&1 | head -n 1)"

# Check for Android SDK
if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
    print_error "Android SDK not found. Please set ANDROID_HOME or ANDROID_SDK_ROOT environment variable."
    exit 1
fi
print_success "Android SDK found"

# Check for NDK
if [ -z "$ANDROID_NDK_HOME" ]; then
    print_info "ANDROID_NDK_HOME not set. Will use NDK from SDK if available."
fi

echo ""
echo "=========================================="
echo "  Build Configuration"
echo "=========================================="
echo ""

# Ask for build type
echo "Select build type:"
echo "1) Debug (faster build, includes debug symbols)"
echo "2) Release (optimized, smaller APK)"
read -p "Enter choice [1-2]: " build_choice

case $build_choice in
    1)
        BUILD_TYPE="Debug"
        GRADLE_TASK="assembleDebug"
        ;;
    2)
        BUILD_TYPE="Release"
        GRADLE_TASK="assembleRelease"
        ;;
    *)
        print_error "Invalid choice. Defaulting to Debug build."
        BUILD_TYPE="Debug"
        GRADLE_TASK="assembleDebug"
        ;;
esac

print_info "Building $BUILD_TYPE version..."
echo ""

# Clean previous builds
echo "Cleaning previous builds..."
./gradlew clean
print_success "Clean completed"
echo ""

# Build the APK
echo "Building APK..."
echo "This may take several minutes on first build..."
echo ""

if ./gradlew $GRADLE_TASK; then
    print_success "Build completed successfully!"
    echo ""
    
    # Find the APK
    if [ "$BUILD_TYPE" = "Debug" ]; then
        APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
    else
        APK_PATH="app/build/outputs/apk/release/app-release-unsigned.apk"
    fi
    
    if [ -f "$APK_PATH" ]; then
        APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
        echo "=========================================="
        echo "  Build Summary"
        echo "=========================================="
        echo "Build Type: $BUILD_TYPE"
        echo "APK Location: $APK_PATH"
        echo "APK Size: $APK_SIZE"
        echo ""
        
        if [ "$BUILD_TYPE" = "Release" ]; then
            print_info "Note: Release APK is unsigned. You need to sign it before distribution."
            echo ""
            echo "To sign the APK:"
            echo "1. Create a keystore (if you don't have one):"
            echo "   keytool -genkey -v -keystore windroidpro.keystore -alias windroidpro -keyalg RSA -keysize 2048 -validity 10000"
            echo ""
            echo "2. Sign the APK:"
            echo "   jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore windroidpro.keystore $APK_PATH windroidpro"
            echo ""
            echo "3. Align the APK:"
            echo "   zipalign -v 4 $APK_PATH app-release-signed.apk"
        fi
        
        echo ""
        print_success "Build process completed!"
        
        # Ask if user wants to install
        if [ "$BUILD_TYPE" = "Debug" ]; then
            echo ""
            read -p "Do you want to install the APK on a connected device? (y/n): " install_choice
            if [ "$install_choice" = "y" ] || [ "$install_choice" = "Y" ]; then
                if command -v adb &> /dev/null; then
                    echo "Installing APK..."
                    if adb install -r "$APK_PATH"; then
                        print_success "APK installed successfully!"
                    else
                        print_error "Installation failed. Make sure USB debugging is enabled."
                    fi
                else
                    print_error "ADB not found. Please install Android SDK Platform Tools."
                fi
            fi
        fi
    else
        print_error "APK not found at expected location: $APK_PATH"
        exit 1
    fi
else
    print_error "Build failed. Check the error messages above."
    exit 1
fi

echo ""
echo "=========================================="
echo "  Thank you for using WinDroid Pro!"
echo "=========================================="