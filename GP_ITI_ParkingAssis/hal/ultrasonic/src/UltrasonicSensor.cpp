#include "UltrasonicSensor.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <chrono>
#include <thread>

UltrasonicSensor::UltrasonicSensor(int trigPin, int echoPin)
    : trigPin(trigPin), echoPin(echoPin), initialized(false) {}

UltrasonicSensor::~UltrasonicSensor() {
    cleanup();
}

bool UltrasonicSensor::initialize() {
    // Export GPIO pins
    if (!exportGpio(trigPin) || !exportGpio(echoPin)) {
        std::cerr << "Failed to export GPIO pins" << std::endl;
        return false;
    }

    // Set pin directions (trig is output, echo is input)
    if (!setGpioDirection(trigPin, "out") || !setGpioDirection(echoPin, "in")) {
        std::cerr << "Failed to set GPIO pin directions" << std::endl;
        return false;
    }

    initialized = true;
    return true;
}

bool UltrasonicSensor::exportGpio(int pin) {
    std::ofstream exportFile("/sys/class/gpio/export");
    if (!exportFile) {
        return false;
    }
    exportFile << pin;
    return exportFile.good();
}

bool UltrasonicSensor::setGpioDirection(int pin, const std::string& direction) {
    std::ofstream directionFile("/sys/class/gpio/gpio" + std::to_string(pin) + "/direction");
    if (!directionFile) {
        return false;
    }
    directionFile << direction;
    return directionFile.good();
}

bool UltrasonicSensor::setGpioValue(int pin, bool value) {
    std::ofstream valueFile("/sys/class/gpio/gpio" + std::to_string(pin) + "/value");
    if (!valueFile) {
        return false;
    }
    valueFile << (value ? "1" : "0");
    return valueFile.good();
}

bool UltrasonicSensor::getGpioValue(int pin, bool& value) {
    std::ifstream valueFile("/sys/class/gpio/gpio" + std::to_string(pin) + "/value");
    if (!valueFile) {
        return false;
    }

    int gpioValue;
    valueFile >> gpioValue;
    value = (gpioValue == 1);
    return valueFile.good();
}

void UltrasonicSensor::sleepMs(int ms) {
    std::this_thread::sleep_for(std::chrono::milliseconds(ms));
}

float UltrasonicSensor::measureDistance() {
    if (!initialized) {
        std::cerr << "Sensor is not initialized" << std::endl;
        return -1.0f;
    }

    // Trigger the ultrasonic pulse
    setGpioValue(trigPin, false);
    sleepMs(2);  // wait for 2ms
    setGpioValue(trigPin, true);
    sleepMs(10); // wait for 10ms to generate pulse
    setGpioValue(trigPin, false);

    // Wait for echo to start
    bool echoStart = false;
    auto start = std::chrono::high_resolution_clock::now();
    while (!echoStart) {
        bool value;
        if (getGpioValue(echoPin, value) && value) {
            echoStart = true;
            start = std::chrono::high_resolution_clock::now();
        }
    }

    // Wait for echo to stop
    auto end = std::chrono::high_resolution_clock::now();
    while (true) {
        bool value;
        if (getGpioValue(echoPin, value) && !value) {
            end = std::chrono::high_resolution_clock::now();
            break;
        }
    }

    // Calculate the duration of the pulse
    std::chrono::duration<float> duration = end - start;
    float distance = (duration.count() * 34300.0f) / 2.0f;  // Speed of sound is 34300 cm/s

    return distance;
}

void UltrasonicSensor::cleanup() {
    if (initialized) {
        // Cleanup: unexport the GPIO pins
        std::ofstream unexportFile("/sys/class/gpio/unexport");
        if (unexportFile) {
            unexportFile << trigPin;
            unexportFile << echoPin;
        }
        initialized = false;
    }
}
