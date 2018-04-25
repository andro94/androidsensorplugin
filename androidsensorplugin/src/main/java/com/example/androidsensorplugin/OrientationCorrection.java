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
    float[] rotationMatrix;

    public OrientationCorrection(Display display) {
        this.mDisplay = display;
        orientationValues = new float[3];
        orientationValuesOriginal = new float[3];
        rotationMatrix = new float[9];
    }

    // Always call this method at the beginning, because is where getRotationMatrix is called
    public float[] getOrientationValues(float[] accelerometerValues, float[] magnetometerValues) {
        //generar una matriz de rotación a partir del acelerómetro sin procesar y los datos del magnetómetro
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

    public float[] getQuaternionFromRotationMatrix(){
        return getQuaternion(rotationMatrix[0],rotationMatrix[1],rotationMatrix[2],
                            rotationMatrix[3],rotationMatrix[4],rotationMatrix[5],
                            rotationMatrix[6],rotationMatrix[7],rotationMatrix[8]);
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

    public float[] getQuaternion (float xx, float xy, float xz, float yx, float yy, float yz, float zx,
                                   float zy, float zz) {
        // the trace is the sum of the diagonal elements; see
        // http://mathworld.wolfram.com/MatrixTrace.html
        final float t = xx + yy + zz;

        float x,y,z,w;

        // we protect the division by s by ensuring that s>=1
        if (t >= 0) { // |w| >= .5
            float s = (float)Math.sqrt(t + 1); // |s|>=1 ...
            w = 0.5f * s;
            s = 0.5f / s; // so this division isn't bad
            x = (zy - yz) * s;
            y = (xz - zx) * s;
            z = (yx - xy) * s;
        } else if ((xx > yy) && (xx > zz)) {
            float s = (float)Math.sqrt(1.0 + xx - yy - zz); // |s|>=1
            x = s * 0.5f; // |x| >= .5
            s = 0.5f / s;
            y = (yx + xy) * s;
            z = (xz + zx) * s;
            w = (zy - yz) * s;
        } else if (yy > zz) {
            float s = (float)Math.sqrt(1.0 + yy - xx - zz); // |s|>=1
            y = s * 0.5f; // |y| >= .5
            s = 0.5f / s;
            x = (yx + xy) * s;
            z = (zy + yz) * s;
            w = (xz - zx) * s;
        } else {
            float s = (float)Math.sqrt(1.0 + zz - xx - yy); // |s|>=1
            z = s * 0.5f; // |z| >= .5
            s = 0.5f / s;
            x = (xz + zx) * s;
            y = (zy + yz) * s;
            w = (yx - xy) * s;
        }
        return new float[]{x,y,z,w};
    }
}
