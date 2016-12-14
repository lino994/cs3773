package com.example.linos.testapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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

/**
 * Main Login Activity here the user can input
 * their username and password
 * or choose that they forget their password
 */
public class MainActivity extends AppCompatActivity {
    private static final String LINK = "http://galadriel.cs.utsa.edu/~group5/testConnect.php";
    private static final String COUNT = "LogCount";     //used for lockout
    private static final String TIME = "Time";          //used for lockout

    Button bForgot;         // forgot button password
    EditText medit;         // where username is entered
    EditText epass;         // where password is entered
    String uname;           // where username will be stored
    String pass;            // where password will be stored

    /*init values for lockout */
    int logCounter = 0;
    long time = 0;
    int MAXTIME = 30000;
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
                Intent forgotIntent = new Intent(v.getContext(), ForgotPassword.class);
                startActivity(forgotIntent);
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

/* used if used presses login */
    public void Login(View view) {
        uname=medit.getText().toString();
        pass=epass.getText().toString();
        if(uname.equals("") || pass.equals("")){
            Toast.makeText(getApplicationContext(), "Field Empty", Toast.LENGTH_SHORT).show();
        }else{
            callLogin(uname, pass, view);
        }
    }
/**
 * main func that uses an ASync class to connect to the server
 * and check if user inputted the correct information
 */
    public void callLogin(final String uname, String pass, final View view){
        class LoginASync extends AsyncTask <String, Void, String>{

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
                Log.v("logCount", count);
                logCounter = Integer.parseInt(count);

                String l = pref.getString(uname+TIME, "0");
                time = System.currentTimeMillis() - Long.parseLong(l);

                if (time > 30000) {
                    editor.putString(uname+COUNT, "0");
                    logCounter = 0;
                    long currentTime = System.currentTimeMillis();
                    editor.putString(uname+TIME, Long.toString(currentTime));
                    editor.commit();
                }

                try{
                    /******************TEST USERS****************************
                        Username : UserOne
                        Password : P@55word!
                     ****************************************************/

                    //if user has tried 3 times to login and fails he is locked out for a certain amount of time
                    if ((logCounter > 2) && (time < MAXTIME)) {
                        return "failure\n";

                    } else {
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
                        return sb.toString();
                    }

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
                /**
                 * different login scenarios:
                 *          - firstTimeSuccess : user logs in for first time is sent to set Security Password
                 *          - Success          : user is sent to main messaging/contact activity
                 *          - failure          : messsage is displayed saying password is incorrect counter of tries is increased
                 * */
                if(r.equals("firstTimeSuccess")){
                    editor.putString(uname+COUNT, "0");
                    long currentTime = System.currentTimeMillis();
                    editor.putString(uname+TIME, Long.toString(currentTime));
                    editor.commit();

                    Intent secIntent = new Intent(view.getContext(), SetSecQuestion.class);
                    secIntent.putExtra("uname",uname);
                    secIntent.putExtra("isNew",0);
                    startActivityForResult(secIntent, 0);
                    finish();

                } else if(r.equals("success")){
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

                    if ((logCounter > 2) && (time < MAXTIME)) {

                        Toast.makeText(getApplicationContext(), "Maximum attempted reached!\nTry again in 30s", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "incorrect username/password ", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v("Result Error", result);
                }

            }

        }
        /**
         * execute login async to connect to database
         */
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

    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("no", null).show();
    }
}
