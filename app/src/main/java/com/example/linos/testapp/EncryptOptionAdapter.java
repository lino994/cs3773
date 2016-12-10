package com.example.linos.testapp;

import android.widget.ArrayAdapter;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import static android.R.attr.resource;

/**
 * Adaptor for ListView of Encryption options for user to check
 * on or off
 * Created by linos on 12/9/2016.
 */

public class EncryptOptionAdapter extends ArrayAdapter <EncryptCheckBoxModel> {
   Context context;
   EncryptCheckBoxModel [] options = null;
    public EncryptOptionAdapter(Context context, EncryptCheckBoxModel [] options) {
        super(context, R.layout.checkbox_layout, options);
        this.context = context;
        this.options = options;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.checkbox_layout, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.checkText);
        final CheckBox cb = (CheckBox) convertView.findViewById(R.id.optCheckBox);
        name.setText(options[position].getOption());
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isChecked = cb.isChecked();
                options[position].setIsChecked(isChecked);
            }
        });

        return convertView;
    }
}

