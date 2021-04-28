package com.example.p41timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView output;
    private EditText input;
    private Chronometer chronometer;
    private SharedPreferences SharedPreferences;
    private SharedPreferences.Editor Editor;

    private ImageButton btnstart, btnpause, btnstop;

    private long timeOnPause;
    private boolean running;
    private long time,minutes,seconds, getTime;

    String workType,workOut,spendTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = findViewById(R.id.textview1);
        btnstart = findViewById(R.id.start);
        btnpause = findViewById(R.id.pause);
        btnstop = findViewById(R.id.stop);
        input = findViewById(R.id.entertype);

        chronometer = findViewById(R.id.chronometer);

        //load data
        SharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        Editor = SharedPreferences.edit();
        timeOnPause = 0;

        workType = SharedPreferences.getString("workType", "");
        time = SharedPreferences.getLong("time", 0);
        minutes = (time % (1000 * 60 * 60)) / (1000 * 60);
        seconds = (time % (1000 * 60)) / 1000;
        spendTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        output.setText("You spent " + spendTime + " on " + workType + " last time.");

        if (savedInstanceState != null) {
            timeOnPause = savedInstanceState.getLong("timeOnPause", 0);
            running = savedInstanceState.getBoolean("running", true);
            getTime = savedInstanceState.getLong("getTime", 0);
            if (!running) {
                chronometer.setBase(SystemClock.elapsedRealtime() - timeOnPause);
                chronometer.stop();
                running = false;
            } else if (running) {
                chronometer.setBase(SystemClock.elapsedRealtime() - (SystemClock.elapsedRealtime() - getTime));
                chronometer.start();
                running = true;
            }
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("timeOnPause", timeOnPause);
        outState.putBoolean("running", running);
        outState.putLong("getTime", chronometer.getBase());
    }

    // Start
    public void startChronometer(View view) {
        if (input.length() == 0) {
            Toast.makeText(MainActivity.this, "Please enter your workout type!", Toast.LENGTH_SHORT).show();
        }
        else {
            if (!running) {
            running = true;
            chronometer.setBase(SystemClock.elapsedRealtime() - timeOnPause);
            chronometer.start();
            }
        }
    }

    // Pause
    public void pauseChronometer(View view) {
        if (running) {
            chronometer.stop();
            timeOnPause = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }

    //stop
    private void updatedisplay(CharSequence time) {

        if (input.getText().toString().equals(""))
            output.setText("You spent " + time + " on your workTime last time.");
        else output.setText("You spent " + time + " on " + workOut + " last time.");
    }

    public void stopChronometer(View view) {

        if (!running) {
            chronometer.stop();
            long totalTime = timeOnPause;
            Editor.putLong("getTime", totalTime);
            Editor.putString("workType", input.getText().toString());
            Editor.apply();
            workOut = input.getText().toString();
            updatedisplay(chronometer.getText());
            timeOnPause = 0;
            chronometer.setBase(SystemClock.elapsedRealtime());

        } else {
            Toast.makeText(MainActivity.this, "Click the pause first please!", Toast.LENGTH_SHORT).show();
        }

    }



}