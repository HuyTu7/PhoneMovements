package com.hqt.phonemm;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;


public class PhoneMovement extends Activity implements SensorEventListener, View.OnClickListener {

    private Button mMeasurement;
    private Button mEnd;
    private Button mSave;
    private Button mReset;
    private TextView linear_v;
    private TextView angular_v;
    private TextView linear_d;
    private TextView angular_d;
    private SensorManager mSensorManager;
    private Sensor gyrSensor;
    private Sensor accSensor;
    private Sensor gSensor;
    private ArrayList<Float>[] linearV = (ArrayList<Float>[]) new ArrayList[3];
    private ArrayList<Float>[] linearA = (ArrayList<Float>[]) new ArrayList[3];
    private ArrayList<Float>[] gravity = (ArrayList<Float>[]) new ArrayList[3];
    private ArrayList<Float>[] angularV = (ArrayList<Float>[]) new ArrayList[3];
    private boolean flag = false;
    private ArrayList<Float>[] time = (ArrayList<Float>[]) new ArrayList[2];
    private float[] lV = new float[3];
    private float[] g = new float[3];
    private float[] aV = new float[3];
    private long[] previousT = new long[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_movement);

        initialize();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gyrSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        mMeasurement = (Button) findViewById(R.id.b_button);
        mEnd = (Button) findViewById(R.id.e_button);
        mSave = (Button) findViewById(R.id.s_button);
        mReset = (Button) findViewById(R.id.r_button);
        mMeasurement.setOnClickListener(this);
        mEnd.setOnClickListener(this);
        mReset.setOnClickListener(this);
        mSave.setOnClickListener(this);

        linear_v = (TextView) findViewById(R.id.tv_acc);
        angular_v = (TextView) findViewById(R.id.tv_gyr);
        linear_d = (TextView) findViewById(R.id.tv_linear_d);
        angular_d = (TextView) findViewById(R.id.tv_angular_d);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    public void initialize() {
        //linearV[0] = new ArrayList<Float>();
        //linearV[1] = new ArrayList<Float>();
        //linearV[2] = new ArrayList<Float>();
        linearA[0] = new ArrayList<Float>();
        linearA[1] = new ArrayList<Float>();
        linearA[2] = new ArrayList<Float>();
        angularV[0] = new ArrayList<Float>();
        angularV[1] = new ArrayList<Float>();
        angularV[2] = new ArrayList<Float>();
        //gravity[0] = new ArrayList<Float>();
        //gravity[1] = new ArrayList<Float>();
        //gravity[2] = new ArrayList<Float>();
        time[0] = new ArrayList<Float>();
        time[1] = new ArrayList<Float>();
        time[0].add((float)0);
        time[1].add((float)0);
        //linearV[0].add((float) 0);
        //linearV[1].add((float) 0);
        //linearV[2].add((float) 0);
        linearA[0].add((float) 0);
        linearA[1].add((float) 0);
        linearA[2].add((float) 0);
        angularV[0].add((float) 0);
        angularV[1].add((float) 0);
        angularV[2].add((float) 0);
        //gravity[0].add((float) 0);
        //gravity[1].add((float) 0);
        //gravity[2].add((float) 0);
        //time.add((long)0);
    }


    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
        if (flag) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long[] current = new long[2];
        float entry;
        float rate;
        synchronized (this) {
            //if(flag) {
            switch (event.sensor.getType()) {
                    /*
                    case Sensor.TYPE_GRAVITY:
                        g[0] = event.values[0];
                        g[1] = event.values[1];
                        g[2] = event.values[2];
                        if (flag) {
                            gravity[0].add(g[0]);
                            gravity[1].add(g[1]);
                            gravity[2].add(g[2]);
                        }
                        break;*/
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    if (flag) {
                        current[0] = System.currentTimeMillis();
                        entry = time[0].get(time[0].size()-1) + (float)(current[0] - previousT[0])/((float)1000);
                        rate = time[0].get(time[0].size()-1)/entry;
                        lV = lowPass(event.values.clone(), lV.clone(), rate);
                        time[0].add(entry);


                        previousT[0] = current[0];
                        linearA[0].add(lV[0]);
                        linearA[1].add(lV[1]);
                        linearA[2].add(lV[2]);
                    }
                    linear_v.setText("Linear Acceleration: X= " + Float.toString(linearA[0].get(linearA[0].size() - 1))
                            + " Y= " + Float.toString(linearA[1].get(linearA[1].size() - 1))
                            + " Z= " + Float.toString(linearA[2].get(linearA[2].size() - 1)));
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    if (flag) {
                        aV = lowPass(event.values.clone(), aV, (float) (1 - 0.25));
                        current[1] = System.currentTimeMillis();
                        entry = (float)(current[1] - previousT[1])/((float)1000);
                        time[1].add(entry + time[1].get(time[1].size()-1));
                        previousT[1] = current[1];
                        angularV[0].add(aV[0]);
                        angularV[1].add(aV[1]);
                        angularV[2].add(aV[2]);
                    }
                    angular_v.setText("Angular Velocity: X= " + Float.toString(angularV[0].get(angularV[0].size() - 1))
                            + " Y= " + Float.toString(angularV[1].get(angularV[1].size() - 1))
                            + " Z= " + Float.toString(angularV[2].get(angularV[2].size() - 1)));
                    break;

            }
            //}
        }
    }

    /*
    public String ld_cal(){
        final float alpha = (float) 0.8;
        //String s = "linearV " + linearV[0].size() + ", " + linearV[1].size() + ", " + linearV[2].size();
        //s += "\n gravity " + gravity[0].size() + ", " + gravity[1].size() + ", " + gravity[2].size();
        // Isolate the force of gravity with the low-pass filter.
        /*
        for (int i = 0; i < gravity[0].size(); i++){
            g[0] = alpha * gravity[0].get(i) + (1 - alpha) * linearV[0].get(i);
            g[1] = alpha * gravity[1].get(i) + (1 - alpha) * linearV[1].get(i);
            g[2] = alpha * gravity[2].get(i) + (1 - alpha) * linearV[2].get(i);
            // Remove the gravity contribution with the high-pass filter.
            linearA[0].add(linearV[0].get(i) - gravity[0].get(i));
            linearA[1].add(linearV[1].get(i) - gravity[1].get(i));
            linearA[2].add(linearV[2].get(i) - gravity[2].get(i));
        }
        String s = "\n linearA " + linearA[0].size() + ", " + linearA[1].size() + ", " + linearA[2].size();
        //if(time[0].size() > linearA[0].size()){
        //    time[0].remove(time[0].size()-1);
        //}
        float dl = (float) linear_d_cal();
        //float dl = linear_d_cal();
        //return Float.toString(dl);
        s += " " + Float.toString(dl);
        return s;
    }*/

    public String linear_d_cal() {
        float[] d = {(float) 0, (float) 0, (float) 0};
        float[] v = {(float) 0, (float) 0, (float) 0};
        float dl;
        float dt;
        for (int i = 1; i < linearA[0].size(); i++) {
            dt = (float) (time[0].get(i) - time[0].get(i - 1));
            v[0] += (linearA[0].get(i) + linearA[0].get(i - 1)) / 2 * dt;
            v[1] += (linearA[1].get(i) + linearA[1].get(i - 1)) / 2 * dt;
            v[2] += (linearA[2].get(i) + linearA[2].get(i - 1)) / 2 * dt;
            d[0] += v[0] * dt;
            d[1] += v[1] * dt;
            d[2] += v[2] * dt;
        }
        dl = (float) Math.sqrt(d[0] * d[0] + d[1] * d[1] + d[2] * d[2]);
        return Float.toString(dl);
    }

    private String ad_cal() {
        float[] d = {(float) 0, (float) 0, (float) 0};
        float[] v = {(float) 0, (float) 0, (float) 0};
        float dl;
        float dt;
        for (int i = 1; i < angularV[0].size(); i++) {
            dt = (float) (time[1].get(i) - time[1].get(i - 1));
            d[0] += angularV[0].get(i) * dt;
            d[1] += angularV[1].get(i) * dt;
            d[2] += angularV[2].get(i) * dt;
        }
        dl = (float) Math.sqrt(d[0] * d[0] + d[1] * d[1] + d[2] * d[2]);
        dl = (dl * 180) / ((float) Math.PI);
        return Float.toString(dl);
    }

    protected float[] lowPass(float[] input, float[] output, float rate) {
        if (output == null) {
            return input;
        }
        for (int i = 0; i < input.length; i++) {
            //output[i] = output[i] + rate * (input[i] - output[i]);
            output[i] = (rate)*output[i] + (1-rate) * input[i];
        }
        return output;
    }

    private void writeFile(String filename, ArrayList<Float> t, ArrayList<Float>[] data) {
        File root = new File(Environment.getExternalStorageDirectory(), "PhoneMM");
        if(!root.exists()){
            root.mkdirs();
        }
        /*boolean alreadyExists = new File(root, filename).exists();
        if(alreadyExists){
            filename += "_1.csv";
        }
        else{
            filename += ".csv";
        }*/
        File file = new File(root, filename);

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(file));
            String[] line = {"time", "X_values", "Y_values", "Z_values"};

            for(int i = 0; i < data[0].size(); i++){
                writer.writeNext(line);
                line[0] = Float.toString((float)t.get(i));
                line[1] = Float.toString(data[0].get(i));
                line[2] = Float.toString(data[1].get(i));
                line[3] = Float.toString(data[2].get(i));
            }
            writer.flush();
            writer.close();
            Toast.makeText(getBaseContext(), "Done saving file", Toast.LENGTH_SHORT).show();
        }catch(java.io.IOException ioe){
            Toast.makeText(getBaseContext(), ioe.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        //long startTime = (long) -1;
        long startT;
        switch (v.getId()) {
            case R.id.b_button:
                flag = true;
                mMeasurement.setEnabled(false);
                mEnd.setEnabled(true);
                mSave.setEnabled(false);
                mReset.setEnabled(false);
                startT = System.currentTimeMillis();
                previousT[0] = startT;
                previousT[1] = startT;
                mSensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
                mSensorManager.registerListener(this, gSensor, SensorManager.SENSOR_DELAY_NORMAL);
                mSensorManager.registerListener(this, gyrSensor, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            case R.id.e_button:
                mMeasurement.setEnabled(false);
                mEnd.setEnabled(false);
                mSave.setEnabled(true);
                mReset.setEnabled(true);
                mSensorManager.unregisterListener(this);
                //String s = "Time " + time[0].size();
                linear_d.setText("Linear Displacement: " + linear_d_cal());
                angular_d.setText("Angular Displacement: " + ad_cal());
                break;
            case R.id.r_button:
                mMeasurement.setEnabled(true);
                mEnd.setEnabled(true);
                mReset.setEnabled(false);
                mSave.setEnabled(true);
                linear_v.setText("Linear Acceleration: ");
                angular_v.setText("Angular Velocity: ");
                linear_d.setText("Linear Displacement: ");
                angular_d.setText("Angular Displacement: ");
                initialize();
                break;
            case R.id.s_button:
                mSave.setEnabled(false);
                writeFile("linearA_1.csv", time[0], linearA);
                writeFile("angularV_1.csv", time[1], angularV);
        }

    }



}
