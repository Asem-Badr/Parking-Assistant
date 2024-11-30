#include "SteeringWheelImpl.h"
#include "I2CHal.h"
#include <android-base/logging.h>

namespace aidl {
namespace com {
namespace luxoft {
namespace steeringwheel {

// Helper function to map ADC value to angle
static int32_t mapAdcToAngle(uint16_t adcValue) {
    constexpr int32_t maxAdcValue = 26360; // 16-bit max
    constexpr int32_t maxAngle = 180;     // Desired angle range
    return (adcValue * maxAngle) / maxAdcValue;
}

// Implement getSteeringWheelAngle
ndk::ScopedAStatus SteeringWheelImpl::getSteeringWheelAngle(int32_t* _aidl_return) {
    if (!_aidl_return) {
        return ndk::ScopedAStatus::fromStatus(STATUS_BAD_VALUE);
    }

    LOG(INFO) << "getSteeringWheelAngle called!";
    
    // Example reading from ADC
    uint16_t adcValue = getRpm(); // Assuming this returns a 16-bit ADC value
    if(adcValue==65535) adcValue=0;

    // Map the ADC value to an angle
    int32_t angle = mapAdcToAngle(adcValue);

    *_aidl_return =angle ;  // Set the mapped angle as the return value
    return ndk::ScopedAStatus::ok();
}

}  // namespace steeringwheel
}  // namespace luxoft
}  // namespace com
}  // namespace aidl

