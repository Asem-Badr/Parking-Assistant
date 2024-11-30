#pragma once

#include <aidl/com/luxoft/steeringwheel/BnSteeringWheel.h>
#include <mutex>


namespace aidl {
namespace com {
namespace luxoft {
namespace steeringwheel {


class SteeringWheelImpl : public BnSteeringWheel {

    // Implement get_steeringwheel_angle
    virtual ndk::ScopedAStatus getSteeringWheelAngle(int32_t* _aidl_return) override;


protected:

    ::ndk::ScopedAIBinder_DeathRecipient death_recipient_;
    static void binderDiedCallbackAidl(void* cookie_ptr);

};

}  // namespace hello
}  // namespace hardware
}  // namespace aospinsight
}  // namespace gpio
