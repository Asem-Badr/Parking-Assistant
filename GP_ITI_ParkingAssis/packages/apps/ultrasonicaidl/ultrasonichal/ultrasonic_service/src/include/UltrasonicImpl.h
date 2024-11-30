#pragma once

#include <aidl/com/luxoft/ultrasonic/BnUltrasonic.h>
#include "UltrasonicHal.h"
#include <android-base/logging.h>
#include <mutex>


namespace aidl {
namespace com {
namespace luxoft {
namespace ultrasonic {

#define ULTRASONIC_SENSOR_NUMBER 2

class UltrasonicImpl : public BnUltrasonic {
    UltrasonicHal sensor[ULTRASONIC_SENSOR_NUMBER];  // Ultrasonic sensor array

public:
    UltrasonicImpl();

    // Override the AIDL interface methods
    virtual ndk::ScopedAStatus getUltrasonicReading(int32_t sensorNumber, int32_t* _aidl_return) override;

private:
    ::ndk::ScopedAIBinder_DeathRecipient death_recipient_;
    static void binderDiedCallbackAidl(void* cookie_ptr);
};

}  // namespace ultrasonic
}  // namespace luxoft
}  // namespace com
}  // namespace aidl
