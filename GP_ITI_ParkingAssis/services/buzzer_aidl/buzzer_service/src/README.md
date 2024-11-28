In Test Application :
The application uses the IBuzzer AIDL interface, specifically the setBuzzerLevel(int buzzer_level) method, to send a buzzer level value (ranging from 0 to 4) to the service.
The buzzer level determines the behavior of the buzzer in terms of its ON duration and OFF duration.
Service Layer:
The BuzzerImpl class receives the buzzer level from the application via the setBuzzerLevel() method.
It validates the buzzer level and forwards it to the HAL layer using setBuzzerLevel(buzzerLevel).
The service ensures proper error handling and returns feedback to the application, indicating whether the operation was successful or failed.
HAL Driver Layer:
The BuzzerHal class is responsible for directly interacting with the hardware.
Upon receiving the buzzer level (ranging from 0 to 4):
It calculates the ON duration (buzzerLevel * 100 ms) and the OFF duration (500 ms - ON duration).
The driver toggles the GPIO pin based on these durations to produce a beep pattern corresponding to the level.
For example:
Level 0: Buzzer OFF.
Level 4: Maximum ON duration with minimal OFF duration.
The driver also handles hardware initialization (e.g., GPIO pin setup) and cleanup to ensure safe operation.