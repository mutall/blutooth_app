package com.example.bluetooth_share;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MyAdapter extends ArrayAdapter {
    private Context context;
    private List<BluetoothDevice> list;

    MyAdapter(Context c, List<BluetoothDevice> list){
        super(c, 0, list);
        this.list = list;
        this.context = c;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.single_row, parent, false);
            BluetoothDevice bluetooth_device = list.get(position);

            TextView t_name = convertView.findViewById(R.id.name);
            TextView t_mac = convertView.findViewById(R.id.mac_address);

            t_name.setText(bluetooth_device.getName());
            t_mac.setText(bluetooth_device.getAddress());
        }

        return convertView;
    }
}
