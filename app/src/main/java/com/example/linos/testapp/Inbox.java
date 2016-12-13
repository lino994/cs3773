package com.example.linos.testapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class Inbox extends AppCompatActivity {
    ArrayList<Message> inbox;
    String currentUser;
    String recv;
    String sender;
    String msg;
    DatabaseHelper myDb;
    MessageListAdapter adapter;
    ArrayList<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inbox);
        Intent myIntent = this.getIntent();
        currentUser = myIntent.getExtras().getString("current");
        inbox = new ArrayList<Message>();

        contactList = (ArrayList<Contact>) myIntent.getSerializableExtra("contactList");

        myDb = new DatabaseHelper(this);
        Cursor res = myDb.getAllMessages(currentUser);
        String recvTime= null;

        StringBuffer buf = new StringBuffer();
        msg = "";

        res.moveToFirst();
        int count = res.getCount();
        if (count > 0) {
            do {
                String messageText = res.getString(1);
                sender = res.getString(2);
                recv = res.getString(5);
                int messageNumber = res.getInt(4);
                recvTime = res.getString(3);
                int read = res.getInt(6);
                int encryptMethod = res.getInt(7);
                if (recv.equals(currentUser)) {
                    Message newMessage = new Message(sender, recv, messageText, messageNumber, encryptMethod);
                    newMessage.setTimeString(recvTime);
                    Contact contact = findContactByUsername(sender);
                    newMessage.setSenderName(contact.getName());
                    if (read == 1) {
                        newMessage.setRead();
                    }
                    inbox.add(newMessage);
                }
            } while (res.moveToNext());
        }

        setListView(inbox);

    }

    public void setListView(ArrayList<Message> inbox){

        adapter = new MessageListAdapter(Inbox.this, inbox);
        final ListView listView = (ListView) findViewById(R.id.inboxList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {
                            /*store contact user chose */
                Object selected = listView.getItemAtPosition(position);
                Message messageSelected = (Message) selected;
                if (messageSelected != null) {
                    readMessage(messageSelected);
                }


                adapter.notifyDataSetChanged();
//                Toast.makeText(getApplicationContext(), "encrypt = " + messageSelected.getEncryptMethod(), Toast.LENGTH_SHORT).show();


            }});
    }

    private Contact findContactByUsername(String userName) {
        Contact contact = null;

        for (Contact c : contactList) {
            if (c.getUserName().equals(userName)) {
                contact = c;
                break;
            }
        }
        return contact;
    }

    private void readMessage(Message messageSelected) {
        int messageNumber = messageSelected.getMessageNumber();

        /* update read time and set the message to be read */
        myDb.updateRead(messageNumber, 1);
        if (!messageSelected.read()) {
            Date current = new Date();
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            String timeString = df.format(current);
            myDb.updateTime(messageNumber, timeString);
            messageSelected.readMessage();
        }

         /*start activity where user is given an option of choosing encryption options */
        Class destinationClass = MessageDisplay.class;
        boolean needsBoth = false;

        switch (messageSelected.getEncryptMethod()) {
            case 1:
                destinationClass = EnterKey.class;
                break;
            case 2:
                destinationClass = PatternEncrypt.class;
                break;
            case 3:
                destinationClass = PatternEncrypt.class;
                needsBoth = true;
                break;
            default: break;
        }

        Intent selectEn = new Intent(Inbox.this, destinationClass);
        Bundle bundle = messageSelected.getMessageBundle();
        bundle.putString("action", "readMessage");
        bundle.putBoolean("needsBoth", needsBoth);
        selectEn.putExtras(bundle);


        startActivity(selectEn);
    }
}