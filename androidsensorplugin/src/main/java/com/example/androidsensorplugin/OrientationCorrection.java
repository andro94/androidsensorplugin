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

    public OrientationCorrection(Display display) {
        this.mDisplay = display;
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
        float orientationValues[] = new float[3];
        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrixAdjusted, orientationValues);
        }

        //contener cada componente de la matriz orientationValues
        azimuth = orientationValues[0];
        pitch = orientationValues[1];
        roll = orientationValues[2];
        
        return orientationValues;
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
}
