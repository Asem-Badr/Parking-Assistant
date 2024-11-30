#include "include/UltrasonicHal.h"
#include <chrono>
#include <thread>
#include <iostream>

bool UltrasonicHal::initialize() {
    // Export GPIOs and configure their directions
    if (!gpio.exportGpio(trigPin) || !gpio.exportGpio(echoPin)) {
        std::cerr << "Failed to export GPIO pins for sensor" << std::endl;
        return false;
    }

    if (!gpio.setGpioDirection(trigPin, "out") || 
        !gpio.setGpioDirection(echoPin, "in")) {
        std::cerr << "Failed to set GPIO directions for sensor" << std::endl;
        return false;
    }

    return true;
}




void preciseDelayMicroseconds(int microseconds) {
    auto start = std::chrono::high_resolution_clock::now();
    while (std::chrono::high_resolution_clock::now() - start < std::chrono::microseconds(microseconds)) {
        // Busy-wait
    }
}


float UltrasonicHal::measureDistance() {
    // Set TRIG pin low and wait 2 microseconds
    gpio.setGpioValue(trigPin, false);
    // std::this_thread::sleep_for(std::chrono::microseconds(2));
    preciseDelayMicroseconds(2);

    // Set TRIG pin high for 10 microseconds to send pulse
    gpio.setGpioValue(trigPin, true);
    //std::this_thread::sleep_for(std::chrono::microseconds(10));
    preciseDelayMicroseconds(10);

    gpio.setGpioValue(trigPin, false);

    // Variables to measure time and store GPIO value
    auto startTime = std::chrono::high_resolution_clock::now();
    auto endTime = startTime;
    auto timeout = std::chrono::microseconds(30000); // 30 ms timeout
    int gpioValue = 0;

    // Wait for ECHO pin to go high with timeout
    while (true) {
        if (!gpio.getGpioValue(echoPin, &gpioValue)) {
            std::cerr << "Failed to read GPIO value for ECHO pin." << std::endl;
            return -1.0;
        }
        if (gpioValue == 1) break;

        if (std::chrono::high_resolution_clock::now() - startTime > timeout) {
            std::cerr << "Timeout waiting for ECHO pin to go high." << std::endl;
            return -1.0;
        }
    }
    startTime = std::chrono::high_resolution_clock::now();

    // Wait for ECHO pin to go low with timeout
    while (true) {
        if (!gpio.getGpioValue(echoPin, &gpioValue)) {
            std::cerr << "Failed to read GPIO value for ECHO pin." << std::endl;
            return -1.0;
        }
        if (gpioValue == 0) break;

        if (std::chrono::high_resolution_clock::now() - startTime > timeout) {
            std::cerr << "Timeout waiting for ECHO pin to go low." << std::endl;
            return -1.0;
        }
        endTime = std::chrono::high_resolution_clock::now();
    }

    // Calculate pulse duration
    auto pulseDuration = std::chrono::duration_cast<std::chrono::microseconds>(endTime - startTime).count();

    // Calculate and return distance in cm
    return (pulseDuration * 0.034) / 2;  // Speed of sound: 0.034 cm/µs
}



