package com.example.bluetooth_share;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ListAdapter implements AdapterView.OnItemClickListener {
    public  ListAdapter(ListView listView){
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object r = parent.getItemAtPosition(position);
        Log.i("tag", r.toString());
    }
}
