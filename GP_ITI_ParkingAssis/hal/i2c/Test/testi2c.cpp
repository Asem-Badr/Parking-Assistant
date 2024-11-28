#include "I2CHal.h"  
#include <iostream>
#include <chrono>
#include <thread>

int main() {
    while (1) {
        int value = getRpm();
        std::cout << "Current Register Reading: " << value << std::endl;
        std::this_thread::sleep_for(std::chrono::milliseconds(1000));  // Update every second
    }
}
