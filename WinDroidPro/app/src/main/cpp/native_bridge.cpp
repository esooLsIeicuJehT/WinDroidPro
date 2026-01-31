#include <jni.h>
#include <android/log.h>
#include <string>
#include <cstring>
#include <memory>
#include <vector>
#include <stdlib.h>
#include <malloc.h>
#include <dlfcn.h>
#include <unistd.h>
#include <sys/wait.h>
#include <map>
#include <mutex>
#include <sys/types.h>
#include <fcntl.h>
#include <vector>
#include <sstream>
#include <iostream>

#define LOG_TAG "WinDroidPro-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

// Forward declarations
extern "C" {
    // Wine integration
    int wine_init(const char* wine_prefix, const char* wine_arch);
    int wine_execute(const char* exe_path, const char* args, const char* working_dir);
    void wine_cleanup();
    
    // Box64 integration
    int box64_init(const char* lib_path);
    void* box64_load_library(const char* lib_name);
    void* box64_get_symbol(void* handle, const char* symbol_name);
    void box64_cleanup();
    
    // USB management
    int usb_init();
    int usb_attach_device(int vendor_id, int product_id, int fd);
    int usb_detach_device(int device_id);
    void usb_cleanup();
}

// JNI method implementations
extern "C" JNIEXPORT jstring JNICALL
Java_com_windroidpro_native_1bridge_NativeBridge_getVersion(JNIEnv* env, jobject /* this */) {
    return env->NewStringUTF("WinDroid Pro v1.0.0 - Native Bridge");
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_windroidpro_native_1bridge_NativeBridge_initializeWine(
    JNIEnv* env, jobject /* this */, jstring wine_prefix, jstring wine_arch) {
    
    const char* prefix = env->GetStringUTFChars(wine_prefix, nullptr);
    const char* arch = env->GetStringUTFChars(wine_arch, nullptr);
    
    LOGI("Initializing Wine with prefix: %s, arch: %s", prefix, arch);
    
    int result = wine_init(prefix, arch);
    
    env->ReleaseStringUTFChars(wine_prefix, prefix);
    env->ReleaseStringUTFChars(wine_arch, arch);
    
    if (result == 0) {
        LOGI("Wine initialized successfully");
        return JNI_TRUE;
    } else {
        LOGE("Wine initialization failed with code: %d", result);
        return JNI_FALSE;
    }
}

extern "C" JNIEXPORT jint JNICALL
Java_com_windroidpro_native_1bridge_NativeBridge_executeWineApp(
    JNIEnv* env, jobject /* this */, jstring exe_path, jstring args, jstring working_dir) {
    
    const char* exe = env->GetStringUTFChars(exe_path, nullptr);
    const char* arguments = env->GetStringUTFChars(args, nullptr);
    const char* work_dir = env->GetStringUTFChars(working_dir, nullptr);
    
    LOGI("Executing Wine app: %s with args: %s", exe, arguments);
    
    int result = wine_execute(exe, arguments, work_dir);
    
    env->ReleaseStringUTFChars(exe_path, exe);
    env->ReleaseStringUTFChars(args, arguments);
    env->ReleaseStringUTFChars(working_dir, work_dir);
    
    return result;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_windroidpro_native_1bridge_NativeBridge_initializeBox64(
    JNIEnv* env, jobject /* this */, jstring lib_path) {
    
    const char* path = env->GetStringUTFChars(lib_path, nullptr);
    
    LOGI("Initializing Box64 with library path: %s", path);
    
    int result = box64_init(path);
    
    env->ReleaseStringUTFChars(lib_path, path);
    
    if (result == 0) {
        LOGI("Box64 initialized successfully");
        return JNI_TRUE;
    } else {
        LOGE("Box64 initialization failed with code: %d", result);
        return JNI_FALSE;
    }
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_windroidpro_native_1bridge_NativeBridge_initializeUSB(
    JNIEnv* env, jobject /* this */) {
    
    LOGI("Initializing USB subsystem");
    
    int result = usb_init();
    
    if (result == 0) {
        LOGI("USB subsystem initialized successfully");
        return JNI_TRUE;
    } else {
        LOGE("USB initialization failed with code: %d", result);
        return JNI_FALSE;
    }
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_windroidpro_native_1bridge_NativeBridge_attachUSBDevice(
    JNIEnv* env, jobject /* this */, jint vendor_id, jint product_id, jint fd) {
    
    LOGI("Attaching USB device: VID=%04x, PID=%04x, FD=%d", vendor_id, product_id, fd);
    
    int result = usb_attach_device(vendor_id, product_id, fd);
    
    if (result >= 0) {
        LOGI("USB device attached successfully with ID: %d", result);
        return JNI_TRUE;
    } else {
        LOGE("USB device attachment failed with code: %d", result);
        return JNI_FALSE;
    }
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_windroidpro_native_1bridge_NativeBridge_detachUSBDevice(
    JNIEnv* env, jobject /* this */, jint device_id) {
    
    LOGI("Detaching USB device with ID: %d", device_id);
    
    int result = usb_detach_device(device_id);
    
    if (result == 0) {
        LOGI("USB device detached successfully");
        return JNI_TRUE;
    } else {
        LOGE("USB device detachment failed with code: %d", result);
        return JNI_FALSE;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_windroidpro_native_1bridge_NativeBridge_cleanup(
    JNIEnv* env, jobject /* this */) {
    
    LOGI("Cleaning up native resources");
    
    wine_cleanup();
    box64_cleanup();
    usb_cleanup();
    
    LOGI("Native cleanup completed");
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_windroidpro_native_1bridge_NativeBridge_setBox64Config(
    JNIEnv* env, jobject /* this */, jstring preset) {
    const char* preset_str = env->GetStringUTFChars(preset, nullptr);
    LOGI("Setting Box64 configuration for preset: %s", preset_str);

    if (strcasecmp(preset_str, "performance") == 0) {
        setenv("BOX64_DYNAREC", "1", 1);
        setenv("BOX64_DYNAREC_STRONGMEM", "1", 1);
        setenv("BOX64_DYNAREC_BIGBLOCK", "1", 1);
        setenv("BOX64_DYNAREC_FORWARD", "1024", 1);
    } else if (strcasecmp(preset_str, "stability") == 0) {
        setenv("BOX64_DYNAREC", "1", 1);
        setenv("BOX64_DYNAREC_SAFE", "1", 1);
        setenv("BOX64_DYNAREC_BIGBLOCK", "0", 1);
    } else {
        // Balanced
        setenv("BOX64_DYNAREC", "1", 1);
        setenv("BOX64_DYNAREC_BIGBLOCK", "1", 1);
    }

    env->ReleaseStringUTFChars(preset, preset_str);
    return JNI_TRUE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_windroidpro_native_1bridge_NativeBridge_optimizeMemory(
    JNIEnv* env, jobject /* this */) {
    LOGI("Optimizing memory usage");
    // Trim memory using mallopt if available
    #ifdef M_TRIM_THRESHOLD
    mallopt(M_TRIM_THRESHOLD, -1);
    mallopt(M_MMAP_THRESHOLD, 128*1024);
    #endif
    return JNI_TRUE;
}

// Helper function to tokenize arguments respecting quotes
std::vector<std::string> tokenize_args(const char* args) {
    std::vector<std::string> tokens;
    if (!args) return tokens;

    std::string str = args;
    std::string current_token;
    bool in_quote = false;
    char quote_char = 0;

    for (char c : str) {
        if (in_quote) {
            if (c == quote_char) {
                in_quote = false;
            } else {
                current_token += c;
            }
        } else {
            if (c == '"' || c == '\'') {
                in_quote = true;
                quote_char = c;
            } else if (c == ' ') {
                if (!current_token.empty()) {
                    tokens.push_back(current_token);
                    current_token.clear();
                }
            } else {
                current_token += c;
            }
        }
    }
    if (!current_token.empty()) {
        tokens.push_back(current_token);
    }
    return tokens;
}

// Globals for USB management
struct UsbDevice {
    int vendor_id;
    int product_id;
    int fd;
};

std::map<int, UsbDevice> g_usb_devices;
std::mutex g_usb_mutex;
int g_next_device_id = 1;

// Globals for Box64
void* g_box64_handle = nullptr;

// Actual implementations
extern "C" {
    int wine_init(const char* wine_prefix, const char* wine_arch) {
        LOGI("Initializing Wine environment");
        if (!wine_prefix || !wine_arch) return -1;

        setenv("WINEPREFIX", wine_prefix, 1);
        setenv("WINEARCH", wine_arch, 1);
        // Ensure standard paths are in place if needed
        return 0;
    }
    
    int wine_execute(const char* exe_path, const char* args, const char* working_dir) {
        LOGI("Executing Wine: %s %s", exe_path, args);

        // Prepare arguments
        std::vector<std::string> tokens = tokenize_args(args);
        std::vector<char*> argv;
        argv.push_back(const_cast<char*>("wine"));
        argv.push_back(const_cast<char*>(exe_path));
        for (const auto& token : tokens) {
            argv.push_back(const_cast<char*>(token.c_str()));
        }
        argv.push_back(nullptr);

        pid_t pid = fork();
        if (pid == -1) {
            LOGE("Fork failed");
            return -1;
        } else if (pid == 0) {
            // Child process
            if (working_dir && *working_dir) {
                if (chdir(working_dir) != 0) {
                    LOGE("Failed to change directory to %s", working_dir);
                }
            }

            execvp("wine", argv.data());

            // If execvp returns, it failed
            LOGE("execvp failed: %s", strerror(errno));
            _exit(127);
        } else {
            // Parent process
            int status;
            if (waitpid(pid, &status, 0) == -1) {
                LOGE("waitpid failed");
                return -1;
            }

            if (WIFEXITED(status)) {
                return WEXITSTATUS(status);
            } else {
                return -1;
            }
        }
    }
    
    void wine_cleanup() {
        LOGI("Cleaning up Wine environment");
        unsetenv("WINEPREFIX");
        unsetenv("WINEARCH");
    }
    
    int box64_init(const char* lib_path) {
        LOGI("Initializing Box64 with library: %s", lib_path);
        if (g_box64_handle) {
            LOGI("Box64 already initialized");
            return 0;
        }

        g_box64_handle = dlopen(lib_path, RTLD_LAZY);
        if (!g_box64_handle) {
            LOGE("Failed to load Box64: %s", dlerror());
            return -1;
        }
        return 0;
    }
    
    void* box64_load_library(const char* lib_name) {
        LOGI("Box64 loading library: %s", lib_name);
        void* handle = dlopen(lib_name, RTLD_LAZY);
        if (!handle) {
            LOGE("Failed to load library %s: %s", lib_name, dlerror());
        }
        return handle;
    }
    
    void* box64_get_symbol(void* handle, const char* symbol_name) {
        if (!handle || !symbol_name) return nullptr;
        return dlsym(handle, symbol_name);
    }
    
    void box64_cleanup() {
        LOGI("Cleaning up Box64");
        if (g_box64_handle) {
            dlclose(g_box64_handle);
            g_box64_handle = nullptr;
        }
    }
    
    int usb_init() {
        LOGI("Initializing USB map");
        std::lock_guard<std::mutex> lock(g_usb_mutex);
        g_usb_devices.clear();
        return 0;
    }
    
    int usb_attach_device(int vendor_id, int product_id, int fd) {
        LOGI("Attaching USB device VID:%x PID:%x FD:%d", vendor_id, product_id, fd);
        std::lock_guard<std::mutex> lock(g_usb_mutex);

        int device_id = g_next_device_id++;
        g_usb_devices[device_id] = {vendor_id, product_id, fd};
        return device_id;
    }
    
    int usb_detach_device(int device_id) {
        LOGI("Detaching USB device ID:%d", device_id);
        std::lock_guard<std::mutex> lock(g_usb_mutex);

        auto it = g_usb_devices.find(device_id);
        if (it != g_usb_devices.end()) {
            g_usb_devices.erase(it);
            return 0;
        }
        return -1;
    }
    
    void usb_cleanup() {
        LOGI("Cleaning up USB map");
        std::lock_guard<std::mutex> lock(g_usb_mutex);
        g_usb_devices.clear();
    }
}