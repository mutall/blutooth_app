package com.example.bluetooth_share;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

class BluetoothService {
    private static final String TAG = BluetoothService.class.getSimpleName();
    private ServerBt serverBt;
    private ClientBt clientBt;
    private ConnectedBt connectedBt;
    private BluetoothAdapter adapter;

    public BluetoothService(){
        adapter = BluetoothAdapter.getDefaultAdapter();
        initializeServer();
    }

    private void initializeServer() {
        if (serverBt == null){
            serverBt = new ServerBt(adapter);
            serverBt.start();
        }
    }

    public void startClient(BluetoothDevice device){
        clientBt = new ClientBt(device, adapter);
        clientBt.start();
    }

    public void manageConnection(BluetoothSocket bt){
        ConnectedBt connectedBt = new ConnectedBt(bt);
        connectedBt.start();

    }

    private class ClientBt extends Thread {
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
                Log.d(TAG, "client socket has connected");
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            manageConnection(socket);
        }
    }

    private class ServerBt extends Thread {
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
                    Log.d(TAG, "server socket has connected");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
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


    }

    private class ConnectedBt extends Thread{
        private InputStream inputStream;
        private OutputStream outputStream;
        private byte[] bytes;

        public ConnectedBt(BluetoothSocket socket){


            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            }catch (IOException e){
                Log.e(TAG, e.getMessage());
            }
        }

        @Override
        public void run() {
            bytes = new byte[1024];
            while (true) {
                try {
                    int no_of_bytes = inputStream.read(bytes);
                    String message = new String(bytes, 0, no_of_bytes);
                    Log.i(TAG, message);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                    break;
                }
            }
        }

        public void write(byte[] out){
            try {
                outputStream.write(out);
                Log.d(TAG, new String(out));
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
