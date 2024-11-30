#pragma once

#include <aidl/com/luxoft/buzzer/BnBuzzer.h>
#include "BuzzerHal.h"
#include <mutex>

namespace aidl {
namespace com {
namespace luxoft {
namespace buzzer {

class BuzzerImpl : public BnBuzzer {

BuzzerHal mBuzzerHal[1] ;

public:
    BuzzerImpl();
    virtual ndk::ScopedAStatus setBuzzerLevel(int32_t buzzerLevel, bool* _aidl_return) override;

protected:

    ::ndk::ScopedAIBinder_DeathRecipient death_recipient_;
    static void binderDiedCallbackAidl(void* cookie_ptr);
};

}  // namespace buzzer
}  // namespace luxoft
}  // namespace com
}  // namespace aidl
