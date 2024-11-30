#include "BuzzerImpl.h"

#include <android-base/logging.h>

namespace aidl {
namespace com {
namespace luxoft {
namespace buzzer {



BuzzerImpl::BuzzerImpl() 
    : mBuzzerHal{BuzzerHal(18)} 
{
    for (int i = 0; i < 1; ++i) {
        if (!mBuzzerHal[i].initialize()) {
            LOG(ERROR) << "Failed to initialize ultrasonic sensor " << i;
        }
    }
}


ndk::ScopedAStatus BuzzerImpl::setBuzzerLevel(int32_t buzzerLevel, bool* _aidl_return) {
    if (!_aidl_return) {
        return ndk::ScopedAStatus::fromStatus(STATUS_BAD_VALUE);
    }
    // Delegate the level setting to the HAL
    bool success = mBuzzerHal[0].setBuzzerLevel(buzzerLevel);
    if (!success) {
        LOG(ERROR) << "BuzzerImpl Failed to initialize the Buzzer HAL" << buzzerLevel;
        *_aidl_return = false;
        return ndk::ScopedAStatus::fromStatus(STATUS_UNKNOWN_ERROR);
    }
    *_aidl_return = true;
    return ndk::ScopedAStatus::ok();
}



}  // namespace buzzer
}  // namespace luxoft
}  // namespace com
}  // namespace aidl
