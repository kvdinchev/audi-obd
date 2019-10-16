package com.obd.audi.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import static com.obd.audi.utils.Constants.MAC_ADDRESS;
import static com.obd.audi.utils.Constants.UUID_STRING;

public class BluetoothConnection {

    private BluetoothSocket bluetoothSocket;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private UUID uuid = UUID.fromString(UUID_STRING);

    private final static BluetoothConnection bluetoothConnection = new BluetoothConnection();

    public static BluetoothConnection getInstance() {
        return bluetoothConnection;
    }

    private BluetoothConnection() {
    }

    public void connect() throws IOException {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(MAC_ADDRESS);
        bluetoothAdapter.cancelDiscovery();
        createBluetoothSocket();
    }

    public void createBluetoothSocket() {
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
        } catch (Exception e1) {
            Class<?> clazz = bluetoothDevice.getClass();
            Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
            try {
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{Integer.valueOf(1)};
                bluetoothSocket = (BluetoothSocket) m.invoke(bluetoothDevice, params);
                bluetoothSocket.connect();
            } catch (Exception e2) {

            }
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public boolean isBluetoothSocketConnected() {
        if (bluetoothSocket != null) {
            return bluetoothSocket.isConnected();
        } else {
            return false;
        }
    }

    public InputStream getInputStream() {
        try {
            return bluetoothSocket.getInputStream();
        } catch (Exception e) {

        }
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        };
    }

    public OutputStream getOutputStream() {
        try {
            return bluetoothSocket.getOutputStream();
        } catch (Exception e) {

        }
        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        };
    }
}
