package com.example.bluetooth_share;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.pm.ApplicationInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ServerBt extends Thread {
    BluetoothServerSocket bluetoothServerSocket;
    public ServerBt(BluetoothAdapter adapter){
        UUID uuid  = UUID.fromString("893c2250-d606-11e9-bb65-2a2ae2dbcce4");

        try {
            bluetoothServerSocket = adapter.listenUsingInsecureRfcommWithServiceRecord("Bluetooth_share", uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        BluetoothSocket bluetoothSocket;
        while (true) {
            try {
                bluetoothSocket = bluetoothServerSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            if (bluetoothSocket != null){

                manageConnection(bluetoothSocket);
                close();
                break;
            }
        }

    }

    public void close(){
        try {
            bluetoothServerSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void manageConnection(BluetoothSocket sock){}
}
