#pragma once

#include <aidl/com/luxoft/ultrasonic/BnUltrasonic.h>
#include "UltrasonicSensor.h"
#include <mutex>



namespace aidl {
namespace com {
namespace luxoft {
namespace ultrasonic {


#define ULTRASONIC_SENSOR_NUMBER 6

class UltrasonicImpl : public BnUltrasonic {

    UltrasonicSensor sensor[ULTRASONIC_SENSOR_NUMBER];

public:
    UltrasonicImpl();
 virtual ndk::ScopedAStatus getUltrasonicReading(int32_t sensorNumber,int32_t* _aidl_return) override;

   
protected:

    ::ndk::ScopedAIBinder_DeathRecipient death_recipient_;
    static void binderDiedCallbackAidl(void* cookie_ptr);

};

}  // namespace hello
}  // namespace hardware
}  // namespace aospinsight
}  // namespace gpio
