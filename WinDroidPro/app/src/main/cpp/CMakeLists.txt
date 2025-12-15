cmake_minimum_required(VERSION 3.22.1)

project("windroidpro")

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Compiler flags
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -Wextra -O3 -fPIC")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -O3 -fPIC -frtti -fexceptions")

# Architecture-specific optimizations
if(${ANDROID_ABI} STREQUAL "arm64-v8a")
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -march=armv8-a+crypto")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -march=armv8-a+crypto")
elseif(${ANDROID_ABI} STREQUAL "armeabi-v7a")
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -march=armv7-a -mfpu=neon")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -march=armv7-a -mfpu=neon")
endif()

# Include directories
include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR}/wine
    ${CMAKE_CURRENT_SOURCE_DIR}/box64
    ${CMAKE_CURRENT_SOURCE_DIR}/usb
)

# Source files
file(GLOB_RECURSE NATIVE_SOURCES
    "native_bridge.cpp"
    "wine_wrapper.cpp"
    "box64_wrapper.cpp"
    "usb_manager.cpp"
    "file_system.cpp"
    "process_manager.cpp"
    "graphics_bridge.cpp"
    "audio_bridge.cpp"
    "input_handler.cpp"
    "wsl_manager.cpp"
    "utils.cpp"
)

# Create native library
add_library(${CMAKE_PROJECT_NAME} SHARED ${NATIVE_SOURCES})

# Find required libraries
find_library(log-lib log)
find_library(android-lib android)
find_library(EGL-lib EGL)
find_library(GLESv3-lib GLESv3)
find_library(vulkan-lib vulkan)
find_library(z-lib z)

# Link libraries
target_link_libraries(${CMAKE_PROJECT_NAME}
    ${log-lib}
    ${android-lib}
    ${EGL-lib}
    ${GLESv3-lib}
    ${vulkan-lib}
    ${z-lib}
)

# Add prebuilt libraries (Wine, Box64, etc.)
# These will be extracted from assets at runtime
add_library(wine SHARED IMPORTED)
set_target_properties(wine PROPERTIES IMPORTED_LOCATION
    ${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libwine.so)

add_library(box64 SHARED IMPORTED)
set_target_properties(box64 PROPERTIES IMPORTED_LOCATION
    ${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libbox64.so)

# Link prebuilt libraries
target_link_libraries(${CMAKE_PROJECT_NAME}
    wine
    box64
)