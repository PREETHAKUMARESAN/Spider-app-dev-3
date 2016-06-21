package com.example.preethakumaresan.myapplication;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements SensorEventListener, AdapterView.OnItemSelectedListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    public double sensory=0;
    public boolean clickenable=false;

    public static int[] images = {R.mipmap.harry, R.mipmap.hermione, R.mipmap.snape, R.mipmap.malfoy};
    private ImageView img;
    MediaPlayer mp;
    public int i = 0;
    public Button enable,disable,play,stop;
    public boolean end = false;
    private double times = 0,start=0;
    private TextView time;
    public boolean value = true;

    public int paatu;
    public int pos;
    public Spinner spinner;

    MainActivity2 m=new MainActivity2(MainActivity.this,play,stop);



    //Handler for the timer
    private Handler timehandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            times = SystemClock.uptimeMillis() - start;
            int ms = (int) times % 1000;
            int sec = (int) (times/ 1000) % 60;
            time.setText(String.format("%02d:%03d",sec,ms));
        }
    };

    //Handler for changing the images
    private Handler imagehandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            img.setImageResource(images[i]);
            if (i == images.length-1 )
            {((Button) findViewById(R.id.slideshow)).setEnabled(true);
                enable.setEnabled(true);
                end = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sensor
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(mSensorManager.getSensorList(Sensor.TYPE_PROXIMITY).size()!=0)
        {
            mSensor = mSensorManager.getSensorList(Sensor.TYPE_PROXIMITY).get(0);
            mSensorManager.registerListener((SensorEventListener) this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(MainActivity.this,"Sorry no sensor :(",Toast.LENGTH_SHORT).show();
        }

        enable=(Button)findViewById(R.id.enable);
        disable=(Button)findViewById(R.id.disable);
        //pause=(Button)findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);
        play = (Button) findViewById(R.id.play);
        time = (TextView) findViewById(R.id.timer);


        spinner = (Spinner) findViewById(R.id.spinner);

        assert spinner != null;
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.tracks, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        assert spinner != null;
        spinner.setAdapter(adapter);
        //enable.setTextColor(getResources().getColor(R.color.white));

        spinner.setOnItemSelectedListener(this);

        pos = spinner.getSelectedItemPosition();

        img = (ImageView) findViewById(R.id.slideShow);


        //onClick for image slideshow
        ((Button) findViewById(R.id.slideshow)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button) findViewById(R.id.slideshow)).setEnabled(false);
                enable.setEnabled(false);
                disable.setEnabled(false);
               //slideshow
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            for (i=-1;i<images.length-1;++i) {
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                imagehandle.sendEmptyMessage(0);
                            }
                        }
                    }
                };
                start= SystemClock.uptimeMillis();
                end = false;
                Thread thread = new Thread(runnable);
                thread.start();



                //The runnable for the thread for timer
                Runnable runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            while (!end) {
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                timehandle.sendEmptyMessage(0);
                            }
                        }
                    }
                };
                Thread timer = new Thread(runnable1);
                timer.start();
            }
        });


        enable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {enable.setEnabled(false);
                m=new MainActivity2(MainActivity.this,play,stop);
                clickenable= true;
                ((Button) findViewById(R.id.slideshow)).setEnabled(false);
                disable.setEnabled(true);
            }
        });

        disable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {disable.setEnabled(false);
                ((Button) findViewById(R.id.slideshow)).setEnabled(true);
                clickenable=false;
                enable.setEnabled(true);
            }
        });

        play.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) { value=true;
                m = new MainActivity2(MainActivity.this,play,stop);
                stop.setEnabled(true);play.setEnabled(false);
                m.execute(paatu);
            }
        });

        //Stop the song
        stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { m.cancel(true);
                value = false; play.setEnabled(true); stop.setEnabled(false);
                m.mp.stop();
                m.mp.release();

            }
        });}




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        paatu =spinner.getSelectedItemPosition();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener((SensorEventListener)MainActivity.this);
        m.mp.release();
        m.mp  = null;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener((SensorEventListener)MainActivity.this);
        if(value) {
            m.mp.stop();
            m.mp.release();}

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub

        sensory= event.values[0];
        if(clickenable) {
            if(sensory<1.0) {
                i++;
                i = i % images.length;
                img.setImageResource(images[i]);
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

}

