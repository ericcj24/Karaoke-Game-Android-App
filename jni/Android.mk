LOCAL_PATH := $(call my-dir)

# cross-compiled fftw3 library. see build.sh in fftw3 source directory for compiler commands
include $(CLEAR_VARS)
LOCAL_MODULE := fftw3
LOCAL_SRC_FILES := ./fftw3/lib/libfftw3.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/fftw3/include
include $(PREBUILT_STATIC_LIBRARY)

# our processing
include $(CLEAR_VARS)
LOCAL_MODULE   := process
LOCAL_SRC_FILES := process.c
LOCAL_LDLIBS := -llog -lm
LOCAL_STATIC_LIBRARIES := fftw3
include $(BUILD_SHARED_LIBRARY)