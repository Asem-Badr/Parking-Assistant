#include "SteeringWheelImpl.h"

#include <android-base/logging.h>
#include <android/binder_manager.h>
#include <android/binder_process.h>

using aidl::com::luxoft::steeringwheel::SteeringWheelImpl;

int main() {
    LOG(INFO) << "SteeringWheel daemon started!";

    ABinderProcess_setThreadPoolMaxThreadCount(0);
    std::shared_ptr<SteeringWheelImpl> steeringwheel = ndk::SharedRefBase::make<SteeringWheelImpl>();

    const std::string instance = std::string() + SteeringWheelImpl::descriptor + "/default";
    binder_status_t status = AServiceManager_addService(steeringwheel->asBinder().get(), instance.c_str());
    CHECK_EQ(status, STATUS_OK);

    ABinderProcess_joinThreadPool();
    return EXIT_FAILURE;  // should not reached
}

