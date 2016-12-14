package com.example.linos.testapp;

import android.database.Cursor;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class MessageDisplay extends AppCompatActivity {

    private static int TIME_TO_DELETE = 30;
    private static int SECONDS = 1000;
    Bundle info;
    Intent recentIntent;
    Button replyButton;
    ArrayList<Contact> contactList;
    String uname;
    String recv;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_display);

        recentIntent = this.getIntent();
        info = recentIntent.getExtras();
        TextView fromText = (TextView) findViewById(R.id.fromText);
        fromText.setText("From: " + info.getString("sender"));

        myDb = new DatabaseHelper(this);

        String sender = info.getString("sender");
        String key = "";
        String pattern = "";
        String salt = sender;
        String decryptedMessage = info.getString("messageText");
        boolean read = info.getBoolean("read");
        int messagePos = info.getInt("messagePos");

        if (!read) {


            int encryptMethod = info.getInt("encrypt");

            switch (encryptMethod) {
                case 1:
                    key = info.getString("key");
                    decryptedMessage = decryptedMessage(decryptedMessage, key, salt);
                    break;
                case 2:
                    pattern = info.getString("patternKey");
                    decryptedMessage = decryptedMessage(decryptedMessage, pattern, salt);
                    break;
                case 3:
                    key = info.getString("key");
                    pattern = info.getString("patternKey");

                    decryptedMessage = decryptedMessage(decryptedMessage, pattern, salt);
                    decryptedMessage = decryptedMessage(decryptedMessage, key, salt);
                    break;
                default:
                    break;
            }

            new CountDownTimer(TIME_TO_DELETE * SECONDS, SECONDS) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {

                    int messageNumber = info.getInt("messageNumber");
                    delMessageByMessageNumber(messageNumber);
                    Inbox.messageListAdapter.notifyDataSetChanged();
                    boolean deleted = myDb.deleteMessage(messageNumber);
                    System.out.println("deleted successful = " + deleted);
                }
            }.start();


            // error when decrypted
            if (decryptedMessage == null) {
                Toast.makeText(getApplicationContext(), "DECRYPTION UNSUCCESSFULLY!!!", Toast.LENGTH_SHORT).show();


                int messageNumber = info.getInt("messageNumber");
                Cursor cur2 = myDb.getMessageAt(messageNumber);
                int count2 = cur2.getCount();
                if (count2 > 0) {
                    cur2.moveToFirst();
                    int tries = Integer.parseInt(cur2.getString(8)) + 1;
                    myDb.updateTry(messageNumber, tries);
                    if (tries > 4) {
                        delMessageByMessageNumber(messageNumber);
                        Inbox.messageListAdapter.notifyDataSetChanged();
                        boolean deleted = myDb.deleteMessage(messageNumber);
                        System.out.println("deleted successful = " + deleted);

                    }
                    decryptedMessage = "Decryption was unsuccessful!\n" + (5-tries) + " more attempts left!!!";
                }
            } else {    // decrypt successfully
                int messageNumber = info.getInt("messageNumber");
                myDb.updateMessageText(messageNumber, decryptedMessage);
                myDb.updateEncrypt(messageNumber, 0);
                myDb.updateRead(messageNumber, 1);

                updateMessage(messageNumber, decryptedMessage, 1, 0);
                Toast.makeText(getApplicationContext(), "DECRYPTION SUCCESSFULLY!!!", Toast.LENGTH_SHORT).show();

            }
        } else {

        }

        TextView messageText = (TextView) findViewById(R.id.messageTextDisplay);
        replyButton = (Button) findViewById(R.id.replyButton);
        messageText.setText(decryptedMessage);

        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    /*start activity where user is given an option of choosing encryption options */
                    Intent selectEn = new Intent(MessageDisplay.this, SetEncryption.class);
                    uname = info.getString("recv");
                    recv = info.getString("sender");
                    selectEn.putExtra("uname", uname);
                    selectEn.putExtra("recv", recv);
                    startActivity(selectEn);
                    finish();

            }
        });
    }

    protected void onPostExceuted() {

    }
    private String decryptedMessage(String message, String password, String salt) {
        String decryptedMessage = "";

        Encryption decryptor = Encryption.getDefault(password, salt, new byte[16]);
        decryptedMessage = decryptor.decryptOrNull(message);

        return decryptedMessage;
    }

    public synchronized void delMessageByMessageNumber(int messageNumber) {
        synchronized (Inbox.inbox) {
            Iterator<Message> ite = Inbox.inbox.iterator();
            while (ite.hasNext()) {
                Message message = ite.next();
                if (message.getMessageNumber() == messageNumber) {
                    ite.remove();
                }
            }

        }
    }

    public synchronized void updateMessage(int messageNumber, String messageText, int read, int encrypt) {
        synchronized (Inbox.inbox) {
            Iterator<Message> ite = Inbox.inbox.iterator();
            while (ite.hasNext()) {
                Message message = ite.next();
                if (message.getMessageNumber() == messageNumber) {
                    message.setEncryptMethod(encrypt);
                    message.setRead();
                    message.setMessageText(messageText);
                }
            }
            Inbox.messageListAdapter.notifyDataSetChanged();
        }
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
