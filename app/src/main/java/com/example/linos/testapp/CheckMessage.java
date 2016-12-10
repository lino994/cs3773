package com.example.linos.testapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CheckMessage extends Service {
    private static final String LINK = "http://galadriel.cs.utsa.edu/~group5/getMessage.php";
    static ArrayList<String> msgs = new ArrayList<String>();
    DatabaseHelper mydb;



    public int onStartCommand(Intent intent,int flags,int startId){
        final String LINK = "http://galadriel.cs.utsa.edu/~group5/getMessage.php";
        final String uname;
        Intent thisIntent = intent;
        uname = thisIntent.getExtras().getString("uname");
        mydb = new DatabaseHelper(this);

        Log.v("user",uname);
        Log.v("link",LINK);
        Log.i("Service:","Started");

        Runnable r = new Runnable() {
            @Override
            public void run() {
                while(uname != ""){
                    Log.v("UNAME",uname);
                    long future = System.currentTimeMillis() + 5000;
                    while(System.currentTimeMillis() < future){
                        synchronized (this) {
                            try {
                                wait(future - System.currentTimeMillis());
                                CheckMsg(LINK, uname);
                                Log.v("Service", "executing at" + System.currentTimeMillis());
                            } catch (Exception e) {}
                        }
                    }
                }
            }
        };
        Thread check = new Thread(r);
        check.start();
        return Service.START_STICKY;
    }

    public void onDestroy(){
        super.onDestroy();
        Log.i("Service:","Destroyed");
    }

    public void CheckMsg(String LINK, final String uname){
        class CheckMsgASync extends AsyncTask<String, Void, String> {

            private Dialog loadingDiag;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                String uname = params[1];
                Log.v("do in back uname",uname);

                try{

                    //data to be sent
                    String data = URLEncoder.encode("uname", "UTF-8")
                            + "=" + URLEncoder.encode(uname, "UTF-8");
                    //Log.v("user: ", uname

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
                        //Log.v("check line: ", line);
                        sb.append(line + "\n");
                        boolean insert = mydb.insertData(line);
                        if(insert){
                            Log.v("updated database","good");
                        }
                    }
                    Log.v("string ", sb.toString());
                    return sb.toString();

                }catch(Exception e){
                    Log.v("Conn Error  :", e.getMessage());
                    return new String ("Exception: " + e.getMessage());
                }
            }
            @Override
            protected void onPostExecute(String result) {

                /**
                 * use an array list and  and JSON to decode result
                 * set it to a ListView to be displayed to the user as
                 * their contacts list
                 */
                //ArrayList<String> messages = new ArrayList<String>();
                //Log.v("check resultFromJSON:",result);
                try {
                    JSONArray jsonResult = new JSONArray(result);
                    for(int i = 0; i < jsonResult.length(); i++){
                        JSONObject jsonObj = jsonResult.getJSONObject(i);
                        msgs.add(jsonObj.getString("messages"));
                    }
                    Log.v("check after List set",msgs.toString());


                }catch (Exception e){
                    Log.v("check JSON Decoding:", e.getMessage());
                }



            }
        }
        CheckMsgASync cmsg = new CheckMsgASync();
        cmsg.execute(LINK , uname);
    }

    public static ArrayList<String> getMsgs(){
        ArrayList<String> copy = msgs;
        return copy;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
