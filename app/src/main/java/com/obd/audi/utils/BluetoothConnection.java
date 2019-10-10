package com.obd.audi.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothConnection {

    private BluetoothSocket bluetoothSocket;

    public BluetoothConnection(String macAddress) throws IOException {
        UUID uuid = UUID.fromString(macAddress);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
        bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
    }

    public InputStream getInputStream() throws IOException {
        return bluetoothSocket.getInputStream();
    }
}
