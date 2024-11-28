#include <iostream>
#include <unistd.h>
#include "UltrasonicSensor.h"

int main() {
    // Define GPIO pins for the ultrasonic sensor
    int trigPin = 17;  // Trigger pin (GPIO 17)
    int echoPin = 27;  // Echo pin (GPIO 27)

    // Create an UltrasonicSensor object
    UltrasonicSensor sensor(trigPin, echoPin);
    UltrasonicSensor sensor2(18, 15);

    // Initialize the sensor (export and configure the GPIO pins)
    if (!sensor.initialize()) {
        std::cerr << "Failed to initialize ultrasonic sensor!" << std::endl;
        return -1;
    }

while(true) {
    // Perform a distance measurement
    float distance = sensor.measureDistance();
    float distance2 = sensor2.measureDistance();
    
    if (distance < 0.0f) {
        std::cerr << "Failed to measure distance!" << std::endl;
    } else {
        std::cout << "Distance: " << distance << " cm" << std::endl;
    }

    if (distance2 < 0.0f) {
        std::cerr << "Failed to measure distance!" << std::endl;
    } else {
        std::cout << "Distance from sensor 2: " << distance << " cm" << std::endl;
    }

    sleep(1);
}

    // Clean up (unexport the GPIO pins)
    sensor.cleanup();

    return 0;
}
