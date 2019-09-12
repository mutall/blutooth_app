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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "kiserian";
    String name;
    String address;
    String btclass;
    ListView listView;
    List list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
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
        //create a ne w bluetooth adapter
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        //test if device has bluetooth. if it doesnt exit after 5sec
        if (adapter == null){
            showToast("Device does not support bluetooth. Exiting application...");

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
        }
        //get all paired devices
        Set<BluetoothDevice> myPairedDevices = adapter.getBondedDevices();

        if(myPairedDevices.size() > 0){
            for (BluetoothDevice bt :myPairedDevices){
                name=bt.getName();
                names.setText(name);
                address=bt.getAddress();
                addreses.setText(address);
                Log.i(TAG, bt.getName());
                Log.i(TAG, bt.getAddress());
                Log.i(TAG, bt.getBluetoothClass().toString());
            }
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
