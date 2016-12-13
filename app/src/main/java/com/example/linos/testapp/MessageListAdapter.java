package com.example.linos.testapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by MyPC on 12/12/2016.
 */

public class MessageListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Message> mMessageList;


    public MessageListAdapter(Context mContext, List<Message> mMessageList) {
        this.mContext = mContext;
        this.mMessageList = mMessageList;
    }

    public int getCount() {
        return mMessageList.size();
    }

    public Object getItem(int position) {
        return mMessageList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.messageview, null);
        TextView contactName = (TextView) v.findViewById(R.id.messageName);
        TextView time = (TextView) v.findViewById(R.id.messageTime);
        ImageView msgUnreadImg = (ImageView) v.findViewById(R.id.viewMessageImg);

        contactName.setText(mMessageList.get(position).getSenderName());
        String timeText = mMessageList.get(position).getTimeString();
        time.setText(timeText);
        boolean read = mMessageList.get(position).read();
        if (read) {
            msgUnreadImg.setVisibility(View.INVISIBLE);
            String text = "Read: " + timeText;
            time.setText(text);

        } else {
            msgUnreadImg.setVisibility(View.VISIBLE);
        }

        v.setTag(mMessageList.get(position).getSender());
        return v;
    }
}

