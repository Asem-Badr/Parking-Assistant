#include "UltrasonicImpl.h"

#include <android-base/logging.h>


namespace aidl {
namespace com {
namespace luxoft {
namespace ultrasonic {


// Constructor to initialize sensors
UltrasonicImpl::UltrasonicImpl() 
    : sensor{UltrasonicSensor(16, 20), UltrasonicSensor(21, 26),UltrasonicSensor(19, 13),
             UltrasonicSensor(6, 5), UltrasonicSensor(22, 4),UltrasonicSensor(7,25 )} 
{
    for (int i = 0; i < 6; ++i) {
        if (!sensor[i].initialize()) {
            LOG(ERROR) << "Failed to initialize ultrasonic sensor " << i;
        }
    }
}


 ndk::ScopedAStatus UltrasonicImpl::getUltrasonicReading(int32_t sensorNumber,int32_t* _aidl_return) {
 if (!_aidl_return) {
        return ndk::ScopedAStatus::fromStatus(STATUS_BAD_VALUE);
    }

    float distance = -1;

    if (sensorNumber < 0 || sensorNumber >= ULTRASONIC_SENSOR_NUMBER) {
    LOG(ERROR) << "Invalid sensor number: " << sensorNumber;
    *_aidl_return = static_cast<int32_t>(-2);
    return ndk::ScopedAStatus::ok();
    }
    else {
        distance = sensor[sensorNumber].measureDistance();
    }
    
    //  if (distance > 300) {
    //     LOG(ERROR) << "Failed to measure (greater than 300) distance for sensor: " << sensorNumber;
    //     *_aidl_return = static_cast<int32_t>(-1);
    //     return ndk::ScopedAStatus::ok();
    // }

    // if (distance < 0) {
    //     LOG(ERROR) << "Failed to measure distance for sensor: " << sensorNumber;
    //     *_aidl_return = static_cast<int32_t>(-2);
    //     return ndk::ScopedAStatus::ok();
    // }

    LOG(INFO) << "Sensor " << sensorNumber << " Distance: " << distance << " cm";
    *_aidl_return = static_cast<int32_t>(distance);
    return ndk::ScopedAStatus::ok();
}


}  // namespace hello
}  // namespace hardware
}  // namespace aospinsight
}  // namespace aidl
