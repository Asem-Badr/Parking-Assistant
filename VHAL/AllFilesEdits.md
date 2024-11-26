     
     /*************************************************************************************************************************************/
     /*************************************************************************************************************************************/
     /******************************************** TestVendorProperty.aidl ****************************************************************/
     /*************************************************************************************************************************************/
     /*************************************************************************************************************************************/
     
     /**
     * Property used for {@code CarVendorPropertyCustomPermissionTest}.
     *
     * VehiclePropertyGroup.VENDOR | VehicleArea.WINDOW | VehiclePropertyGroup.INT32
     */
    ULTRASONIC_ONE =   0x207 + 0x20000000 + 0x03000000 + 0x00400000
    ULTRASONIC_TWO =   0x208 + 0x20000000 + 0x03000000 + 0x00400000
    ULTRASONIC_THREE = 0x209 + 0x20000000 + 0x03000000 + 0x00400000
    ULTRASONIC_FOUR =  0x20A + 0x20000000 + 0x03000000 + 0x00400000
    ULTRASONIC_FIVE =  0x20B + 0x20000000 + 0x03000000 + 0x00400000
    ULTRASONIC_SIX=    0x20C + 0x20000000 + 0x03000000 + 0x00400000



     /*************************************************************************************************************************************/
     /*************************************************************************************************************************************/
     /************************************************ TestProperties.json ****************************************************************/
     /*************************************************************************************************************************************/
     /*************************************************************************************************************************************/
     
        {
            "property": "TestVendorProperty::ULTRASONIC_ONE",
            "defaultValue": {"int32Values":[111]},
            "access": "VehiclePropertyAccess::READ",
            "changeMode": "VehiclePropertyChangeMode::ON_CHANGE"
        },
        
        {
            "property": "TestVendorProperty::ULTRASONIC_TWO",
            "defaultValue": {"int32Values":[222]},
            "access": "VehiclePropertyAccess::READ",
            "changeMode": "VehiclePropertyChangeMode::ON_CHANGE"
        },
		 {
            "property": "TestVendorProperty::ULTRASONIC_THREE",
            "defaultValue": {"int32Values":[333]},
            "access": "VehiclePropertyAccess::READ",
            "changeMode": "VehiclePropertyChangeMode::ON_CHANGE"
        },
        
        {
            "property": "TestVendorProperty::ULTRASONIC_FOUR",
            "defaultValue": {"int32Values":[444]},
            "access": "VehiclePropertyAccess::READ",
            "changeMode": "VehiclePropertyChangeMode::ON_CHANGE"
        },
		 {
            "property": "TestVendorProperty::ULTRASONIC_FIVE",
            "defaultValue": {"int32Values":[555]},
            "access": "VehiclePropertyAccess::READ",
            "changeMode": "VehiclePropertyChangeMode::ON_CHANGE"
        },
        
        {
            "property": "TestVendorProperty::ULTRASONIC_SIX",
            "defaultValue": {"int32Values":[666]},
            "access": "VehiclePropertyAccess::READ",
            "changeMode": "VehiclePropertyChangeMode::ON_CHANGE"
        }
		
   



     /*************************************************************************************************************************************/
     /*************************************************************************************************************************************/
     /**************************************************** FakeVhalConfigParser.java ******************************************************/
     /*************************************************************************************************************************************/
     /*************************************************************************************************************************************/

      //NINJA
       Map.entry("ULTRASONIC_ONE",TestVendorProperty.ULTRASONIC_ONE),
       Map.entry("ULTRASONIC_TWO",TestVendorProperty.ULTRASONIC_TWO),
       Map.entry("ULTRASONIC_THREE",TestVendorProperty.ULTRASONIC_THREE),
       Map.entry("ULTRASONIC_FOUR",TestVendorProperty.ULTRASONIC_FOUR),
       Map.entry("ULTRASONIC_FIVE",TestVendorProperty.ULTRASONIC_FIVE),
       Map.entry("ULTRASONIC_SIX",TestVendorProperty.ULTRASONIC_SIX)





     /*************************************************************************************************************************************/
     /*************************************************************************************************************************************/
     /*********************************************************** Car.java ****************************************************************/
     /*************************************************************************************************************************************/
     /*************************************************************************************************************************************/


     /**
     * Permission necessary to access Car ULTRASONIC_ONE.
     * @hide
     */
     @FlaggedApi(Flags.FLAG_DISPLAY_COMPATIBILITY)
     @SystemApi
     public static final String PERMISSION_ULTRASONIC=
            "android.car.permission.ULTRASONIC";



     /*************************************************************************************************************************************/
     /*************************************************************************************************************************************/
     /************************************************** VehiclePropertyIds.java **********************************************************/
     /*************************************************************************************************************************************/
     /*************************************************************************************************************************************/

      //NINJA
       /**
     * Permission necessary to access Car distance.
     * @hide
     */ 
    @FlaggedApi(FLAG_ANDROID_VIC_VEHICLE_PROPERTIES)
    @SystemApi
    @RequiresPermission(Car.PERMISSION_ULTRASONIC)
    public static final int ULTRASONIC_ONE = 591397383 ;

     /**
     * Permission necessary to access Car distance.
     * @hide
     */ 
    @FlaggedApi(FLAG_ANDROID_VIC_VEHICLE_PROPERTIES)
    @SystemApi
    @RequiresPermission(Car.PERMISSION_ULTRASONIC)
    public static final int ULTRASONIC_TWO = 591397384 ;
	
	  /**
     * Permission necessary to access Car distance.
     * @hide
     */ 
    @FlaggedApi(FLAG_ANDROID_VIC_VEHICLE_PROPERTIES)
    @SystemApi
    @RequiresPermission(Car.PERMISSION_ULTRASONIC)
    public static final int ULTRASONIC_THREE = 591397385 ;
	
	  /**
     * Permission necessary to access Car distance.
     * @hide
     */ 
    @FlaggedApi(FLAG_ANDROID_VIC_VEHICLE_PROPERTIES)
    @SystemApi
    @RequiresPermission(Car.PERMISSION_ULTRASONIC)
    public static final int ULTRASONIC_FOUR = 591397386 ;
	
	  /**
     * Permission necessary to access Car distance.
     * @hide
     */ 
    @FlaggedApi(FLAG_ANDROID_VIC_VEHICLE_PROPERTIES)
    @SystemApi
    @RequiresPermission(Car.PERMISSION_ULTRASONIC)
    public static final int ULTRASONIC_FIVE = 591397387 ;
	
	  /**
     * Permission necessary to access Car distance.
     * @hide
     */ 
    @FlaggedApi(FLAG_ANDROID_VIC_VEHICLE_PROPERTIES)
    @SystemApi
    @RequiresPermission(Car.PERMISSION_ULTRASONIC)
    public static final int ULTRASONIC_SIX = 591397388 ;



     /*************************************************************************************************************************************/
     /*************************************************************************************************************************************/
     /************************************************** FakeVehicleHardware.cpp **********************************************************/
     /*************************************************************************************************************************************/
     /*************************************************************************************************************************************/

    //NINJA
    if (propId == toInt(TestVendorProperty::ULTRASONIC_ONE)){  // If ULTRASONIC_ONE property is requested
            *isSpecialValue = true;  // Mark this as a special value

            // Define GPIO pins for the ultrasonic sensor
            int trigPin = 16;  // Trigger pin (GPIO 16)
            int echoPin = 20;  // Echo pin (GPIO 20)

            // Create an UltrasonicSensor object
            UltrasonicSensor sensor(trigPin, echoPin);

            // Initialize the sensor
            if (!sensor.initialize()) {
                ALOGE("Failed to initialize ultrasonic sensor on TRIG pin %d, ECHO pin %d", trigPin, echoPin);
                return nullptr;  // Return null pointer or an error result here
            }

            // Measure the distance using the sensor
            int distance = (int)sensor.measureDistance();

            // Validate the distance value
            if (distance < 0) {
                ALOGE("Invalid distance measurement: %f", distance);
                return nullptr;  // Return null pointer or an error result here
            }
        	result = mValuePool->obtainInt32(distance);
      	    ALOGD("temp %d",distance);
       	    result.value()->prop = propId;
        	result.value()->areaId = 0;
        	result.value()->timestamp = distance;
        	return result;
			
			 //NINJA
    if (propId == toInt(TestVendorProperty::ULTRASONIC_TWO)){  // If ULTRASONIC_ONE property is requested
            *isSpecialValue = true;  // Mark this as a special value

            // Define GPIO pins for the ultrasonic sensor
            int trigPin = 21;  // Trigger pin (GPIO 21)
            int echoPin = 26;  // Echo pin (GPIO 26)

            // Create an UltrasonicSensor object
            UltrasonicSensor sensor(trigPin, echoPin);

            // Initialize the sensor
            if (!sensor.initialize()) {
                ALOGE("Failed to initialize ultrasonic sensor on TRIG pin %d, ECHO pin %d", trigPin, echoPin);
                return nullptr;  // Return null pointer or an error result here
            }

            // Measure the distance using the sensor
            int distance = (int)sensor.measureDistance();

            // Validate the distance value
            if (distance < 0) {
                ALOGE("Invalid distance measurement: %f", distance);
                return nullptr;  // Return null pointer or an error result here
            }
        	result = mValuePool->obtainInt32(distance);
      	    ALOGD("temp %d",distance);
       	    result.value()->prop = propId;
        	result.value()->areaId = 0;
        	result.value()->timestamp = distance;
        	return result;


 //NINJA
    if (propId == toInt(TestVendorProperty::ULTRASONIC_THREE)){  // If ULTRASONIC_ONE property is requested
            *isSpecialValue = true;  // Mark this as a special value

            // Define GPIO pins for the ultrasonic sensor
            int trigPin = 19;  // Trigger pin (GPIO 19)
            int echoPin = 13;  // Echo pin (GPIO 3)

            // Create an UltrasonicSensor object
            UltrasonicSensor sensor(trigPin, echoPin);

            // Initialize the sensor
            if (!sensor.initialize()) {
                ALOGE("Failed to initialize ultrasonic sensor on TRIG pin %d, ECHO pin %d", trigPin, echoPin);
                return nullptr;  // Return null pointer or an error result here
            }

            // Measure the distance using the sensor
            int distance = (int)sensor.measureDistance();

            // Validate the distance value
            if (distance < 0) {
                ALOGE("Invalid distance measurement: %f", distance);
                return nullptr;  // Return null pointer or an error result here
            }
        	result = mValuePool->obtainInt32(distance);
      	    ALOGD("temp %d",distance);
       	    result.value()->prop = propId;
        	result.value()->areaId = 0;
        	result.value()->timestamp = distance;
        	return result;
			
			
			 //NINJA
    if (propId == toInt(TestVendorProperty::ULTRASONIC_FOUR)){  // If ULTRASONIC_ONE property is requested
            *isSpecialValue = true;  // Mark this as a special value

            // Define GPIO pins for the ultrasonic sensor
            int trigPin = 6;  // Trigger pin (GPIO 6)
            int echoPin = 5;  // Echo pin (GPIO 5)

            // Create an UltrasonicSensor object
            UltrasonicSensor sensor(trigPin, echoPin);

            // Initialize the sensor
            if (!sensor.initialize()) {
                ALOGE("Failed to initialize ultrasonic sensor on TRIG pin %d, ECHO pin %d", trigPin, echoPin);
                return nullptr;  // Return null pointer or an error result here
            }

            // Measure the distance using the sensor
            int distance = (int)sensor.measureDistance();

            // Validate the distance value
            if (distance < 0) {
                ALOGE("Invalid distance measurement: %f", distance);
                return nullptr;  // Return null pointer or an error result here
            }
        	result = mValuePool->obtainInt32(distance);
      	    ALOGD("temp %d",distance);
       	    result.value()->prop = propId;
        	result.value()->areaId = 0;
        	result.value()->timestamp = distance;
        	return result;
			
			 //NINJA
    if (propId == toInt(TestVendorProperty::ULTRASONIC_FIVE)){  // If ULTRASONIC_ONE property is requested
            *isSpecialValue = true;  // Mark this as a special value

            // Define GPIO pins for the ultrasonic sensor
            int trigPin = 22;  // Trigger pin (GPIO 22)
            int echoPin = 4;  // Echo pin (GPIO 4)

            // Create an UltrasonicSensor object
            UltrasonicSensor sensor(trigPin, echoPin);

            // Initialize the sensor
            if (!sensor.initialize()) {
                ALOGE("Failed to initialize ultrasonic sensor on TRIG pin %d, ECHO pin %d", trigPin, echoPin);
                return nullptr;  // Return null pointer or an error result here
            }

            // Measure the distance using the sensor
            int distance = (int)sensor.measureDistance();

            // Validate the distance value
            if (distance < 0) {
                ALOGE("Invalid distance measurement: %f", distance);
                return nullptr;  // Return null pointer or an error result here
            }
        	result = mValuePool->obtainInt32(distance);
      	    ALOGD("temp %d",distance);
       	    result.value()->prop = propId;
        	result.value()->areaId = 0;
        	result.value()->timestamp = distance;
        	return result;
			
			 //NINJA
    if (propId == toInt(TestVendorProperty::ULTRASONIC_SIX)){  // If ULTRASONIC_ONE property is requested
            *isSpecialValue = true;  // Mark this as a special value

            // Define GPIO pins for the ultrasonic sensor
            int trigPin = 7;  // Trigger pin (GPIO 7)
            int echoPin = 25;  // Echo pin (GPIO 25)

            // Create an UltrasonicSensor object
            UltrasonicSensor sensor(trigPin, echoPin);

            // Initialize the sensor
            if (!sensor.initialize()) {
                ALOGE("Failed to initialize ultrasonic sensor on TRIG pin %d, ECHO pin %d", trigPin, echoPin);
                return nullptr;  // Return null pointer or an error result here
            }

            // Measure the distance using the sensor
            int distance = (int)sensor.measureDistance();

            // Validate the distance value
            if (distance < 0) {
                ALOGE("Invalid distance measurement: %f", distance);
                return nullptr;  // Return null pointer or an error result here
            }
        	result = mValuePool->obtainInt32(distance);
      	    ALOGD("temp %d",distance);
       	    result.value()->prop = propId;
        	result.value()->areaId = 0;
        	result.value()->timestamp = distance;
        	return result;
