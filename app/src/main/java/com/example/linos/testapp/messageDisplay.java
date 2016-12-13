package com.example.linos.testapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;


public class MessageDisplay extends AppCompatActivity {
    Bundle info;
    Intent recentIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_display);

        recentIntent = this.getIntent();
        info = recentIntent.getExtras();
        TextView fromText = (TextView) findViewById(R.id.fromText);
        fromText.setText("From: " + info.getString("sender"));

        String sender = info.getString("sender");

        String key = "";
        String pattern = "";
        String salt = sender;
        String decryptedMessage = info.getString("messageText");

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
            default: break;
        }


        TextView messageText = (TextView) findViewById(R.id.messageTextDisplay);

        messageText.setText(decryptedMessage);

    }

    private String decryptedMessage(String message, String password, String salt) {
        String decryptedMessage = "";

        Encryption decryptor = Encryption.getDefault(password, salt, new byte[16]);
        decryptedMessage = decryptor.decryptOrNull(message);

        return decryptedMessage;
    }


}
