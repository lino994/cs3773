package com.example.linos.testapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by MyPC on 12/12/2016.
 */

public class ContactListAdapter extends BaseAdapter{

    private Context mContext;
    private List<Contact> mContactList;


    public ContactListAdapter(Context mContext, List<Contact> mContactList) {
        this.mContext = mContext;
        this.mContactList = mContactList;
    }

    public int getCount() {
        return mContactList.size();
    }

    public Object getItem(int position) {
        return mContactList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.activity_contactview, null);
        TextView contactName = (TextView) v.findViewById(R.id.contactName);
        TextView userName = (TextView) v.findViewById(R.id.userName);

        contactName.setText(mContactList.get(position).getName());
        userName.setText(mContactList.get(position).getUserName());

        v.setTag(mContactList.get(position).getName());
        return v;
    }
}
