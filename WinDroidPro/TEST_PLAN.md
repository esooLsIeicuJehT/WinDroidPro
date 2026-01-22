# WinDroid Pro Test Plan

This document outlines the testing strategy for WinDroid Pro, focusing on compatibility, performance, and stability.

## 1. Application Compatibility Testing

### 1.1 Windows Applications
**Goal:** Verify that common Windows applications run correctly.
**Test Cases:**
- **Productivity:** Install and run Notepad++, LibreOffice. Verify file operations (Open/Save).
- **Games:** Run lightweight 2D games (e.g., Stardew Valley) and 3D titles (if GPU supported). Check FPS and input responsiveness.
- **Utilities:** Test 7-Zip for archive handling.

### 1.2 Bypass Tools (FRP/iOS)
**Goal:** Verify specialized tools for device management work via USB/Network.
**Test Cases:**
- **FRP Bypass Tools:**
    - Connect a locked Android device via USB OTG.
    - Launch the FRP tool within WinDroid Pro.
    - Verify the tool detects the connected device (requires USB Passthrough).
    - Attempt a read/write operation.
- **iOS Bypass Tools:**
    - Connect an iOS device in DFU/Recovery mode.
    - Launch the iOS tool.
    - Verify device detection.
    - **Note:** Driver support (USBDK/libusb) in Wine is critical here. Ensure `NativeUsbManager` is active.

## 2. Hardware Compatibility

### 2.1 USB Devices
**Goal:** Ensure USB OTG devices are detected and accessible in the container.
**Test Cases:**
- **Storage:** Plug in a USB Drive. Verify it appears as a drive letter in Wine.
- **Serial Devices:** Connect a USB-to-Serial adapter. Check COM port mapping.
- **ADB/Fastboot:** Connect an Android phone. Run `adb devices` inside the container.

## 3. Performance Optimization

### 3.1 ARM Translation (Box64)
**Goal:** specific presets improve performance for different workloads.
**Test Cases:**
- **Performance Preset:** Run a CPU-intensive benchmark (e.g., 7-Zip benchmark). Record scores.
- **Stability Preset:** Run a long-running task to check for crashes.
- **Verification:** Check `BOX64_DYNAREC` env vars are set correctly using `taskmgr` or `cmd /c set`.

### 3.2 Memory Usage
**Goal:** Minimize memory footprint.
**Test Cases:**
- Monitor memory usage via Android Studio Profiler during container startup and shutdown.
- Verify `optimizeMemory()` (mallopt) reduces RSS after app closure.

## 4. Benchmarking Procedure

1.  **Launch Benchmark App:** Use the internal `PerformanceBenchmark` test suite.
2.  **Environment:** Close background apps.
3.  **Metrics:** Record Startup Time, Memory Peak, and JNI Latency.

## 5. Automated Tests
- Unit tests are located in `app/src/test/java/`.
- Run via `./gradlew test`.
