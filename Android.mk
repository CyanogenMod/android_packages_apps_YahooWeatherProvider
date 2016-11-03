LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := YahooWeatherProvider
LOCAL_MODULE_TAGS := optional
LOCAL_PACKAGE_NAME := YahooWeatherProvider

yahoo_root  := $(LOCAL_PATH)
yahoo_dir   := app
yahoo_out   := $(OUT_DIR)/target/common/obj/APPS/$(LOCAL_MODULE)_intermediates
yahoo_build := $(yahoo_root)/$(yahoo_dir)/build
yahoo_apk   := build/outputs/apk/$(yahoo_dir)-release-unsigned.apk

$(yahoo_root)/$(yahoo_dir)/$(yahoo_apk):
	rm -Rf $(yahoo_build)
	mkdir -p $(yahoo_out)
	ln -sf $(yahoo_out) $(yahoo_build)
	cd $(yahoo_root)/$(yahoo_dir) && JAVA_TOOL_OPTIONS="$(JAVA_TOOL_OPTIONS) -Dfile.encoding=UTF8" ../gradlew assembleRelease

LOCAL_CERTIFICATE := platform
LOCAL_SRC_FILES := $(yahoo_dir)/$(yahoo_apk)
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)

include $(BUILD_PREBUILT)
