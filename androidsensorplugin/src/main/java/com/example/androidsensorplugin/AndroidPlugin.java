package com.example.androidsensorplugin;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andro on 23/01/2018.
 */

public class AndroidPlugin implements SensorEventListener {

    public static final int TYPE_ORIENTATION = 1232;

    private double SENSITIVITY_THRESHOLD;

    private Context mContext;
    private Display mDisplay;
    private static AndroidPlugin mInstance;


 // declaro las variables para los sensores
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetic;
    private Sensor mUncalibratedMagnetic;
    private Sensor mLight;
    private Sensor mProximity;
    private Sensor mTemperature;
    private Sensor mPressure;
    private Sensor mHumidity;
    private Sensor mRotationVector;
    private Sensor mGameRotation;
    private Sensor mGeomagneticRotation;

    // Orientacion
    private float mAzimut;
    private float mPitch;
    private float mRoll;
    private float mAzimutOriginal;
    private float mPitchOriginal;
    private float mRollOriginal;
    private float mAzimutRotation;
    private float mPitchRotation;
    private float mRollRotation;
    private float mAzimutGameRotation;
    private float mPitchGameRotation;
    private float mRollGameRotation;
    private float mAzimutGeomagnetic;
    private float mPitchGeomagnetic;
    private float mRollGeomagnetic;
    float[] rotMat;
    float[] vals;


    private float[] mGravity;
    private float[] mMagneticValues;
    private float[] mUncalibratedMagneticValues;
    private float[] mLightValues;
    private float[] mProximityValues;
    private float[] mTemperatureValues;
    private float[] mPressureValues;
    private float[] mHumidityValues;

    private float[] mOrientationValues;
    private float[] mQuaternionValues;

    // Clase especializada en corregir la informacion de orientacion
    private OrientationCorrection mOrientationCorrection;

    private List<SensorObserver> mObservers;

    private AndroidPlugin(){
        mInstance = this;
        mObservers = new ArrayList<>();
        SENSITIVITY_THRESHOLD = 2;
        rotMat = new float[9];
        vals = new float[3];
    }

    public static AndroidPlugin getInstance(){
        if(mInstance == null){
            mInstance = new AndroidPlugin();
        }
        return mInstance;
    }

    public void setContext(Context context){
        mContext = context;

        WindowManager wm = (WindowManager) mContext.getSystemService (Context.WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay ();

        mOrientationCorrection = new OrientationCorrection(mDisplay);

// Aqui digo el tipo de sensor que voy a utilizar
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mUncalibratedMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mGameRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        mGeomagneticRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);

        // una variable por cada sensor

        // registra la aplicación de llamada como escucha, lo que permite la recepción asíncrona de sucesos. Debe pasar un objeto que implemente la interfaz
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mUncalibratedMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mHumidity, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mRotationVector, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGameRotation, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGeomagneticRotation, SensorManager.SENSOR_DELAY_NORMAL);

        // No olvidar registrar cada sensor

        mObservers = new ArrayList<>();
    }

    public void stopEventListening(){
        mSensorManager.unregisterListener(this);
    }

    //condicion por cada sensor
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
            notifyObservers(Sensor.TYPE_ACCELEROMETER);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mMagneticValues = event.values;
            notifyObservers(Sensor.TYPE_MAGNETIC_FIELD);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) {
            mUncalibratedMagneticValues = event.values;
            notifyObservers(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        }
        if (event.sensor.getType() == Sensor.TYPE_LIGHT){
            mLightValues = event.values;
            notifyObservers(Sensor.TYPE_LIGHT);
        }
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            mProximityValues = event.values;
            notifyObservers(Sensor.TYPE_PROXIMITY);
        }
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
            mTemperatureValues = event.values;
            notifyObservers(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE){
            mPressureValues = event.values;
            notifyObservers(Sensor.TYPE_PRESSURE);
        }
        if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY){
            mHumidityValues = event.values;
            notifyObservers(Sensor.TYPE_RELATIVE_HUMIDITY);
        }
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(rotMat, event.values);
            SensorManager.getOrientation(rotMat, vals);
            mAzimutRotation = vals[0];
            mPitchRotation = vals[1];
            mRollRotation = vals[2];
            notifyObservers(Sensor.TYPE_ROTATION_VECTOR);
        }
        if(event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(rotMat, event.values);
            SensorManager.getOrientation(rotMat, vals);
            mAzimutGameRotation = vals[0];
            mPitchGameRotation = vals[1];
            mRollGameRotation = vals[2];
            notifyObservers(Sensor.TYPE_GAME_ROTATION_VECTOR);
        }
        if(event.sensor.getType() == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(rotMat, event.values);
            SensorManager.getOrientation(rotMat, vals);
            mAzimutGeomagnetic = vals[0];
            mPitchGeomagnetic = vals[1];
            mRollGeomagnetic = vals[2];
            notifyObservers(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        }

        //para Orientation Correction
        if ((mGravity == null) || (mMagneticValues == null))
            return;

        // Updating orientation information
        mOrientationValues = mOrientationCorrection.getOrientationValues(mGravity, mMagneticValues);
        // orientation contains: azimut, pitch and roll
        mAzimut = mOrientationValues[0];
        mPitch = mOrientationValues[1];
        mRoll = mOrientationValues[2];

        float[] orientationOriginal = mOrientationCorrection.getOrientationValuesOriginal();
        mAzimutOriginal = orientationOriginal[0];
        mPitchOriginal = orientationOriginal[1];
        mRollOriginal = orientationOriginal[2];

        mQuaternionValues = mOrientationCorrection.getQuaternionFromRotationMatrix();

        notifyObservers(TYPE_ORIENTATION);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public float[] getGravity(){ return mGravity; }

    public float[] getMagneticValues(){ return mMagneticValues; }

    public float[] getUncalibratedMagneticValues() {
        return mUncalibratedMagneticValues;
    }

    public float[] getLight() {return mLightValues; }

    public float[] getProximity() {return mProximityValues; }

    public float[] getTemperature() {return mTemperatureValues; }

    public float[] getPressure() {return mPressureValues; }

    public float[] getHumidity() {return mHumidityValues; }

    //para trabajar la orientacion en unity
    public float getAzimut() {
        return mAzimut;
    }

    public float getPitch() {
        return mPitch;
    }

    public float getRoll() {
        return mRoll;
    }

    public float getAzimutRotation() {
        return mAzimutRotation;
    }

    public float getPitchRotation() {
        return mPitchRotation;
    }

    public float getRollRotation() {
        return mRollRotation;
    }

    public float getAzimutOriginal() {
        return mAzimutOriginal;
    }

    public float getPitchOriginal() {
        return mPitchOriginal;
    }

    public float getRollOriginal() {
        return mRollOriginal;
    }

    public float getAzimutGameRotation() {
        return mAzimutGameRotation;
    }

    public float getPitchGameRotation() {
        return mPitchGameRotation;
    }

    public float getRollGameRotation() {
        return mRollGameRotation;
    }

    public float getAzimutGeomagnetic() {
        return mAzimutGeomagnetic;
    }

    public float getPitchGeomagnetic() {
        return mPitchGeomagnetic;
    }

    public float getRollGeomagnetic() {
        return mRollGeomagnetic;
    }

    public float[] getOrientation() { return mOrientationValues; }

    public String getPluginName(){
        return "Android All Sensors Plugin";
    }


    public void registerObserver(SensorObserver observer){
        mObservers.add(observer);
    }

    public void removeObserver(SensorObserver observer){
        int index = mObservers.indexOf(observer);
        if(index >= 0) mObservers.remove(index);
    }


    //Agregar condicion por cada sensor annadido
    private void notifyObservers(int sensorType){
        if(sensorType == Sensor.TYPE_ACCELEROMETER){
            for(SensorObserver observer : mObservers){
                observer.accelerometerUpdate();
            }
        } else if(sensorType == Sensor.TYPE_MAGNETIC_FIELD){
            for(SensorObserver observer : mObservers){
                observer.magneticUpdate();
            }
        } else if(sensorType == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED){
            for(SensorObserver observer : mObservers){
                observer.uncalibratedMagneticUpdate();
            }
        } else if (sensorType == Sensor.TYPE_LIGHT){
            for (SensorObserver observer : mObservers){
                observer.lightUpdate();
            }
        } else if (sensorType == Sensor.TYPE_PROXIMITY){
            for (SensorObserver observer : mObservers){
                observer.proximityUpdate();
            }
        } else if (sensorType == Sensor.TYPE_AMBIENT_TEMPERATURE){
            for (SensorObserver observer : mObservers){
                observer.temperatureUpdate();
            }
        } else if (sensorType == Sensor.TYPE_PRESSURE){
            for (SensorObserver observer : mObservers){
                observer.pressureUpdate();
            }
        } else if (sensorType == Sensor.TYPE_RELATIVE_HUMIDITY){
            for (SensorObserver observer : mObservers){
                observer.humidityUpdate();
            }
        } else if (sensorType == TYPE_ORIENTATION){
            for (SensorObserver observer : mObservers){
                observer.orientationUpdate();
            }
        } else if (sensorType == Sensor.TYPE_ROTATION_VECTOR){
            for (SensorObserver observer : mObservers){
                observer.rotationVectorUpdate();
            }
        } else if (sensorType == Sensor.TYPE_GAME_ROTATION_VECTOR){
            for (SensorObserver observer : mObservers){
                observer.gameRotationUpdate();
            }
        } else if (sensorType == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR){
            for (SensorObserver observer : mObservers){
                observer.geomagneticRotationUpdate();
            }
        }
        

    }

    public double getSENSITIVITY_THRESHOLD() {
        return SENSITIVITY_THRESHOLD;
    }

    public float[] getQuaternion(){
        return mQuaternionValues;
    }

    public float getQuatX(){
        return mQuaternionValues[0];
    }

    public float getQuatY(){
        return mQuaternionValues[1];
    }

    public float getQuatZ(){
        return mQuaternionValues[2];
    }

    public float getQuatW(){
        return mQuaternionValues[3];
    }


}