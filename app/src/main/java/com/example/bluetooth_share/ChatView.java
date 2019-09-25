package com.example.bluetooth_share;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ChatView extends AppCompatActivity {
    private static final String TAG = ChatView.class.getSimpleName();
    public Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 10:
                    if(progressBar.isShowing()){
                        progressBar.dismiss();
                    }
                    showToast("connected you can now chat");
                    break;
                case 11:
                    String reply = msg.obj.toString();
                    list.add(new Chat(reply, 2));
                    adapter.notifyDataSetChanged();
                    break;
                case 12:
                    break;
            }


        }
    };
    ProgressDialog progressBar;
    EditText msg1;
    FloatingActionButton fab1, fab2;
    ArrayList<Chat> list;
    ListView listView;
    CustomAdapter adapter;
    BluetoothDevice bluetoothDevice;
    BluetoothService service;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);
        msg1 = findViewById(R.id.msg1);
        fab1 = findViewById(R.id.fab1);
        listView = findViewById(R.id.chatList);
        bluetoothDevice = getIntent().getParcelableExtra("bluetooth");
        service = new BluetoothService(handler);
        service.startClient(bluetoothDevice);
        progressBar = new ProgressDialog(this);
        progressBar.setTitle("connecting to "+bluetoothDevice.getName());
        progressBar.setCancelable(false);
        progressBar.show();


        list = new ArrayList();
        adapter = new CustomAdapter(this, list);
        listView.setAdapter(adapter);

        View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!msg1.equals("")){
                    list.add(new Chat(msg1.getText().toString(), 1));
                    service.write(msg1.getText().toString().getBytes());
                    Log.d(TAG, "onClick: "+msg1.getText().toString());
                    adapter.notifyDataSetChanged();
                    msg1.setText("");
                }
            }
        };
        fab1.setOnClickListener(listener1);
    }
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
