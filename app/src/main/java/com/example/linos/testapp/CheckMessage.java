package com.example.linos.testapp;

import android.app.Dialog;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;


import java.util.Date;
import java.text.DateFormat;


public class CheckMessage extends Service {

    private static final String LINK = "http://galadriel.cs.utsa.edu/~group5/getMessage1.php";
    private static final String DATABASE_NAME = "random.db";

    DatabaseHelper myDb;
    String uname;
    boolean running = false;

    public void onCreate() {
        running = true;
        this.deleteDatabase(DATABASE_NAME);
        myDb = new DatabaseHelper(this);

    }


    public IBinder onBind(Intent intent) {
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        uname = intent.getExtras().getString("uname");
        new Thread(new Runnable() {
            public void run() {
                if (running) {
                    while (running) {
                            try {
                                checkMessage(uname, LINK);
                                Thread.sleep(5000);

                            } catch (Exception e) {
                            }

                    }

                }

            }
        }).start();

        return START_STICKY;
    }

    public void onDestroy(){
        //super.stopSelf();
        //check.interrupt();
        running = false;
        super.onDestroy();

        Log.i("Service:","Destroyed");
    }


    private void checkMessage(String uname, String uri) {
        try{

            String data = URLEncoder.encode("uname", "UTF-8")
                    + "=" + URLEncoder.encode(uname, "UTF-8");


            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            OutputStreamWriter osWrite = new OutputStreamWriter(connection.getOutputStream());
            osWrite.write(data);
            osWrite.flush();

            BufferedReader bReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = bReader.readLine()) != null) {
                this.messagesExtract(line);
                Log.v("check line: ", line);
            }
            connection.disconnect();
            Log.v("string ", sb.toString());

        }catch(Exception e){
            Log.v("Conn Error  :", e.getMessage());
        }
    }
    private void messagesExtract(String input) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");


        try {
            JSONArray jsonArray = new JSONArray(input);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject job = jsonArray.getJSONObject(i);
                String msg = job.getString("messages");
                String recv = job.getString("recv");
                String sender = job.getString("sender");
                String number = job.getString("messageNumber");
                String encrypt = job.getString("encrypt");
                Date recvTime = new Date();
                String timeString = dateFormat.format(recvTime);

                int messageNumber = Integer.parseInt(number);
                int encryptMethod = Integer.parseInt(encrypt);

                myDb.insertData(msg, sender, timeString, messageNumber, recv, 0, encryptMethod);
                Message newMessage = new Message(sender, recv, msg, messageNumber, encryptMethod);
                newMessage.setTime(recvTime);
            }

        } catch (Exception e){
            Log.v("check JSON Decoding:", e.getMessage());
        }
    }

}
