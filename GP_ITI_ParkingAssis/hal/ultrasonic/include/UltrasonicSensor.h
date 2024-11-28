#ifndef ULTRASONIC_SENSOR_H
#define ULTRASONIC_SENSOR_H

#include <string>
#include <chrono>
#include <thread>

class UltrasonicSensor {
public:
    UltrasonicSensor(int trigPin, int echoPin);
    ~UltrasonicSensor();

    bool initialize();
    float measureDistance();
    void cleanup();

private:
    bool exportGpio(int pin);
    bool setGpioDirection(int pin, const std::string& direction);
    bool setGpioValue(int pin, bool value);
    bool getGpioValue(int pin, bool& value);
    void sleepMs(int ms);

    int trigPin;
    int echoPin;
    bool initialized;
};

#endif // ULTRASONIC_SENSOR_H
