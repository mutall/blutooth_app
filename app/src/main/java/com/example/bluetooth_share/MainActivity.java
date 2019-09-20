package com.example.bluetooth_share;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    ListView listView;
    List<BluetoothDevice> list, scannedDevices;
    BluetoothAdapter adapter;
    Switch aSwitch;
    TextView tview;
    MyAdapter adp;
    Button paired, send;
    EditText data;
    private static final int SET_DISCOVERBLE = 20;
    BluetoothService service;


    BroadcastReceiver bluetooth_state = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(adapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        service = new BluetoothService(MainActivity.this);
                        Log.d(TAG, "State on");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "state turning on...");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "state off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "state turning off..");
                }
            }
        }
    };

    BroadcastReceiver scan_mode = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: "+device.getName());
                StringBuilder builder = new StringBuilder();
                Log.d(TAG, builder.append("Bluetooth device found ").append(device.getName()).append(device.getAddress()).toString());
                scannedDevices.add(device);

                adp = new MyAdapter(MainActivity.this, scannedDevices);
                adp.notifyDataSetChanged();
                listView.setAdapter(adp);
                adapter.cancelDiscovery();
            }
        }
    };

    BroadcastReceiver discoverable = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)){
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0);
                switch (mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        showToast("device is discoverable");
                        Log.d(TAG, "device is discoverable");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        showToast("Not discoverable but can connect");
                        Log.d(TAG, "Not discoverable but can connect");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        showToast("Not discoverable! Cannot connect");
                        Log.d(TAG, "Not discoverable! Cannot connect");
                        break;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        aSwitch = findViewById(R.id.switch1);
        listView = findViewById(R.id.listview);
        tview = findViewById(R.id.textView);
        paired = findViewById(R.id.paired_btn);
        send = findViewById(R.id.send);
        data = findViewById(R.id.editText2);



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!data.getText().toString().equals("")){
                    String msg = data.getText().toString();
                    service.write(msg.getBytes());
                }
            }
        });

        //create a new bluetooth adapter
        adapter = BluetoothAdapter.getDefaultAdapter();

        if(adapter.isEnabled()){
            service = new BluetoothService(MainActivity.this);
            paired.setEnabled(true);
            tview.setText("bluetooth enabled");
            aSwitch.setChecked(true);
        }

        paired.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        aSwitch.setOnCheckedChangeListener(this);

        list = new ArrayList();
        scannedDevices = new ArrayList<>();
    }

    public void enableBt() {
        //test if device has bluetooth. if it doesn`t exit after 5sec
        if (adapter == null) {
            showToast("Device doesnt support bluetooth. Exiting application...");

            try {
                wait(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.finish();
        }

        //check if the bluetooth device is enable. if not prompt the user to enable
        if (!adapter.isEnabled()) {
            Log.d(TAG, "bluetooth not enabled. enabling...");
            Intent enable_bluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enable_bluetooth);
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(bluetooth_state, filter);
            tview.setText("Bluetooth enabled");
            paired.setEnabled(true);

        }
    }

    public void disableBt() {
        if (adapter.isEnabled()) {
            adapter.disable();
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(bluetooth_state, filter);
            tview.setText("Bluetooth disabled");
            paired.setEnabled(false);
            if(adp != null){
                adp.clear();
            }
        }
    }

    public void enableDiscoveraility(){

        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(discoverable, filter);
        startActivityForResult(intent, SET_DISCOVERBLE);


    }

    public void scanDevices(){
        if(adapter.isDiscovering()){
            adapter.cancelDiscovery();
            Log.d(TAG, "Stopped discovery");

            adapter.startDiscovery();
            Log.d(TAG, "started discovery");
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(scan_mode, filter);
        }else{
            adapter.startDiscovery();
            Log.d(TAG, "started discovery");
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(scan_mode, filter);
        }
    }

    public void showPairedDevices() {
        if(adp != null){
            adp.clear();
        }
        //get all paired devices
        Set<BluetoothDevice> myPairedDevices = adapter.getBondedDevices();
        if (myPairedDevices.size() > 0) {
            for (BluetoothDevice bt : myPairedDevices) {
                list.add(bt);
            }
            Log.d(TAG, list.toString());
            adp = new MyAdapter(MainActivity.this, list);
            adp.notifyDataSetChanged();
            listView.setAdapter(adp);
        }

    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object random = parent.getItemAtPosition(position);
        BluetoothDevice device = (BluetoothDevice) random;
        service.startClient(device);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Log.i(TAG, "You toggled on");
            enableBt();
//            enableDiscoveraility();
        }else {
            disableBt();
        }
    }

    @Override
    public void onClick(View v) {
//        scanDevices();
        showPairedDevices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adapter.isEnabled()){
            adapter.cancelDiscovery();
            adapter.disable();
        }
        unregisterReceiver(bluetooth_state);
//        unregisterReceiver(scan_mode);
        unregisterReceiver(discoverable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == SET_DISCOVERBLE){
                showToast("device discoverable for 5min");

                Log.d(TAG, "onActivityResult: device discoverable for 5min");
            }
        }
    }
}
