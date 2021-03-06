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
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * Created by nique on 12/8/2016.
 */

public class SendMessage extends AppCompatActivity{
    private static final String LINK = "http://galadriel.cs.utsa.edu/~group5/newMessage1.php";
    Button bSubmit;                 //button to send msg
    Boolean needsBoth;              //needs both encryptions
    Boolean needsPattern;           //needs pattern encryption
    Boolean needsKey;               //needs key encryption
    Bundle info;                    //hold info needed to send message and check options chosen
    EditText edMessage;             //message edittext
    String recv;                   //uname of user to be sent to
    String sender;                  //current username
    String key;                     //key to encrypt
    String patternKey;              //key from pattern chosen
    String salt = "";               //salt for encryption (delicious)
    String encryptedMessage = "";   //message to be sent
    String encryptMethod = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        Intent ResetIntent  = getIntent();

        /*grab nessecary info from previous activity */
        info = ResetIntent.getExtras();
        sender = info.getString("uname");
        recv = info.getString("reciever");
        needsKey = info.getBoolean("needsKey");
        needsBoth = info.getBoolean("needsBoth");
        needsPattern = info.getBoolean("needsPattern");

        /*where user enters message*/
        edMessage = (EditText) findViewById(R.id.edMessage);
        TextView recvTV = (TextView)  findViewById(R.id.toMessage);
        recvTV.setText("To: " + recv);
        /*initalize button to send */
        bSubmit = (Button) findViewById(R.id.send);

        bSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                salt = sender;
                String msg1 = edMessage.getText().toString();
                if (msg1.equals("")) {
                    Toast.makeText(getApplicationContext(), "Field Empty", Toast.LENGTH_SHORT).show();

                } else {
                    //user only wants key encryption
                    if (needsKey && !needsBoth) {
                        key = info.getString("key");
                        Encryption encryption = Encryption.getDefault(key, salt, new byte[16]);
                        String msg = edMessage.getText().toString();
                        encryptedMessage = encryption.encryptOrNull(msg);
                        encryptMethod = "1";
                    }
                    //user only wants pattern encryption
                    else if (needsPattern && !needsBoth) {  //message does not need key encryption
                        key = info.getString("patternKey");

                        Encryption encryption = Encryption.getDefault(key, salt, new byte[16]);
                        String msg = edMessage.getText().toString();
                        encryptedMessage = encryption.encryptOrNull(msg);
                        encryptMethod = "2";
                    }
                    //user wants both encryptions
                    else if (needsBoth) {
                        key = info.getString("key");

                        patternKey = info.getString("patternKey");

                        Encryption encryption = Encryption.getDefault(key, salt, new byte[16]);
                        String msg = edMessage.getText().toString();
                        encryptedMessage = encryption.encryptOrNull(msg);

                        Encryption encryption2 = Encryption.getDefault(patternKey, salt, new byte[16]);
                        encryptedMessage = encryption2.encryptOrNull(encryptedMessage);

                        encryptMethod = "3";
                    }
                    //user want no encryption
                    else {
                        encryptedMessage = edMessage.getText().toString();
                        encryptMethod = "0";
                    }


                    //message to be sent
                    String message = encryptedMessage;
                    Log.v("Message: ", message);
                    initSubmit(v, recv, sender, message);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {

        if(sender.equals("admin")){
            Intent myIntent = new Intent(this, Admin.class);
            myIntent.putExtra("uname",sender);
            startActivityForResult(myIntent, 0);
            finish();
        }
        else{
            Intent myIntent = new Intent(this, Messaging.class);
            myIntent.putExtra("uname",sender);
            startActivityForResult(myIntent, 0);
            finish();
        }


    }


    public void initSubmit(final View v, final String recv, final String sender, final String message) {
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
                String timeString = "";
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = new Date();
                timeString = dateFormat.format(date);
                try{
                    /******************TEST USERS****************************
                     Username : UserOne
                     Password : p@ss1

                     Username : UserTwo
                     Password : p@ss2

                     ****************************************************/
                    String data = URLEncoder.encode("uname", "UTF-8")
                            + "=" + URLEncoder.encode(recv, "UTF-8");
                    data += "&" + URLEncoder.encode("message", "UTF-8")
                            + "=" + URLEncoder.encode(message, "UTF-8");
                    data += "&" + URLEncoder.encode("sender", "UTF-8")
                            + "=" + URLEncoder.encode(sender, "UTF-8");
                    data += "&" + URLEncoder.encode("encrypt", "UTF-8")
                            + "=" + URLEncoder.encode(encryptMethod, "UTF-8");
                    data += "&" + URLEncoder.encode("time", "UTF-8")
                            + "=" + URLEncoder.encode(timeString, "UTF-8");
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
        qas.execute(LINK , recv, message);
    }
}
