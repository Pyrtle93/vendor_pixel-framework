# Inherit from pixel-framework config
TARGET_INCLUDE_PIXEL_FRAMEWORKS ?= true
ifeq ($(TARGET_INCLUDE_PIXEL_FRAMEWORKS),true)
PRODUCT_PACKAGES += \
    SystemUIGoogle \
    SettingsGoogle
endif
