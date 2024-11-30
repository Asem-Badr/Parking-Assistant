#pragma once

#include "GpioHal.h"

class UltrasonicHal {
private:
    GpioHal gpio;
    int trigPin;
    int echoPin;

public:
    UltrasonicHal(int trig, int echo) : trigPin(trig), echoPin(echo) {}

    bool initialize();
    float measureDistance();
};
