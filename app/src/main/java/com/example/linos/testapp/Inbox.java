package com.example.linos.testapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Inbox extends AppCompatActivity {
    Bundle msgInfo;
    ArrayList<Message> inbox;
    String currentUser;
    String recv;
    String sender;
    String msg;
    DatabaseHelper myDb;
    ArrayList<String> Senders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Intent myIntent = this.getIntent();
        currentUser = myIntent.getExtras().getString("current");
        inbox = new ArrayList<Message>();

        myDb = new DatabaseHelper(this);
        Cursor res = myDb.getAllMessages(currentUser);
        if (res.getCount() == 0) {
            Log.v("NO MESSAGE", "true");
        }
        StringBuffer buf = new StringBuffer();

        msg = "";
        int count = 0;
        String messages = "";

        while(res.moveToNext()) {
//            buf.delete(0, buf.length());
            System.out.println("BUFF" + buf);
            buf.append("id :" + res.getString(0) + "\n");
            buf.append("message :" + res.getString(1) + "\n");
            messages = res.getString(1);
        }

        System.out.println("MESSAGES:  "+ messages);
        messages =  messages.substring(1, messages.length()-1);

        for (String m : messages.split(",")) {
            System.out.println("MESSAGE BEFORE SPLIT" + m);
            /*format incomming message */
            for (String retVal : m.split("~")) {
                if (count == 0) {
                    recv = retVal.substring(13, retVal.length());
                } else if (count == 1) {
                    sender = retVal;
                } else if (count == 2) {
                    msg = retVal.substring(0, retVal.length() - 2);
                }
                count++;
            }
            count = 0;
            if (recv.equals(currentUser)) {
                inbox.add(new Message(sender, recv, msg));
            }
//            System.out.println("RECIEVED  " +  recv);
//            System.out.println("SENDER "+sender);
//            System.out.println("MESSAGE AFTER SPLIT " + msg);
        }

        //System.out.println(inbox);

        setListView(inbox);

    }

    public void setListView(ArrayList<Message> inbox){
        ArrayList<String> Senders = new ArrayList<String>();
        for(Message message : inbox){
            System.out.println("SENDER TO BE ADDDED: " + message.getSender());
            Senders.add(message.getSender());
        }
        System.out.println("SENDERS:   " + Senders);

        ArrayAdapter adapter = new ArrayAdapter(Inbox.this, R.layout.adaptor_text_layout, Senders);
        final ListView listView = (ListView) findViewById(R.id.inboxList);
        listView.setAdapter(adapter);

    }
}