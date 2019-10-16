package com.obd.audi.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothConnection {

    private BluetoothSocket bluetoothSocket;
    private BluetoothAdapter bluetoothAdapter;
    private EditText editText;

    public BluetoothConnection(String macAddress, EditText editText) {
        this.editText = editText;
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        while (!bluetoothSocket.isConnected()) {
            try {
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
                bluetoothAdapter.cancelDiscovery();
                bluetoothSocket = connect(device, uuid);
            } catch (Exception e) {
                editText.setText(editText.getText() + "bluetoothSocket exception - " + e.getMessage());
            }
//        }
    }

    private BluetoothSocket connect(BluetoothDevice dev, UUID uuid) throws IOException {
        BluetoothSocket sock = null;
        BluetoothSocket sockFallback;

        try {
            sock = dev.createRfcommSocketToServiceRecord(uuid);
            sock.connect();
        } catch (Exception e1) {
            editText.setText(editText.getText() + "cant Connect bluetoothSocket 1");
            if(sock!=null) {
                Class<?> clazz = sock.getRemoteDevice().getClass();
                Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
                try {
                    Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                    Object[] params = new Object[]{Integer.valueOf(1)};
                    sockFallback = (BluetoothSocket) m.invoke(sock.getRemoteDevice(), params);
                    sockFallback.connect();
                    sock = sockFallback;
                    editText.setText("Connection successful");
                } catch (Exception e2) {
                    editText.setText(editText.getText() + "cant Connect bluetoothSocket 2");
                }
            }
        }
        return sock;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public boolean isBluetoothSocketConnected() {
        return bluetoothSocket.isConnected();
    }

    public InputStream getInputStream() {
        try {
            return bluetoothSocket.getInputStream();
        } catch (Exception e) {
            editText.setText(editText.getText() + "inputStream - " + e.getMessage());
        }
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        };
    }

    public OutputStream getOutputStream(){
        try {
            return bluetoothSocket.getOutputStream();
        } catch (Exception e) {
            editText.setText(editText.getText() + "inputStream - " + e.getMessage());
        }
        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        };
    }
}
