#
# Copyright (C) 2021-2023 KonstaKANG
#
# SPDX-License-Identifier: Apache-2.0
#

# Inherit device configuration
$(call inherit-product, device/brcm/rpi4/device.mk)

DEVICE_CAR_PATH := device/brcm/rpi4/car

PRODUCT_AAPT_CONFIG := normal mdpi hdpi
PRODUCT_AAPT_PREF_CONFIG := hdpi
PRODUCT_CHARACTERISTICS := automotive,nosdcard
$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base.mk)
$(call inherit-product, packages/services/Car/car_product/build/car.mk)

# Audio
PRODUCT_PACKAGES += \
    android.hardware.automotive.audiocontrol-service.example 
    
PRODUCT_COPY_FILES += \
    $(DEVICE_CAR_PATH)/car_audio_configuration.xml:$(TARGET_COPY_OUT_VENDOR)/etc/car_audio_configuration.xml

# Bluetooth
PRODUCT_VENDOR_PROPERTIES += \
    bluetooth.device.class_of_device=38,4,8 \
    bluetooth.profile.a2dp.source.enabled=false \
    bluetooth.profile.asha.central.enabled=false \
    bluetooth.profile.bap.broadcast.assist.enabled=false \
    bluetooth.profile.bap.unicast.client.enabled=false \
    bluetooth.profile.bas.client.enabled=false \
    bluetooth.profile.ccp.server.enabled=false \
    bluetooth.profile.csip.set_coordinator.enabled=false \
    bluetooth.profile.hap.client.enabled=false \
    bluetooth.profile.hfp.ag.enabled=false \
    bluetooth.profile.hid.device.enabled=false \
    bluetooth.profile.hid.host.enabled=false \
    bluetooth.profile.map.client.enabled=false \
    bluetooth.profile.map.server.enabled=false \
    bluetooth.profile.mcp.server.enabled=false \
    bluetooth.profile.opp.enabled=false \
    bluetooth.profile.pbap.server.enabled=false \
    bluetooth.profile.sap.server.enabled=false \
    bluetooth.profile.vcp.controller.enabled=false

# Broadcast radio
PRODUCT_PACKAGES += \
    android.hardware.broadcastradio-service.default

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.broadcastradio.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.broadcastradio.xml

# Camera
ENABLE_CAMERA_SERVICE := true

# CAN
PRODUCT_PACKAGES += \
    android.hardware.automotive.can-service

PRODUCT_PACKAGES += \
    canhalctrl \
    canhaldump \
    canhalsend

# Display
PRODUCT_COPY_FILES += \
    $(DEVICE_CAR_PATH)/display_settings.xml:$(TARGET_COPY_OUT_VENDOR)/etc/display_settings.xml

# EVS
ENABLE_CAREVSSERVICE_SAMPLE := true
ENABLE_EVS_SAMPLE := true
ENABLE_EVS_SERVICE := true
ENABLE_REAR_VIEW_CAMERA_SAMPLE := true

PRODUCT_COPY_FILES += \
    $(DEVICE_CAR_PATH)/evs_config_override.json:${TARGET_COPY_OUT_VENDOR}/etc/automotive/evs/config_override.json

# Occupant awareness
PRODUCT_PACKAGES += \
    android.hardware.automotive.occupant_awareness@1.0-service

include packages/services/Car/car_product/occupant_awareness/OccupantAwareness.mk

# Overlays
PRODUCT_PACKAGES += \
    AndroidRpiOverlay \
    CarServiceRpiOverlay \
    SettingsProviderRpiOverlay \
    WifiRpiOverlay \
  

# Permissions
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.software.activities_on_secondary_displays.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.software.activities_on_secondary_displays.xml \
    frameworks/native/data/etc/car_core_hardware.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/car_core_hardware.xml

# Vehicle
PRODUCT_PACKAGES += \
    android.hardware.automotive.vehicle@V3-default-service

# Device identifier. This must come after all inclusions.
PRODUCT_DEVICE := rpi4
PRODUCT_NAME := aosp_rpi4_car
PRODUCT_BRAND := Raspberry
PRODUCT_MODEL := Raspberry Pi 4
PRODUCT_MANUFACTURER := Raspberry
PRODUCT_RELEASE_NAME := Raspberry Pi 4

# include device/brcm/iti/iti_rpi4_auto.mk

#####################################################################
PRODUCT_PACKAGES += \
    ultrasonic-service \
    UltrasonicAidlTest \
    

BOARD_VENDOR_SEPOLICY_DIRS += device/brcm/rpi4/ultrasonichal/sepolicy/vendor 
BOARD_VENDOR_SEPOLICY_DIRS += device/brcm/rpi4/ultrasonichal/sepolicy/hal 
BOARD_VENDOR_SEPOLICY_DIRS += device/brcm/rpi4/ultrasonichal/sepolicy/service 
BOARD_VENDOR_SEPOLICY_DIRS += device/brcm/rpi4/ultrasonichal/sepolicy/daemon 
BOARD_VENDOR_SEPOLICY_DIRS += device/brcm/rpi4/ultrasonichal/sepolicy/app 


# ########## Device Manifest & Framework Compatibility Matrix Manifest ##########


DEVICE_FRAMEWORK_COMPATIBILITY_MATRIX_FILE += \
    ultrasonichal/ultrasonic_service/manifest/ultrasonic_framework_compatibility_matrix.xml \
