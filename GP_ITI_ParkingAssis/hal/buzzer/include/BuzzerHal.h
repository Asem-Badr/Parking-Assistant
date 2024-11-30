#ifndef BUZZER_H
#define BUZZER_H

#include <string>

class BuzzerHal {
public:
    explicit BuzzerHal(int defaultPin = 18); // Constructor with default GPIO pin
    ~BuzzerHal();

    bool initialize();
    bool setBuzzerLevel(int buzzerLevel);   // Set buzzer beep level (0-4)
    void cleanup();

private:
    bool exportGpio(int pin);
    bool setGpioDirection(int pin, const std::string& direction);
    bool setGpioValue(int pin, bool value);
    void sleepMs(int ms);

    int buzzerPin;    // GPIO pin for the buzzer
    bool initialized; // Indicates if the GPIO is initialized
};

#endif // BUZZER_H
