package com.obd.audi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.obd.audi.utils.BluetoothConnection;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private BluetoothConnection bluetoothConnection;
    private TextView rpm;
    private TextView speed;
    Runnable updateTextRunnable;
    private Handler handler = new Handler();

    private EditText editText;
    private RPMCommand rpmCommand = new RPMCommand();
    private SpeedCommand speedCommand = new SpeedCommand();

    private float x1, x2, y1, y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        rpm = findViewById(R.id.rpm);
        speed = findViewById(R.id.speed);
        bluetoothConnection = BluetoothConnection.getInstance();

        updateTextRunnable = new Runnable() {
            public void run() {
                try {
                    bluetoothConnection.connect();
                    executeCommonCommands();
                } catch (InterruptedException e) {
                    try {
                        bluetoothConnection.getBluetoothSocket().close();
                        bluetoothConnection.createBluetoothSocket();
                    } catch (IOException ex) {
                        editText.append(" IOException socket.close - " + e.getMessage());
                    }
                } catch (IOException e) {
                    editText.append(" IOException - " + e.getMessage());
                }

                try {
                    rpm.setText(rpmCommand());
                } catch (Exception e) {
                    editText.append(" rpmCommand - " + e.getMessage());
                }

                try {
                    speed.setText(speedCommand());
                } catch (Exception e) {
                    editText.append(" SPEED command - " + e.getMessage());
                }
//                editText.setText(" no ex ");
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
                if(x1 > x2) {
                    handler.removeCallbacksAndMessages(null);
                    startActivity(new Intent(MainActivity.this, SecondActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
        }
        return false;
    }

    private void executeCommonCommands() throws IOException, InterruptedException {
        new EchoOffCommand().run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        new LineFeedOffCommand().run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        new TimeoutCommand(125).run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        new SelectProtocolCommand(ObdProtocols.ISO_9141_2).run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
    }

    private String rpmCommand() throws IOException, InterruptedException {
        rpmCommand.run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        return rpmCommand.getFormattedResult();
    }

    private String speedCommand() throws IOException, InterruptedException {
        speedCommand.run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        return speedCommand.getFormattedResult();
    }

}
