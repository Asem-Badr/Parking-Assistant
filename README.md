
# 🚗 ParkingAssistant – Smart Automotive Parking System

**ParkingAssistant** is a real-time parking aid system built on the **Android Open Source Project (AOSP)** and deployed on a **Raspberry Pi 4**. The system enhances safety and convenience by detecting obstacles, visualizing steering direction, and providing both visual and audible feedback to assist drivers in tight or low-visibility parking situations.

> According to the National Safety Council, nearly **20% of vehicle accidents** occur in parking lots. This system addresses this challenge by offering drivers real-time awareness of their environment.

---

## 🧭 Table of Contents

- [Features](#features)
- [System Overview](#system-overview)
- [Architecture](#architecture)
- [Hardware Components](#hardware-components)
- [Software Stack](#software-stack)
- [Data Flow](#data-flow)
- [AOSP Integration](#aosp-integration)
- [Challenges & Solutions](#challenges--solutions)
- [Videos](#videos)
- [Presentation](#presentation)

---

## ✨ Features

| Function | Description |
|---------|-------------|
| 🟢 **Distance Visualization** | Ultrasonic sensors detect obstacles and display them using color-coded levels: Green, Yellow, Orange, Red. |
| 🔊 **Buzzer Alerts** | Audible beeps increase in intensity as the object gets closer. |
| 🛞 **Steering Wheel Feedback** | Real-time visualization of vehicle steering direction using a potentiometer input. |
| 📱 **Modern UI** | Kotlin-based Android application following the MVVM pattern with smooth, real-time updates. |
| 🔧 **System Services** | All sensor logic handled via AIDL-based system services integrated into AOSP. |
| 🔒 **SELinux & Init Integration** | Custom services started at boot via `init.rc` with required SELinux permissions. |

---

## 🔍 System Overview

- **Goal**: Assist drivers in tight parking spots by improving spatial awareness using sensor data and clear feedback.
- **Target Use Cases**:  
  - Crowded urban parking  
  - Nighttime or foggy weather  
  - Drivers with limited visibility or beginner-level experience

---

## 🧱 Architecture

### 🔄 High-Level Architecture

```plaintext
+-------------+       +-------------+       +-------------+       +--------+
| MainActivity| <---> |   Services  | <---> |    HAL      | <---> | Sensor |
|  (UI Layer) |       | (AIDL IPC)  |       |  (I2C ADC)  |       | Layer  |
+-------------+       +-------------+       +-------------+       +--------+
```

## 🧩 Hardware Components

| Component                | Role                                            |
| ------------------------ | ----------------------------------------------- |
| **Raspberry Pi 4**       | Hosts Android system and runs custom services   |
| **Ultrasonic Sensors**   | Obstacle detection on multiple directions       |
| **Linear Potentiometer** | Mimics steering wheel rotation                  |
| **ADS1115 ADC**          | Converts analog potentiometer signal to digital |
| **Buzzer**               | Audible alerts based on proximity               |
| **Touchscreen Display**  | Interface for real-time visualization           |

---

## 💻 Software Stack

| Layer              | Tech                                                         |
| ------------------ | ------------------------------------------------------------ |
| UI                 | Android (Kotlin), MVVM, LiveData, Canvas                     |
| Communication      | AIDL between app and system services                         |
| Services           | `SteeringWheelService`, `UltrasonicService`, `BuzzerService` |
| HAL                | Direct I2C communication for sensor reading                  |
| System Integration | `init.rc`, SELinux policy updates                            |
| OS                 | AOSP (customized for Raspberry Pi 4)                         |

---

## 🔁 Data Flow

1. User opens the app (auto-start or manually).
2. App binds to AIDL system services.
3. Services fetch:

   * Steering data via ADS1115
   * Distance data via ultrasonic sensors
4. App displays:

   * Steering direction (angle arc)
   * Distance levels (color bands)
   * Sounds buzzer (if alert level is high)

```text
[App UI] → [AIDL Call] → [Service] → [HAL / Sensor] → [Processed Data] → [UI Update + Buzzer]
```

---

## 🧰 AOSP Integration

### 📂 System Services

* Services are implemented in the system layer and registered via **ServiceManager**.
* Each service has a corresponding AIDL interface:

  * `ISteeringWheelService.aidl`
  * `IUltrasonicDistanceService.aidl`
  * `IBuzzerService.aidl`

### ⚙️ Init.rc Integration

* Each service is started at boot by adding entries in:

  ```
  device/bcrm/rpi4/init.rpi4.rc
  ```

Example:

```rc
service ultrasonic-service /vendor/bin/hw/ultrasonic-service
    interface aidl com.luxoft.ultrasonic.IUltrasonic/default
    class main
    user root
    group root
    shutdown critical
    oneshot

on boot
    ./vendor/bin/hw/ultrasonic-service
```

### 🔐 SELinux

* SELinux permissions were customized to allow:

  * Access to I2C devices
  * Service startup under custom domains
  * System-to-app communication over AIDL

---

## 🧠 Challenges & Solutions

| Problem                 | Solution                                                                                                 |
| ----------------------- | -------------------------------------------------------------------------------------------------------- |
| Sensor fluctuations     | Implemented hysteresis logic for smoother value transitions                                              |
| UI lag                  | Used Kotlin coroutines and LiveData observers to ensure UI remains responsive                            |
| Service start issues    | Used `init.rc` with correct SELinux labels for persistent startup                                        |
| Multi-sensor noise      | Filtered ultrasonic data with debounce logic                                                             |
| Integration without APK | Built app inside AOSP as part of `packages/apps/` and added to build using `Android.bp` blueprint format |

---

## 🌟 videos




<p align="center">
	<img src="https://github.com/user-attachments/assets/d30b3e69-a0a9-404f-8512-e589d14369fa" width=40% height=60% />
</p>

[Watch the video](https://www.linkedin.com/posts/information-technology-institute-iti-_itida-mcit-iti-ugcPost-7290012779057483777-x6kE)


---

## 📄 Presentation

[Presentation](https://www.canva.com/design/DAGY0Iuepp8/oSEVhZF_dR2yWAMH42Yv_w/edit?utm_content=DAGY0Iuepp8&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)

---
