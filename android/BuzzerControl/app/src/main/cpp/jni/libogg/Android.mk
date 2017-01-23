LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libogg
LOCAL_CFLAGS += -I$(LOCAL_PATH)/../include -ffast-math -fsigned-char -ansi -Wno-implicit-int -Wno-return-type  -Wno-pointer-sign -std=gnu89
ifeq ($(TARGET_ARCH),arm)
	LOCAL_CFLAGS += -march=armv6 -marm -mfloat-abi=softfp -mfpu=vfp
endif


LOCAL_SRC_FILES := \
	bitwise.c \
	framing.c

include $(BUILD_SHARED_LIBRARY)
