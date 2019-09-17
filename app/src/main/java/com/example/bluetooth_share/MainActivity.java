package com.example.bluetooth_share;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener, View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();


    BluetoothService btservice;
    ListView listView;
    List list;
    TextView btstatus;
    Switch switch1;
    BluetoothAdapter adapter;
    Button paired;
    MyAdapter adp;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (state){
                    case BluetoothAdapter.STATE_ON:

                        Log.d(TAG, "state is on");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "state is turning on....");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "state is off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "state is turning of ...");
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
        btstatus = findViewById(R.id.btstatus);
        switch1 = findViewById(R.id.switch1);
        listView = findViewById(R.id.listview);
        paired = findViewById(R.id.button);
        //create a new bluetooth adapter
        adapter = BluetoothAdapter.getDefaultAdapter();

        listView.setOnItemClickListener(this);
        switch1.setOnCheckedChangeListener(this);
        paired.setOnClickListener(this);
        list = new ArrayList();
    }

    public void enableBt(){
        //test if device has bluetooth. if it doesn`t exit after 5sec
        if (adapter == null){
            showToast("Device doesnt support bluetooth. Exiting application...");

            try {
                wait(5000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            this.finish();
        }

        //check if the bluetooth device is enable. if not prompt the user to enable
        if(!adapter.isEnabled()){
            Intent enable_bluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enable_bluetooth);

            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(receiver, filter);

        }
    }
    public void disableBt(){
        if(adapter.isEnabled()) {
            adapter.disable();
            btstatus.setText("Bluetooth now disabled");
            paired.setEnabled(false);
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(receiver, filter);
            if(adp != null) {
                adp.clear();
            }
        }

    }
    public void showPaired(){
        if(adp != null){
            adp.clear();
        }
        //get all paired devices
        Set<BluetoothDevice> myPairedDevices = adapter.getBondedDevices();

        if(myPairedDevices.size() > 0){
            for (BluetoothDevice bt :myPairedDevices){
                list.add(bt);
            }
            Log.i(TAG, list.toString());
            adp = new MyAdapter(this, list);
            listView.setAdapter(adp);
        }
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Object random = parent.getItemAtPosition(position);
        BluetoothDevice device = (BluetoothDevice) random;
        btservice = new BluetoothService();
        btservice.startClient(device);
        Log.d(TAG, device.getName()  +" " +device.getAddress());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "switch has been toggled");
        if (isChecked){
            paired.setEnabled(true);
            enableBt();
            btstatus.setText("Bluetooth is on");

        }else {
            disableBt();

        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "button has been toggled");
        showPaired();
    }


}
