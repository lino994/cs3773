package com.example.linos.testapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class EnterKey extends AppCompatActivity {
    Button bSubmit;
    String key;
    EditText edKey;
    Bundle info;
    String action;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_key);
        Intent myIntent = getIntent();

        /*
          username of sender and reciever to be sent to next activity
          after user decides on encryption type it is put in a bundle
          to be passed along until user sends message
         */
        info = myIntent.getExtras();
        bSubmit = (Button) findViewById(R.id.bSubmitKey);
        edKey = (EditText) findViewById(R.id.edKey);
        action = info.getString("action");

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key = edKey.getText().toString();
                if(!key.equals("") && key.length() >= 8) {

                    info.putBoolean("needsKey", true);
                    info.putString("key", key);

                    /*start send message activity */
                    Intent newIntend = new Intent(EnterKey.this, SendMessage.class);
                    if (action.equals("readMessage")) {
                        newIntend = new Intent(EnterKey.this, MessageDisplay.class);
                    }
                    newIntend.putExtras(info);
                    startActivity(newIntend);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Key Not Valid", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
