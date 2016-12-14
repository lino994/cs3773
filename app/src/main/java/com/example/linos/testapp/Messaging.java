package com.example.linos.testapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.*;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * main activity (first screen user see's after logging in
 * it has a contacts list for the user to choose from and
 * a menu of options for the user to access and choose from
 */
public class Messaging extends AppCompatActivity{
    /*link to get a list of contacts */
    //link to be used to connect
    private static final String LINK = "http://galadriel.cs.utsa.edu/~group5/getContacts1.php";
    String uname;           // will store current logged in user's username
    String msg = "";        //msg stored
    String recv = "";       //who recieved msg
    String sender = "";     //who sent msg
    String newMsg = "";     //formatted msg
    Intent checkMessageIntent;
    DatabaseHelper myDb;
    ArrayList<Contact>  contactList;

    @Override


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.options);
        setSupportActionBar(myToolbar);
        //this.deleteDatabase("Random.db");
        myDb = new DatabaseHelper(this);
        contactList = new ArrayList<>();

        //get username from previous activity
        Intent thisIntent = getIntent();
        uname = thisIntent.getExtras().getString("uname");

        checkMessageIntent = new Intent(this,CheckMessage.class);
        checkMessageIntent.putExtra("uname",uname);
        startService(checkMessageIntent);


        InitContacts(LINK, uname);

    }


    /* retrieve contacts from server */
    public void InitContacts(String LINK, final String uname){
            class ContactASync extends AsyncTask<String, Void, String> {

                private Dialog loadingDiag;
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    loadingDiag = ProgressDialog.show(Messaging.this,"Please Wait","loading..");
                }

                @Override
                protected String doInBackground(String... params) {
                    String uri = params[0];
                    String uname = params[1];

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

                    /**
                     * use an array list and  and JSON to decode result
                     * set it to a ListView to be displayed to the user as
                     * their contacts list
                     */
                    Log.v("resultFromJSON:",result);

                    try {
                        JSONArray jsonResult = new JSONArray(result);           //turn result string into JSONArray
                        for(int i = 0; i < jsonResult.length(); i++){
                            JSONObject jsonObj = jsonResult.getJSONObject(i);   //turn each item in array in JSONObject
                            String userName = jsonObj.getString("user");         //turn to obj to string and add to ArrayList
                            String contactName = jsonObj.getString("name");         //turn to obj to string and add to ArrayList

                            Contact contact = new Contact(userName, contactName);
                            contactList.add(contact);
                        }
                        Log.v("after List set",contactList.toString());

                    }catch (Exception e){
                        Log.v("Error JSON Decoding:", e.getMessage());
                    }

                    ContactListAdapter adapter = new ContactListAdapter(Messaging.this, contactList);
                    final ListView listView = (ListView) findViewById(R.id.contactList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view,int position, long id)
                        {
                            /*store contact user chose */
                            Object selectedContact = listView.getItemAtPosition(position);

                            /*start activity where user is given an option of choosing encryption options */
                            Intent selectEn = new Intent(Messaging.this, SetEncryption.class);
                            selectEn.putExtra("uname", uname);
                            selectEn.putExtra("recv", ((Contact) selectedContact).getUserName());
                            startActivity(selectEn);
                            finish();

                        }});

                }
            }
            ContactASync cas = new ContactASync();
            cas.execute(LINK , uname);
        }

    /**
     * Creates Options menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Checks what the user pressed in the options menu and takes them to
     * the activity they chose.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //this.findViewById(android.R.id.message);
        switch (item.getItemId()) {
            case R.id.password:
                Intent ChangePassIn = new Intent(this, ResetPassword.class);
                ChangePassIn.putExtra("uname",uname);
                startActivityForResult(ChangePassIn, 0);
                Log.v("Selected","password");
                finish();
                return true;

            case R.id.security:
                Intent setQuest = new Intent(this, SetSecQuestion.class);
                setQuest.putExtra("uname",uname);
                setQuest.putExtra("isNew",1);
                startActivity(setQuest);
                Log.v("Selected","security");
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case R.id.messages:

                /*send info and start check message activity */
                Intent checkInbox = new Intent(Messaging.this, Inbox.class);
                checkInbox.putExtra("current",uname);
                checkInbox.putExtra("contactList", contactList);
                startActivity(checkInbox);

                return true;

            case R.id.Logout:
                stopService(checkMessageIntent);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void onBackPressed() {

    }
}