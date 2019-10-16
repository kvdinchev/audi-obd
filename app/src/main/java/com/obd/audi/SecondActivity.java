package com.obd.audi;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.pires.obd.commands.pressure.BarometricPressureCommand;
import com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand;
import com.obd.audi.utils.BluetoothConnection;

import java.io.IOException;

public class SecondActivity extends AppCompatActivity {

    private float x1, x2, y1, y2;
    private BluetoothConnection bluetoothConnection;
    Runnable updateTextRunnable;
    private Handler handler = new Handler();
    private TextView turboBoost;
    private ProgressBar turboBoostBar;
    private EditText editText;
    private IntakeManifoldPressureCommand intakeManifoldPressureCommand = new IntakeManifoldPressureCommand();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_activity);
        bluetoothConnection = BluetoothConnection.getInstance();
        turboBoost = findViewById(R.id.turboBoost);
        turboBoostBar = findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            turboBoostBar.setMin(-5);
        }
        editText = findViewById(R.id.editText);

        updateTextRunnable = new Runnable() {
            public void run() {
                try {
                    float boostFloat = turboBoostCommand();
                    turboBoost.setText(String.valueOf(boostFloat));
                    turboBoostBar.setProgress(Math.round(boostFloat));
                } catch (Exception e) {
                    editText.append(" turboBoostCommand - " + e.getMessage());
                }
//                editText.setText(" no ex screen 2");
                handler.postDelayed(this, 100);
            }
        };
        handler.post(updateTextRunnable);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                if(x1 < x2) {
                    handler.removeCallbacksAndMessages(null);
                    startActivity(new Intent(SecondActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
                break;
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public float turboBoostCommand() throws IOException, InterruptedException {
        intakeManifoldPressureCommand.run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        return intakeManifoldPressureCommand.getImperialUnit();
    }
}