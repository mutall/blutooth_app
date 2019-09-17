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

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = "kiserian";
    ListView listView;
    List list;
    BluetoothAdapter adapter;
    Switch aSwitch;
    TextView tview;
    MyAdapter adp;
    Button paired;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(adapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
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

        //create a new bluetooth adapter
        adapter = BluetoothAdapter.getDefaultAdapter();

        if(adapter.isEnabled()){
            paired.setEnabled(true);
            tview.setText("bluetooth enabled");
            aSwitch.setChecked(true);
        }

        paired.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        aSwitch.setOnCheckedChangeListener(this);

        list = new ArrayList();
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
            registerReceiver(receiver, filter);
            tview.setText("Bluetooth enabled");
            paired.setEnabled(true);
        }
    }

    public void disableBt() {
        if (adapter.isEnabled()) {
            adapter.disable();
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(receiver, filter);
            tview.setText("Bluetooth disabled");
            paired.setEnabled(false);
            if(adp != null){
                adp.clear();
            }
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
        BluetoothService service = new BluetoothService();
        service.startClient(device);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Log.i(TAG, "You toggled on");
            enableBt();
        }else {
            disableBt();
        }
    }

    @Override
    public void onClick(View v) {
        showPairedDevices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
