package com.example.linos.testapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity {
    private static final String LINK = "http://galadriel.cs.utsa.edu/~group5/testConnect.php";
    private static final String COUNT = "LogCount";
    private static final String TIME = "Time";

    Button bForgot;
    EditText medit;
    EditText epass;
    String uname;
    String pass;

    int logCounter = 0;
    long time = 0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        medit = (EditText) findViewById(R.id.edUsername);
        epass = (EditText) findViewById(R.id.edPassword);
        bForgot = (Button) findViewById(R.id.bForgot) ;

        pref = this.getSharedPreferences("LoginTrack", Context.MODE_PRIVATE);
        editor = pref.edit();

        bForgot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(v.getContext(), ForgotPassword.class);
                startActivity(intent);
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public void Login(View view) {
        uname=medit.getText().toString();
        pass=epass.getText().toString();
        if(uname.equals("") || pass.equals("")){
            Toast.makeText(getApplicationContext(), "Field Empty", Toast.LENGTH_SHORT).show();
        }else{
            callLogin(uname, pass, view);
        }
    }

    public void callLogin(final String uname, String pass, final View view){
        class LoginASync extends AsyncTask <String, Void, String>{
            //uname=medit.getText().toString();
            //String pass=epass.getText().toString();

            //Log.v("Username",uname);
            //Log.v("Password",pass);
            private Dialog loadingDiag;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDiag = ProgressDialog.show(MainActivity.this,"Please Wait","loading..");
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                String uname = params[1];
                String pass = params[2];


                String count = pref.getString(uname+COUNT, "0");
                logCounter = Integer.parseInt(count);

                String l = pref.getString(uname+TIME, "0");
                time = System.currentTimeMillis() - Long.parseLong(l);

                try{
                    /******************TEST USERS****************************
                        Username : UserOne
                        Password : p@ss1

                        Username : UserTwo
                        Password : p@ss2

                     ****************************************************/
                    if ((logCounter >= 4) && (time < 30000)) {
                        return "failure\n";
                    }
                    else {
                    String data = URLEncoder.encode("uname", "UTF-8")
                            + "=" + URLEncoder.encode(uname, "UTF-8");
                    data += "&" + URLEncoder.encode("pass", "UTF-8")
                            + "=" + URLEncoder.encode(pass, "UTF-8");
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
                        return sb.toString(); }

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
                if(r.equals("success")){

                    editor.putString(uname+COUNT, "0");
                    long currentTime = System.currentTimeMillis();
                    editor.putString(uname+TIME, Long.toString(currentTime));
                    editor.commit();

                    if(uname.equals("admin")){
                        Intent myIntent = new Intent(view.getContext(), Admin.class);
                        myIntent.putExtra("uname",uname);
                        startActivityForResult(myIntent, 0);
                        finish();
                    }
                    else {
                        Intent myIntent = new Intent(view.getContext(), Messaging.class);
                        myIntent.putExtra("uname",uname);
                        startActivityForResult(myIntent, 0);
                        finish();
                    }
                }else if (r.equals("failure")) {
                    //Toast.makeText(getApplicationContext(), "incorrect username/password", Toast.LENGTH_SHORT).show();
                    logCounter++;
                    editor.putString(uname+COUNT, Integer.toString(logCounter));
                    long currentTime = System.currentTimeMillis();
                    editor.putString(uname+TIME, Long.toString(currentTime));
                    editor.commit();

                    if ((logCounter > 4) && (time < 30000)) {

                        Toast.makeText(getApplicationContext(), "Maximum attempted reached!\nTry again in 30s", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "incorrect username/password ", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v("Result Error", result);
                }

            }

        }

        LoginASync las = new LoginASync();
        las.execute(LINK ,uname, pass);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
