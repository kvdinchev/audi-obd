package com.obd.audi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.obd.audi.utils.BluetoothConnection;

import java.io.IOException;

import static com.obd.audi.utils.Constants.MAC_ADDRESS;

public class MainActivity extends AppCompatActivity {

    BluetoothConnection bluetoothConnection;

    public MainActivity() throws IOException {
//        bluetoothConnection = new BluetoothConnection(MAC_ADDRESS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.textView);
    }
}
