package com.example.bluetooth_share;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

public class ClientBt extends Thread {
    BluetoothDevice dvc;
    BluetoothSocket socket;
    BluetoothAdapter adp;
    ClientBt(BluetoothDevice device, BluetoothAdapter adp){
        this.dvc = device;
        this.adp = adp;
        UUID uuid = UUID.fromString("893c2250-d606-11e9-bb65-2a2ae2dbcce4");
        try {
            socket = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            if(adp.isDiscovering()){
                adp.cancelDiscovery();
            }
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
