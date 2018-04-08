package com.example.androidsensorplugin;

/**
 * Created by andro on 13/03/2018.
 */

public interface SensorObserver {
    // Funcion de actualizacion por cada sensor
    void accelerometerUpdate();
    void magneticUpdate();
    void uncalibratedMagneticUpdate();
    void lightUpdate();
    void proximityUpdate();
    void temperatureUpdate();
    void pressureUpdate();
    void humidityUpdate();
    void orientationUpdate();
    void rotationVectorUpdate();
    void gameRotationUpdate();
    void geomagneticRotationUpdate();
}
