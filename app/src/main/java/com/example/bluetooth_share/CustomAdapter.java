package com.example.bluetooth_share;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter {
    private static final String TAG = CustomAdapter.class.getSimpleName();
    Context context;
    ArrayList<Chat> chatArrayList;
    Activity activity;
    public CustomAdapter(Context c, ArrayList<Chat> chats){
        super(c, 0, chats);
        this.context = c;
        this.chatArrayList = chats;
        activity = (Activity) c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            Chat chat = chatArrayList.get(position);
            Log.d(TAG, "getView: "+chat.getMsg());
            convertView = LayoutInflater.from(context).inflate(R.layout.single_chat, parent, false);
            TextView message = convertView.findViewById(R.id.chat_msg);
            LinearLayout layout = convertView.findViewById(R.id.linearLayout);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT);
            convertView.setPadding(10,15,10,15);
            if (chat.getUser() == 1){
                Log.d(TAG, "getView: sender");
                message.setGravity(Gravity.END);
                layout.setBackground(activity.getDrawable(R.drawable.borders_chat_sender));

            }else{
                Log.d(TAG, "getView: receiver");
                message.setGravity(Gravity.START);
                layout.setBackground(activity.getDrawable(R.drawable.borders_chat));
            }

            message.setText(chat.getMsg());
        }
        return convertView;
    }
}