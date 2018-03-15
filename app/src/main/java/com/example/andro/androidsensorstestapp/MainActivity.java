package com.example.andro.androidsensorstestapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.androidsensorplugin.AndroidPlugin;
import com.example.androidsensorplugin.SensorObserver;

public class MainActivity extends AppCompatActivity implements SensorObserver {

    private AndroidPlugin mAndroidPlugin;

    //variable miembro para el objeto Display
    private Display mDisplay;

    private TextView txtAccelerometer;
    private TextView txtMagneticField;
    private TextView txtUncalibratedMagneticField;
    private TextView txtLight;
    private TextView txtProximity;
    private TextView txtTemperature;
    private TextView txtPressure;
    private TextView txtHumidity;
    private TextView txtOrientation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtAccelerometer = (TextView) findViewById(R.id.txtAccelerometer);
        txtMagneticField = (TextView) findViewById(R.id.txtMagneticField);
        txtUncalibratedMagneticField = (TextView) findViewById(R.id.txtUncalibratedMagneticField);
        txtLight = (TextView) findViewById(R.id.txtLight);
        txtProximity = (TextView) findViewById(R.id.txtProximity);
        txtTemperature = (TextView) findViewById(R.id.txtTemperature);
        txtPressure = (TextView) findViewById(R.id.txtPressure);
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtOrientation = (TextView) findViewById(R.id.txtOrientation);

        //obtener una referencia al administrador de ventanas y luego obtener la pantalla predeterminada. Utilizar la pantalla para obtener la rotación en OnSensorChanged ()
        WindowManager wm = (WindowManager) getSystemService (WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay ();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAndroidPlugin = AndroidPlugin.getInstance();
        mAndroidPlugin.setContext(this);
        mAndroidPlugin.registerObserver(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mAndroidPlugin.stopEventListening();

    }

    @Override
    public void accelerometerUpdate() {
        float azimut = mAndroidPlugin.getAzimut();
        float pitch = mAndroidPlugin.getPitch();
        float roll = mAndroidPlugin.getRoll();
//        float[] quat = mAndroidPlugin.getQuaternion();

        txtAccelerometer.setText(mAndroidPlugin.getGravity()[0] + " m/s2 "
                + mAndroidPlugin.getGravity()[1] + " m/s2 "
                + mAndroidPlugin.getGravity()[2] + " m/s2 ");
    }

    @Override
    public void magneticUpdate() {
        txtMagneticField.setText(mAndroidPlugin.getMagneticValues()[0] + " uT "
                + mAndroidPlugin.getMagneticValues()[1] + " uT "
                + mAndroidPlugin.getMagneticValues()[2] + " uT");
    }

    @Override
    public void uncalibratedMagneticUpdate() {
        txtUncalibratedMagneticField.setText(mAndroidPlugin.getUncalibratedMagneticValues()[0] + " "
                + mAndroidPlugin.getUncalibratedMagneticValues()[1] + " "
                + mAndroidPlugin.getUncalibratedMagneticValues()[2]);
    }

    @Override
    public void lightUpdate() {
        txtLight.setText( mAndroidPlugin.getLight()[0] + " lux");
    }

    @Override
    public void proximityUpdate() {
       txtProximity.setText(mAndroidPlugin.getProximity()[0] + " cm");
    }

    @Override
    public void temperatureUpdate() {
        txtTemperature.setText( mAndroidPlugin.getTemperature()[0] + " ºC");
    }

    @Override
    public void pressureUpdate() {
        txtPressure.setText( mAndroidPlugin.getPressure() [0] + " hPa");
    }

    @Override
    public void humidityUpdate() {
        txtHumidity.setText( mAndroidPlugin.getHumidity() [0] + " %");

    }

    @Override
    public void orientationUpdate() {
        txtOrientation.setText("azimut: " + mAndroidPlugin.getAzimut() + "\n" +
                "pitch: " + mAndroidPlugin.getPitch() + "\n" +
                "roll: " + mAndroidPlugin.getRoll());
    }


}
