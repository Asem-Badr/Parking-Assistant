#include "UltrasonicImpl.h"

namespace aidl {
namespace com {
namespace luxoft {
namespace ultrasonic {

UltrasonicImpl::UltrasonicImpl() 
    : sensor{{23, 24}, 
            {25, 26}}  // Replace with actual pin mappings
{
    if (!sensor[0].initialize() || !sensor[1].initialize()) {
        LOG(ERROR) << "Failed to initialize ultrasonic sensors.";
    }
}

ndk::ScopedAStatus UltrasonicImpl::getUltrasonicReading(int32_t sensorNumber, int32_t* _aidl_return) {
    if (!_aidl_return) {
        return ndk::ScopedAStatus::fromStatus(STATUS_BAD_VALUE);
    }

    if (sensorNumber < 0 || sensorNumber >= ULTRASONIC_SENSOR_NUMBER) {
        LOG(ERROR) << "Invalid sensor number: " << sensorNumber;
        return ndk::ScopedAStatus::fromStatus(STATUS_BAD_VALUE);
    }

    float distance = sensor[sensorNumber].measureDistance();
    if (distance < 0) {
        LOG(ERROR) << "Failed to measure distance for sensor: " << sensorNumber;
        return ndk::ScopedAStatus::fromStatus(STATUS_UNKNOWN_ERROR);
    }

    LOG(INFO) << "Sensor " << sensorNumber << " Distance: " << distance << " cm";
    *_aidl_return = static_cast<int32_t>(distance);
    return ndk::ScopedAStatus::ok();
}

void UltrasonicImpl::binderDiedCallbackAidl(void* cookie_ptr) {
    LOG(WARNING) << "Binder died; cleanup or reset logic may be needed.";
}

}  // namespace ultrasonic
}  // namespace luxoft
}  // namespace com
}  // namespace aidl
