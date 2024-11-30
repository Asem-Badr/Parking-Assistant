                            #include <iostream>
#include <cstdlib> // For std::atoi
#include <unistd.h> // For sleep
#include "BuzzerHal.h"

int main(int argc, char* argv[]) {
    // Check if the required argument is provided
    if (argc != 2) {
        std::cerr << "Usage: " << argv[0] << " <buzzer_level (0-4)>" << std::endl;
        return -1;
    }

    // Parse the buzzer level argument
    int buzzerLevel = std::atoi(argv[1]);
    if (buzzerLevel < 0 || buzzerLevel > 4) {
        std::cerr << "Invalid buzzer level: " << buzzerLevel << ". Must be between 0 and 4." << std::endl;
        return -1;
    }

    // Create and initialize the buzzer
    BuzzerHal buzzer(18); // Default GPIO pin is 18
    if (!buzzer.initialize()) {
        std::cerr << "Failed to initialize the buzzer!" << std::endl;
        return -1;
    }

    // // Set the buzzer level based on the argument
    // buzzer.setBuzzerLevel(buzzerLevel);
    // std::cout << "Buzzer set to level: " << buzzerLevel << std::endl;

    // Keep the program running to maintain the buzzer state
    while (true) {
        buzzer.setBuzzerLevel(buzzerLevel);
        std::cout << "Buzzer set to level: " << buzzerLevel << std::endl;
        std::cout << "Buzzer is running at level " << buzzerLevel << "..." << std::endl;
        sleep(1); // Sleep for 1 second to prevent excessive logging
    }

    // Cleanup (not reachable in this infinite loop, but included for completeness)
    buzzer.cleanup();

    return 0;
}
