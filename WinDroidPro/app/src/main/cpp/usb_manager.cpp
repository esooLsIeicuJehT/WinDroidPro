#include <jni.h>
#include <string>
#include <cstring>
#include <cerrno>
#include <android/log.h>
#include <linux/usbdevice_fs.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <unistd.h>
#include <poll.h>

#define LOG_TAG "UsbManager"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Constants for write optimization
constexpr int USB_WRITE_TIMEOUT_MS = 2000;
constexpr int SMALL_WRITE_THRESHOLD = 4096;
constexpr int SMALL_TRANSFER_THRESHOLD = 16384;

extern "C" {

/**
 * Open USB device
 */
JNIEXPORT jint JNICALL
Java_com_windroidpro_usb_NativeUsbManager_nativeOpenDevice(
        JNIEnv *env,
        jobject /* this */,
        jstring devicePath) {
    
    const char *path = env->GetStringUTFChars(devicePath, nullptr);
    LOGI("Opening USB device: %s", path);
    
    // Open with O_NONBLOCK to avoid blocking on the open call itself
    int fd = open(path, O_RDWR | O_NONBLOCK);
    if (fd < 0) {
        LOGE("Failed to open USB device: %s", strerror(errno));
        env->ReleaseStringUTFChars(devicePath, path);
        return -1;
    }

    // Restore blocking mode for read/write operations
    int flags = fcntl(fd, F_GETFL);
    if (flags >= 0) {
        fcntl(fd, F_SETFL, flags & ~O_NONBLOCK);
    }
    
    env->ReleaseStringUTFChars(devicePath, path);
    LOGI("USB device opened successfully, fd: %d", fd);
    return fd;
}

/**
 * Close USB device
 */
JNIEXPORT void JNICALL
Java_com_windroidpro_usb_NativeUsbManager_nativeCloseDevice(
        JNIEnv *env,
        jobject /* this */,
        jint fd) {
    
    if (fd >= 0) {
        close(fd);
        LOGI("USB device closed, fd: %d", fd);
    }
}

/**
 * Read from USB device
 */
JNIEXPORT jint JNICALL
Java_com_windroidpro_usb_NativeUsbManager_nativeReadDevice(
        JNIEnv *env,
        jobject /* this */,
        jint fd,
        jbyteArray buffer,
        jint length) {
    
    if (fd < 0) {
        LOGE("Invalid file descriptor");
        return -1;
    }

    struct pollfd pfd;
    pfd.fd = fd;
    pfd.events = POLLIN;

    // Wait for data availability to avoid blocking while holding JNI resources
    int poll_result = poll(&pfd, 1, -1);
    
    if (poll_result < 0) {
        LOGE("Poll failed: %s", strerror(errno));
        return -1;
    }

    jbyte *buf = env->GetByteArrayElements(buffer, nullptr);
    int bytes_read = read(fd, buf, length);
    
    if (bytes_read < 0) {
        LOGE("Failed to read from USB device: %s", strerror(errno));
    }
    
    env->ReleaseByteArrayElements(buffer, buf, 0);
    return bytes_read;
}

/**
 * Write to USB device
 */
JNIEXPORT jint JNICALL
Java_com_windroidpro_usb_NativeUsbManager_nativeWriteDevice(
        JNIEnv *env,
        jobject /* this */,
        jint fd,
        jbyteArray buffer,
        jint length) {
    
    if (fd < 0) {
        LOGE("Invalid file descriptor");
        return -1;
    }

    // Poll to ensure device is writable (prevents indefinite blocking)
    struct pollfd pfd;
    pfd.fd = fd;
    pfd.events = POLLOUT;
    int poll_ret = poll(&pfd, 1, USB_WRITE_TIMEOUT_MS);
    if (poll_ret <= 0) {
        if (poll_ret == 0) {
            LOGE("Write timed out - device not ready");
        } else {
            LOGE("Poll failed: %s", strerror(errno));
        }
        return -1;
    }

    // Optimization: Use stack buffer for small writes to avoid JNI overhead
    // and prevent pinning large arrays during blocking I/O
    if (length <= SMALL_WRITE_THRESHOLD) {
        jbyte buf[SMALL_WRITE_THRESHOLD];
        // Note: Java code must ensure length <= buffer.length
        env->GetByteArrayRegion(buffer, 0, length, buf);

        if (env->ExceptionCheck()) {
            return -1;
        }

        int bytes_written = write(fd, buf, length);
        if (bytes_written < 0) {
            LOGE("Failed to write to USB device: %s", strerror(errno));
        }
        return bytes_written;
    } else {
        // Fallback for larger writes
        jbyte *buf = env->GetByteArrayElements(buffer, nullptr);
        if (buf == nullptr) return -1;

        int bytes_written = write(fd, buf, length);

        if (bytes_written < 0) {
            LOGE("Failed to write to USB device: %s", strerror(errno));
        }

        env->ReleaseByteArrayElements(buffer, buf, JNI_ABORT);
        return bytes_written;
    }
}

/**
 * Control transfer
 */
JNIEXPORT jint JNICALL
Java_com_windroidpro_usb_NativeUsbManager_nativeControlTransfer(
        JNIEnv *env,
        jobject /* this */,
        jint fd,
        jint requestType,
        jint request,
        jint value,
        jint index,
        jbyteArray buffer,
        jint length,
        jint timeout) {
    
    if (fd < 0) {
        LOGE("Invalid file descriptor");
        return -1;
    }
    
    struct usbdevfs_ctrltransfer ctrl;
    ctrl.bRequestType = requestType;
    ctrl.bRequest = request;
    ctrl.wValue = value;
    ctrl.wIndex = index;
    ctrl.wLength = length;
    ctrl.timeout = timeout;
    
    jbyte *buf = nullptr;
    if (buffer != nullptr) {
        buf = env->GetByteArrayElements(buffer, nullptr);
        ctrl.data = buf;
    } else {
        ctrl.data = nullptr;
    }
    
    int result = ioctl(fd, USBDEVFS_CONTROL, &ctrl);
    
    if (result < 0) {
        LOGE("Control transfer failed: %s", strerror(errno));
    }
    
    if (buf != nullptr) {
        env->ReleaseByteArrayElements(buffer, buf, 0);
    }
    
    return result;
}

/**
 * Bulk transfer
 */
JNIEXPORT jint JNICALL
Java_com_windroidpro_usb_NativeUsbManager_nativeBulkTransfer(
        JNIEnv *env,
        jobject /* this */,
        jint fd,
        jint endpoint,
        jbyteArray buffer,
        jint length,
        jint timeout) {
    
    if (fd < 0) {
        LOGE("Invalid file descriptor");
        return -1;
    }
    
    struct usbdevfs_bulktransfer bulk;
    bulk.ep = endpoint;
    bulk.len = length;
    bulk.timeout = timeout;

    // Optimization: Use stack buffer for small transfers to avoid JNI overhead
    // and prevent pinning large arrays during blocking I/O
    if (length <= SMALL_TRANSFER_THRESHOLD) {
        jbyte buf[SMALL_TRANSFER_THRESHOLD];

        // If OUT transfer (Host -> Device), copy data to stack buffer
        // Endpoint direction is in bit 7 (0x80)
        if ((endpoint & 0x80) == 0) {
            env->GetByteArrayRegion(buffer, 0, length, buf);
            if (env->ExceptionCheck()) return -1;
        }

        bulk.data = buf;
        int result = ioctl(fd, USBDEVFS_BULK, &bulk);

        if (result < 0) {
            LOGE("Bulk transfer failed: %s", strerror(errno));
        } else if ((endpoint & 0x80) != 0) {
            // If IN transfer (Device -> Host) and success, copy data back
            // ioctl returns number of bytes transferred
            env->SetByteArrayRegion(buffer, 0, result, buf);
            if (env->ExceptionCheck()) return -1;
        }
        return result;
    }
    
    // Fallback for larger transfers
    jbyte *buf = env->GetByteArrayElements(buffer, nullptr);
    if (!buf) return -1;

    bulk.data = buf;
    
    int result = ioctl(fd, USBDEVFS_BULK, &bulk);
    
    if (result < 0) {
        LOGE("Bulk transfer failed: %s", strerror(errno));
    }
    
    // Release the array. JNI_ABORT can be used for OUT transfers if we assume
    // GetByteArrayElements returns a copy (which we don't need to copy back).
    // For IN, we must commit changes, so we need 0.
    int mode = 0;
    if ((endpoint & 0x80) == 0) {
        mode = JNI_ABORT;
    }

    env->ReleaseByteArrayElements(buffer, buf, mode);
    return result;
}

/**
 * Claim interface
 */
JNIEXPORT jboolean JNICALL
Java_com_windroidpro_usb_NativeUsbManager_nativeClaimInterface(
        JNIEnv *env,
        jobject /* this */,
        jint fd,
        jint interfaceNumber) {
    
    if (fd < 0) {
        LOGE("Invalid file descriptor");
        return JNI_FALSE;
    }
    
    int result = ioctl(fd, USBDEVFS_CLAIMINTERFACE, &interfaceNumber);
    
    if (result < 0) {
        LOGE("Failed to claim interface %d: %s", interfaceNumber, strerror(errno));
        return JNI_FALSE;
    }
    
    LOGI("Interface %d claimed successfully", interfaceNumber);
    return JNI_TRUE;
}

/**
 * Release interface
 */
JNIEXPORT jboolean JNICALL
Java_com_windroidpro_usb_NativeUsbManager_nativeReleaseInterface(
        JNIEnv *env,
        jobject /* this */,
        jint fd,
        jint interfaceNumber) {
    
    if (fd < 0) {
        LOGE("Invalid file descriptor");
        return JNI_FALSE;
    }
    
    int result = ioctl(fd, USBDEVFS_RELEASEINTERFACE, &interfaceNumber);
    
    if (result < 0) {
        LOGE("Failed to release interface %d: %s", interfaceNumber, strerror(errno));
        return JNI_FALSE;
    }
    
    LOGI("Interface %d released successfully", interfaceNumber);
    return JNI_TRUE;
}

} // extern "C"