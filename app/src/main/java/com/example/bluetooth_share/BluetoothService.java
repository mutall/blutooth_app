package com.example.bluetooth_share;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothService {
    private BluetoothAdapter adapter;
    private static final String TAG = BluetoothService.class.getSimpleName();
    private ClientBt clientBt;
    private ServerBt serverBt;
    private ConnectedBt connectedBt;
    public BluetoothService(){
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        initialize();
    }

    public void initialize(){
        if(clientBt != null){
            clientBt.cancel();
            clientBt = null;
        }

        if (serverBt == null){
            serverBt = new ServerBt();
            serverBt.start();
        }
    }

    public void startClient(BluetoothDevice device){
        clientBt = new ClientBt(device);
        clientBt.start();
    }

    public void manageConnection(BluetoothSocket sock){
        Log.d(TAG, "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        connectedBt = new ConnectedBt(sock);
        connectedBt.start();
    }

    public void write(byte[] bytes){
        connectedBt.write(bytes);
    }

    private class ClientBt extends Thread {
        BluetoothDevice dvc;
        BluetoothSocket socket;
        BluetoothAdapter adp;
        ClientBt(BluetoothDevice device){
            this.dvc = device;
            this.adp = BluetoothService.this.adapter;
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

            manageConnection(socket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    private class ServerBt extends Thread {
        BluetoothServerSocket bluetoothServerSocket;
        public ServerBt(){
            UUID uuid  = UUID.fromString("893c2250-d606-11e9-bb65-2a2ae2dbcce4");

            try {
                bluetoothServerSocket = BluetoothService.this.adapter.listenUsingInsecureRfcommWithServiceRecord("Bluetooth_share", uuid);
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
    }

    private class ConnectedBt extends Thread{
        private BluetoothSocket socket;
        private InputStream inputStream;
        private OutputStream outputStream;
        private byte[] buffer;


        public ConnectedBt(BluetoothSocket socket){
            this.socket = socket;

            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            buffer = new byte[2048];
            int bytes;

            while (true){
                try{
                    bytes = inputStream.read(buffer);
                    String msg = new String(buffer, 0, bytes);
                    Log.d(TAG, msg);



                }catch(IOException e){
                    e.printStackTrace();
                    break;
                }
            }


        }

        public void write(byte[] bytes){
            String msg = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, msg);
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();

            }
        }

        public void close(){
            try {
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
