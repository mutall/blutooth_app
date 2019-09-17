package com.example.bluetooth_share;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "kiserian";


    ListView listView;
    List list;
    TextView btstatus;
    Switch switch1;
    BluetoothAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btstatus = findViewById(R.id.btstatus);
        switch1 = findViewById(R.id.switch1);
        listView = findViewById(R.id.listview);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object random = parent.getItemAtPosition(position);
                BluetoothDevice device = (BluetoothDevice) random;
                new ServerBt(adapter);
                new ClientBt(device, adapter);
            }
        });
        list = new ArrayList();

        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectBluetooth();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                showToast("settings clicked");
                break;
            case R.id.click:
                showToast("you have clicked me");
                break;
            default:
                showToast("Invalid selection");
        }


        return super.onOptionsItemSelected(item);
    }

    public void connectBluetooth(){
        //create a new bluetooth adapter
        adapter = BluetoothAdapter.getDefaultAdapter();

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
            startActivityForResult(enable_bluetooth, 1);
            btstatus.setText("Bluetooth is on");
        }
        //get all paired devices
        Set<BluetoothDevice> myPairedDevices = adapter.getBondedDevices();

        if(myPairedDevices.size() > 0){
            for (BluetoothDevice bt :myPairedDevices){
                list.add(bt);
            }
            Log.i(TAG, list.toString());
            MyAdapter adp = new MyAdapter(this, list);
//            ArrayAdapter adpt = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, list);
            listView.setAdapter(adp);
        }

    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            showToast("bluetooth connected");
        }else if(resultCode == RESULT_CANCELED){
            showToast("user cancelled");
        }
    }
}
