package com.example.bluetooth_share;

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
    private List<HashMap<String, String>> list;

    MyAdapter(Context c, List<HashMap<String, String>> list){
        super(c, 0, list);
        this.list = list;
        this.context = c;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.single_row, parent, false);
            HashMap bluetooth_device = list.get(position);

            TextView t_name = convertView.findViewById(R.id.name);
            TextView t_mac = convertView.findViewById(R.id.mac_address);

            t_name.setText(Objects.requireNonNull(bluetooth_device.get("device")).toString());
            t_mac.setText(Objects.requireNonNull(bluetooth_device.get("mac")).toString());


        }

        return convertView;
    }
}
