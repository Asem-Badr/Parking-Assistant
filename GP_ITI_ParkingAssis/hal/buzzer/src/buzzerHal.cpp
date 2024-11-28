#include "BuzzerHal.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <chrono>
#include <thread>

BuzzerHal::BuzzerHal(int defaultPin)
    : buzzerPin(defaultPin), initialized(false) {}

BuzzerHal::~BuzzerHal() {
    cleanup();
}

bool BuzzerHal::initialize() {
    // Export the GPIO pin
    if (!exportGpio(buzzerPin)) {
        std::cerr << "Failed to export GPIO pin " << buzzerPin << std::endl;
        return false;
    }

    // Set the GPIO pin direction to output
    if (!setGpioDirection(buzzerPin, "out")) {
        std::cerr << "Failed to set GPIO pin " << buzzerPin << " direction" << std::endl;
        return false;
    }

    initialized = true;
    return true;
}

bool BuzzerHal::exportGpio(int pin) {
    std::ofstream exportFile("/sys/class/gpio/export");
    if (!exportFile) {
        std::cerr << "Failed to open export file for GPIO pin " << pin << std::endl;
        return false;
    }
    exportFile << pin;
    return exportFile.good();
}

bool BuzzerHal::setGpioDirection(int pin, const std::string& direction) {
    std::ofstream directionFile("/sys/class/gpio/gpio" + std::to_string(pin) + "/direction");
    if (!directionFile) {
        std::cerr << "Failed to open direction file for GPIO pin " << pin << std::endl;
        return false;
    }
    directionFile << direction;
    return directionFile.good();
}

bool BuzzerHal::setGpioValue(int pin, bool value) {
    std::ofstream valueFile("/sys/class/gpio/gpio" + std::to_string(pin) + "/value");
    if (!valueFile) {
        std::cerr << "Failed to open value file for GPIO pin " << pin << std::endl;
        return false;
    }
    valueFile << (value ? "1" : "0");
    return valueFile.good();
}

void BuzzerHal::sleepMs(int ms) {
    std::this_thread::sleep_for(std::chrono::milliseconds(ms));
}

bool BuzzerHal::setBuzzerLevel(int buzzerLevel) {
    if (buzzerLevel < 0 || buzzerLevel > 4) {
        std::cerr << "Invalid buzzer level: " << buzzerLevel << ". Must be between 0 and 4." << std::endl;
        return false;
    }

    if (!initialized) {
        std::cerr << "Buzzer is not initialized" << std::endl;
        return false;
    }

    // Generate a beep pattern based on the buzzer level
    int durationOn = buzzerLevel * 100;  // Duration in ms for ON state
    int durationOff = 500 - durationOn; // Adjust OFF state duration

    // Simple beep pattern
    setGpioValue(buzzerPin, true);  // Turn buzzer ON
    sleepMs(durationOn);           // Wait
    setGpioValue(buzzerPin, false); // Turn buzzer OFF
    sleepMs(durationOff);          // Wait

    return true;
}

void BuzzerHal::cleanup() {
    if (initialized) {
        // Unexport the GPIO pin
        std::ofstream unexportFile("/sys/class/gpio/unexport");
        if (unexportFile) {
            unexportFile << buzzerPin;
        }
        initialized = false;
    }
}
