#pragma once
#include <string>
class GpioHal {
public:    
    bool exportGpio(int pin);
    bool GpioHal::setGpioDirection(int pin, const char* direction);
    bool setGpioValue(int pin, bool value);    
    bool GpioHal::getGpioValue(int pin, int* value);
private:
    bool gpioExists(int pin);
};