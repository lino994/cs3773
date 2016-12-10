package com.example.linos.testapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 *
 * Created by nique on 12/8/2016.
 */

public class SendMessage extends AppCompatActivity{
    private static final String LINK = "http://galadriel.cs.utsa.edu/~group5/newMessage.php";
    Button bSubmit;
    EditText edMessage;
    EditText edPass2;
    String uname;
    String sender;
    String key = "123456";
    String key2 = "773737";
    String salt = "";
    String encryptedMessage = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        Intent ResetIntent  = getIntent();
        Bundle info = ResetIntent.getExtras();
        sender = info.getString("uname");
        uname = info.getString("reciever");
        edMessage = (EditText) findViewById(R.id.edMessage);



        bSubmit = (Button) findViewById(R.id.send);

        bSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                salt = sender;
                Encryption encryption = Encryption.getDefault(key, salt, new byte[16]);
                Encryption encryption2 = Encryption.getDefault(key2, salt, new byte[16]);

                String msg = edMessage.getText().toString();
                System.out.println("message " + msg);
                encryptedMessage = encryption.encryptOrNull(msg);
                encryptedMessage = encryption2.encryptOrNull(encryptedMessage);
                System.out.println("encrypted message = " + encryptedMessage);

                Encryption decryption = Encryption.getDefault(key2, salt, new byte[16]);
                Encryption decryption2 = Encryption.getDefault(key, salt, new byte[16]);

                String decryptedMessage1 = decryption.decryptOrNull(encryptedMessage);
                String decryptedMesssage = decryption2.decryptOrNull(decryptedMessage1);
                if (decryptedMesssage == null) {
                    System.out.println("Can't decrypt the message");
                } else {
                    System.out.println("decrypted message = " + decryptedMesssage);
                }

                String message = "Sent from user: " + sender +"\n"+ encryptedMessage;
                Log.v("Message: ", message);
                initSubmit(v, uname, message);

            }
        });
    }

    @Override
    public void onBackPressed(){

        if(sender.equals("admin")){
            //Intent myIntent = new Intent(this, Admin.class);
            //myIntent.putExtra("uname",sender);
            //startActivityForResult(myIntent, 0);
            finish();
        }
        else{
            Intent myIntent = new Intent(this, Messaging.class);
            myIntent.putExtra("uname",sender);
            startActivityForResult(myIntent, 0);
            finish();
        }


    }


    public void initSubmit(final View v, final String uname, final String message) {
        class QuestionASync extends AsyncTask<String, Void, String> {

            private Dialog loadingDiag;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDiag = ProgressDialog.show(SendMessage.this,"Please Wait","loading..");
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                String uname = params[1];
                String message = params[2];

                try{
                    /******************TEST USERS****************************
                     Username : UserOne
                     Password : p@ss1

                     Username : UserTwo
                     Password : p@ss2

                     ****************************************************/
                    String data = URLEncoder.encode("uname", "UTF-8")
                            + "=" + URLEncoder.encode(uname, "UTF-8");
                    data += "&" + URLEncoder.encode("message", "UTF-8")
                            + "=" + URLEncoder.encode(message, "UTF-8");

                    //Log.v("user: ", uname);
                    //Log.v("answer", answer);

                    URL url = new URL(uri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setDoOutput(true);
                    OutputStreamWriter osWrite = new OutputStreamWriter(connection.getOutputStream());
                    osWrite.write(data);
                    osWrite.flush();

                    BufferedReader bReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while((line = bReader.readLine()) != null) {
                        Log.v("line: ", line);
                        sb.append(line + "\n");
                    }
                    Log.v("string :", sb.toString());
                    return sb.toString();

                }catch(Exception e){
                    Log.v("Conn Error  :", e.getMessage());
                    return new String ("Exception: " + e.getMessage());
                }
            }
            @Override
            protected void onPostExecute(String result) {
                loadingDiag.dismiss();
                String r = result.trim();
                Log.v("result: ", r);
                if (sender.equals("admin")) {
                    Intent myIntent = new Intent(SendMessage.this, Admin.class);
                    myIntent.putExtra("uname",sender);
                    startActivityForResult(myIntent, 0);
                    finish();
                }
                else {
                    Intent myIntent = new Intent(SendMessage.this, Messaging.class);
                    myIntent.putExtra("uname",sender);
                    startActivityForResult(myIntent, 0);
                    finish();
                }
            }

        }
        QuestionASync qas = new QuestionASync();
        qas.execute(LINK , uname, message);
    }
}
