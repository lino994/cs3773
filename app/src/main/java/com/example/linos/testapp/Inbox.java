package com.example.linos.testapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.CountDownTimer;
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


public class Inbox extends AppCompatActivity {
    public static ArrayList<Message> inbox;
    String currentUser;
    DatabaseHelper myDb;
    public static MessageListAdapter messageListAdapter;
    ArrayList<Contact> contactList;
    int tries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inbox);
        Intent myIntent = this.getIntent();
        currentUser = myIntent.getExtras().getString("current");
        inbox = new ArrayList<>();
        myDb = new DatabaseHelper(this);

        contactList = (ArrayList<Contact>) myIntent.getSerializableExtra("contactList");

        inbox = inboxGenerated(myDb, currentUser, contactList);

        setListView(inbox);

    }

    public ArrayList<Message> inboxGenerated(DatabaseHelper myDb, String currentUser, ArrayList<Contact> contactList) {
        ArrayList<Message> inbox = new ArrayList<>();
        Cursor res = myDb.getAllMessages(currentUser);
        String recvTime= null;

        StringBuffer buf = new StringBuffer();

        res.moveToFirst();
        int count = res.getCount();
        if (count > 0) {
            do {
                String messageText = res.getString(1);
                String sender = res.getString(2);
                String recv = res.getString(5);
                int messageNumber = res.getInt(4);
                recvTime = res.getString(3);

                Date receiveTime = this.getTime(recvTime);
                Date current = new Date();

                if (current.getTime() - receiveTime.getTime() > 300000) {
                    myDb.deleteMessage(messageNumber);

                    break;
                }
                int read = res.getInt(6);
                int encryptMethod = res.getInt(7);
                tries = res.getInt(8);
                if (recv.equals(currentUser)) {
                    Message newMessage = new Message(sender, recv, messageText, messageNumber, encryptMethod);
                    newMessage.setTimeString(recvTime);
                    Contact contact = findContactByUsername(sender, contactList);
                    newMessage.setSenderName(contact.getName());
                    if (read == 1) {
                        newMessage.setRead();
                    }
                    inbox.add(newMessage);
                }
            } while (res.moveToNext());
            myDb.close();
        }
        return inbox;
    }


    public void setListView(ArrayList<Message> inbox){
        messageListAdapter = new MessageListAdapter(Inbox.this, inbox);
        final ListView listView = (ListView) findViewById(R.id.inboxList);
        listView.setAdapter(messageListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {
                            /*store contact user chose */
                Object selected = listView.getItemAtPosition(position);
                Message messageSelected = (Message) selected;
                if (messageSelected != null) {
                    readMessage(messageSelected, position);
                }


                messageListAdapter.notifyDataSetChanged();
//                Toast.makeText(getApplicationContext(), "encrypt = " + messageSelected.getEncryptMethod(), Toast.LENGTH_SHORT).show();


            }});
    }

    private Contact findContactByUsername(String userName, ArrayList<Contact> contactList) {
        Contact contact = null;

        for (Contact c : contactList) {
            if (c.getUserName().equals(userName)) {
                contact = c;
                break;
            }
        }
        return contact;
    }

    private void readMessage(Message messageSelected, int position) {
        int messageNumber = messageSelected.getMessageNumber();

        /* update read time and set the message to be read */
        if (!messageSelected.read()) {
            Date current = new Date();

            //check time out
            Cursor cur = myDb.getMessageAt(messageNumber);
            int count = cur.getCount();
            if (count > 0) {
                cur.moveToFirst();
                String timeString = cur.getString(3);
                Date timeRecv = this.getTime(timeString);
                Date currentTime = new Date();

                if (currentTime.getTime() - timeRecv.getTime() >= 60000) {
                    inbox.remove(messageSelected);
                    messageListAdapter.notifyDataSetChanged();
                    boolean deleted = myDb.deleteMessage(messageNumber);
                    System.out.println("deleted successful = " + deleted);
                    Toast.makeText(getApplicationContext(), "TIME-OUT! Message has been deleted!", Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            String timeString = df.format(current);
            messageSelected.setTimeString(timeString);
            myDb.updateTime(messageNumber, timeString);
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
        bundle.putSerializable("contactList", contactList);
        bundle.putInt("messagePos", position);
        bundle.putInt("tries", tries);
        selectEn.putExtras(bundle);


        startActivity(selectEn);
    }

    public Date getTime(String timeString) {
        Date time = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            time = dateFormat.parse(timeString);
        } catch (Exception e) {
            System.out.println("Time conversion error " + e.getMessage());
        }
        return time;
    }
}