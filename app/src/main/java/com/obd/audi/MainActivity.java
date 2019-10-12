package com.obd.audi;

import android.content.Intent;
import android.os.Bundle;
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
        getLiveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    executeCommonCommands();
                    while (!Thread.currentThread().isInterrupted()) {
                        rpmCommand();
                        speedCommand();
                        rpm.setText(rpmCommand());
                        speed.setText(rpmCommand());
                    }
                } catch (Exception e) {
                    editText.setText(editText.getText() + "Main - " + e.getMessage());
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });

    }

    private void executeCommonCommands() throws IOException, InterruptedException {
        new EchoOffCommand().run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        new LineFeedOffCommand().run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        new TimeoutCommand(125).run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        new SelectProtocolCommand(ObdProtocols.ISO_9141_2).run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
    }

    private String rpmCommand() throws IOException, InterruptedException {
        rpmCommand.run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        return String.valueOf(rpmCommand.getFormattedResult());
    }

    private String speedCommand() throws IOException, InterruptedException {
        speedCommand.run(bluetoothConnection.getInputStream(), bluetoothConnection.getOutputStream());
        return String.valueOf(speedCommand.getFormattedResult());
    }

}
