#include <jni.h>
#include <string>
#include <android/log.h>
#include <linux/usbdevice_fs.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <unistd.h>

#define LOG_TAG "UsbManager"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

/**
 * Open USB device
 */
JNIEXPORT jint JNICALL
Java_com_windroidpro_usb_UsbManager_nativeOpenDevice(
        JNIEnv *env,
        jobject /* this */,
        jstring devicePath) {
    
    const char *path = env->GetStringUTFChars(devicePath, nullptr);
    LOGI("Opening USB device: %s", path);
    
    int fd = open(path, O_RDWR);
    if (fd < 0) {
        LOGE("Failed to open USB device: %s", strerror(errno));
        env->ReleaseStringUTFChars(devicePath, path);
        return -1;
    }
    
    env->ReleaseStringUTFChars(devicePath, path);
    LOGI("USB device opened successfully, fd: %d", fd);
    return fd;
}

/**
 * Close USB device
 */
JNIEXPORT void JNICALL
Java_com_windroidpro_usb_UsbManager_nativeCloseDevice(
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
Java_com_windroidpro_usb_UsbManager_nativeReadDevice(
        JNIEnv *env,
        jobject /* this */,
        jint fd,
        jbyteArray buffer,
        jint length) {
    
    if (fd < 0) {
        LOGE("Invalid file descriptor");
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
Java_com_windroidpro_usb_UsbManager_nativeWriteDevice(
        JNIEnv *env,
        jobject /* this */,
        jint fd,
        jbyteArray buffer,
        jint length) {
    
    if (fd < 0) {
        LOGE("Invalid file descriptor");
        return -1;
    }
    
    jbyte *buf = env->GetByteArrayElements(buffer, nullptr);
    int bytes_written = write(fd, buf, length);
    
    if (bytes_written < 0) {
        LOGE("Failed to write to USB device: %s", strerror(errno));
    }
    
    env->ReleaseByteArrayElements(buffer, buf, JNI_ABORT);
    return bytes_written;
}

/**
 * Control transfer
 */
JNIEXPORT jint JNICALL
Java_com_windroidpro_usb_UsbManager_nativeControlTransfer(
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
Java_com_windroidpro_usb_UsbManager_nativeBulkTransfer(
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
    
    jbyte *buf = env->GetByteArrayElements(buffer, nullptr);
    bulk.data = buf;
    
    int result = ioctl(fd, USBDEVFS_BULK, &bulk);
    
    if (result < 0) {
        LOGE("Bulk transfer failed: %s", strerror(errno));
    }
    
    env->ReleaseByteArrayElements(buffer, buf, 0);
    return result;
}

/**
 * Claim interface
 */
JNIEXPORT jboolean JNICALL
Java_com_windroidpro_usb_UsbManager_nativeClaimInterface(
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
Java_com_windroidpro_usb_UsbManager_nativeReleaseInterface(
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