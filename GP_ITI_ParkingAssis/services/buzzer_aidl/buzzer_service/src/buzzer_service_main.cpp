#include "BuzzerImpl.h"

#include <android-base/logging.h>
#include <android/binder_manager.h>
#include <android/binder_process.h>

using aidl::com::luxoft::buzzer::BuzzerImpl;

int main() {
    LOG(INFO) << "Buzzer daemon started!";

    ABinderProcess_setThreadPoolMaxThreadCount(0);
    std::shared_ptr<BuzzerImpl> buzzer = ndk::SharedRefBase::make<BuzzerImpl>();

    const std::string instance = std::string() + BuzzerImpl::descriptor + "/default";
    binder_status_t status = AServiceManager_addService(buzzer->asBinder().get(), instance.c_str());
    CHECK_EQ(status, STATUS_OK);

    ABinderProcess_joinThreadPool();
     LOG(INFO) << "Buzzer daemon ended!";
    return EXIT_FAILURE;  // should not reached
}

