LOCAL_PATH := $(call my-dir)

LOCAL_C_INCLUDE := $(LOCAL_PATH)/include
LOCAL_CFLAGS += -ansi -Wno-implicit-int -Wno-return-type

include $(addprefix $(LOCAL_PATH)/, $(addsuffix /Android.mk, \
	libogg \
	libvorbis	\
	libshout \
	libvorbis-jni \
	libshout-jni \
	liblame-jni \
))
