package com.obd.audi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import static com.obd.audi.utils.Constants.MAC_ADDRESS;

public class MainActivity extends AppCompatActivity {

    private BluetoothConnection bluetoothConnection;
    private TextView textView;
    private TextView rpm;
    private TextView speed;
    private Button nextButton;
    private Button btButton;
    private Button getLiveData;
    private int num = 1;
    private Handler updater;
    private boolean isGetData = true;

    private EditText editText;
    private RPMCommand rpmCommand = new RPMCommand();
    private SpeedCommand speedCommand = new SpeedCommand();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        btButton = (Button) findViewById(R.id.connectBt);
        getLiveData = (Button) findViewById(R.id.getLiveData);
        textView = (TextView) findViewById(R.id.textView);
        rpm = (TextView) findViewById(R.id.rpm);
        speed = (TextView) findViewById(R.id.speed);
        nextButton = (Button) findViewById(R.id.nextButton);
        btButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothConnection = new BluetoothConnection(MAC_ADDRESS, editText);
            }
        });

        updater = new Handler();
        final Thread myThread = new Thread() {
            @Override
            public void run() {
                while (isGetData) {
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        editText.setText(e.getMessage());
                    }
                    updater.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                rpm.setText(rpmCommand());
                            } catch (Exception e) {
                                editText.setText(e.getMessage());
                            }
                        }
                    });
                num++;
                }
            }
        };
        getLiveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGetData = true;
                myThread.start();
//                try {
//                    executeCommonCommands();
//                    while (!Thread.currentThread().isInterrupted()) {
//                        rpm.setText(rpmCommand());
//                        speed.setText(rpmCommand());
//                    }
//                } catch (Exception e) {
//                    editText.setText(editText.getText() + "Main - " + e.getMessage());
//                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGetData = false;
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });

    }

    private void executeCommonCommands() throws IOException, InterruptedException {
        new EchoOffCommand().run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        new LineFeedOffCommand().run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        new TimeoutCommand(125).run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        new SelectProtocolCommand(ObdProtocols.AUTO).run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
    }

    private String rpmCommand() throws IOException, InterruptedException {
        rpmCommand.run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        return rpmCommand.getFormattedResult();
    }

    private String speedCommand() throws IOException, InterruptedException {
        speedCommand.run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        return String.valueOf(speedCommand.getFormattedResult());
    }

}
