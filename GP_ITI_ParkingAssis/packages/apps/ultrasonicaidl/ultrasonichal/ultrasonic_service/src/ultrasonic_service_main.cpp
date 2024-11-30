#include "UltrasonicImpl.h"
#include <android-base/logging.h>
#include <android/binder_manager.h>
#include <android/binder_process.h>

using aidl::com::luxoft::ultrasonic::UltrasonicImpl;

int main() {
    LOG(INFO) << "Ultrasonic daemon started!";

    ABinderProcess_setThreadPoolMaxThreadCount(0);
    std::shared_ptr<UltrasonicImpl> ultrasonic = ndk::SharedRefBase::make<UltrasonicImpl>();

    const std::string instance = std::string() + UltrasonicImpl::descriptor + "/default";
    binder_status_t status = AServiceManager_addService(ultrasonic->asBinder().get(), instance.c_str());
    CHECK_EQ(status, STATUS_OK);

    ABinderProcess_joinThreadPool();
    return EXIT_FAILURE;  // should not be reached
}
