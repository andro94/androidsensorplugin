package com.example.androidsensorplugin;

import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;

/**
 * Created by andro on 13/03/2018.
 */

public class OrientationCorrection {

    private Display mDisplay;

    private float azimuth = 0;
    private float pitch = 0;
    private float roll = 0;

    private float azimuthOriginal = 0;
    private float pitchOriginal = 0;
    private float rollOriginal = 0;

    float orientationValues[];
    float orientationValuesOriginal[];

    public OrientationCorrection(Display display) {
        this.mDisplay = display;
        orientationValues = new float[3];
        orientationValuesOriginal = new float[3];
    }

    public float[] getOrientationValues(float[] accelerometerValues, float[] magnetometerValues) {
        //generar una matriz de rotación a partir del acelerómetro sin procesar y los datos del magnetómetro
        float[] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magnetometerValues);

        if(!rotationOK) return null;

        //agregar una nueva matriz de valores float para mantener la nueva matriz de rotación ajustada.
        float [] rotationMatrixAdjusted = new float [9];

        //Obtener la rotación actual del dispositivo desde la pantalla y agregar una declaración de cambio para ese valor
        //clonar los datos en la matriz de rotación existente
        //Este método toma como argumentos la matriz de rotación original, los dos nuevos ejes en los que desea reasignar el eje X existente y el eje y, y una matriz para completar con los datos nuevos
        switch (mDisplay.getRotation ()) {
            case Surface.ROTATION_0:
                rotationMatrixAdjusted = rotationMatrix.clone ();
                break;
            case Surface.ROTATION_90:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,
                        rotationMatrixAdjusted);
                break;
            case Surface.ROTATION_180:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y,
                        rotationMatrixAdjusted);
                break;
            case Surface.ROTATION_270:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X,
                        rotationMatrixAdjusted);
                break;

        }


        //obtener los ángulos de orientación de la matriz de rotación, usar la nueva matrix

        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrixAdjusted, orientationValues);
            SensorManager.getOrientation(rotationMatrix, orientationValuesOriginal);
        }

        //contener cada componente de la matriz orientationValues
        azimuth = orientationValues[0];
        pitch = orientationValues[1];
        roll = orientationValues[2];

        azimuthOriginal = orientationValuesOriginal[0];
        pitchOriginal = orientationValuesOriginal[1];
        rollOriginal = orientationValuesOriginal[2];
        
        return orientationValues;
    }

    public float[] getOrientationValues() {
        return orientationValues;
    }

    public float[] getOrientationValuesOriginal() {
        return orientationValuesOriginal;
    }

    public float getAzimuth() {
        return azimuth;
    }

    public float getPitch() {
        return pitch;
    }

    public float getRoll() {
        return roll;
    }

    public float getAzimuthOriginal() {
        return azimuthOriginal;
    }

    public float getPitchOriginal() {
        return pitchOriginal;
    }

    public float getRollOriginal() {
        return rollOriginal;
    }
}
