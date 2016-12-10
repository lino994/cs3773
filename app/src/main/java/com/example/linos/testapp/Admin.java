package com.example.linos.testapp;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/*
 * Admin Class only accesible with admin username and password
 *
 * Testing:
 *      - Username : admin
 *      - Password : admin
 */

/**Admin Class:
 * Uses Messaging Layout but gives extra options to System Admin
 */
public class Admin extends AppCompatActivity {

    //link to access database
    private static final String LINK = "http://galadriel.cs.utsa.edu/~group5/getContacts.php";
    private int count;
    Button bLogout;
    String uname;
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);

            //initalize the options button
        Toolbar myToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.options);
        setSupportActionBar(myToolbar);

            // grab username of (admin) passed on by previous Intent
        Intent myIn = getIntent();
        uname = myIn.getExtras().getString("uname");

            //function that connects to database to get contact information
        InitContacts(LINK, uname);
    }

    /* retrieve contacts from server */
    public void InitContacts(String LINK, final String uname){
        class ContactASync extends AsyncTask<String, Void, String> {

            private Dialog loadingDiag;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDiag = ProgressDialog.show(Admin.this,"Please Wait","loading..");
            }

            @Override
            /*main processing from database*/
            protected String doInBackground(String... params) {
                String uri = params[0];
                String uname = params[1];

                try{


                    String data = URLEncoder.encode("uname", "UTF-8")           // data to be sent to php file in
                            + "=" + URLEncoder.encode(uname, "UTF-8");          // server to retrieve info from database


                    /*open connection to link*/
                    URL url = new URL(uri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    /*set up writer to php file */
                    connection.setDoOutput(true);
                    OutputStreamWriter osWrite = new OutputStreamWriter(connection.getOutputStream());
                    osWrite.write(data);
                    osWrite.flush();

                    /*set up buffered reader to recieve output from php file */
                    BufferedReader bReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while((line = bReader.readLine()) != null) {
                        Log.v("line from admin: ", line);
                        sb.append(line + "\n");
                    }
                    Log.v("string from admin :", sb.toString());
                    return sb.toString();

                }catch(Exception e){  //connection error
                    Log.v("Conn Error  :", e.getMessage());
                    return new String ("Exception: " + e.getMessage());
                }
            }

            /*after php file has been accessed and executed*/
            @Override
            protected void onPostExecute(String result) {

                loadingDiag.dismiss();                                     //dismiss loading signal
                ArrayList<String> contactList = new ArrayList<String>();   //ArrayList to hold contact information
                Log.v("resultFromJSON:",result);


                try {
                    JSONArray jsonResult = new JSONArray(result);           //turn result string into JSONArray
                    for(int i = 0; i < jsonResult.length(); i++){
                        JSONObject jsonObj = jsonResult.getJSONObject(i);   //turn each item in array in JSONObject
                        contactList.add(jsonObj.getString("user"));         //turn to obj to string and add to ArrayList
                    }
                    Log.v("after List set",contactList.toString());

                }catch (Exception e){
                    Log.v("Error JSON Decoding:", e.getMessage());
                }

                /* Set Up List View */
                ArrayAdapter adapter = new ArrayAdapter(Admin.this, R.layout.adaptor_text_layout, contactList);
                final ListView listView = (ListView) findViewById(R.id.contactList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,int position, long id)
                    {
                        /*store contact user chose */
                        String selectedFromList =(listView.getItemAtPosition(position).toString());

                        /*start activity where user is given an option of choosing encryption options */
                        Intent selectEn = new Intent(Admin.this, SetEncryption.class);
                        selectEn.putExtra("uname", uname);
                        selectEn.putExtra("recv", selectedFromList);
                        startActivity(selectEn);
                        finish();

                        /* String selectedFromList =(listView.getItemAtPosition(position).toString());
                        Log.v("Selected",selectedFromList);
                        Bundle info = new Bundle();
                        info.putString("uname",uname);
                        info.putString("reciever",selectedFromList);
                        Intent newMessage = new Intent(Admin.this, SendMessage.class);
                        newMessage.putExtras(info);
                        startActivity(newMessage);
                        finish();*/
                    }});

            }
        }

        /*execute ContactASync */
        ContactASync cas = new ContactASync();
        cas.execute(LINK , uname);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }
    @Override
    
    public boolean onOptionsItemSelected(MenuItem item) {
        //this.findViewById(android.R.id.message);
        switch (item.getItemId()) {
            case R.id.password:     //change password
                Intent ChangePassIn = new Intent(this, ResetPassword.class);
                ChangePassIn.putExtra("uname",uname);
                startActivityForResult(ChangePassIn, 0);
                Log.v("Selected","password");
                finish();
                return true;

            case R.id.security:     //set Security Question
                Intent setQuest = new Intent(this, SetSecQuestion.class);
                setQuest.putExtra("uname",uname);   //send username to next activity
                setQuest.putExtra("isNew", 0);      //update question not insert new question
                startActivity(setQuest);
                Log.v("Selected","security");
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;
            case R.id.new_user:
                Intent newUser = new Intent(this, Create.class);
                newUser.putExtra("uname",uname);    //send username to next activity
                startActivity(newUser);
                Log.v("Selected","Create new user");
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case R.id.new_message:
                Intent newMessage = new Intent(this, SendMessage.class);
                newMessage.putExtra("uname",uname);
                startActivity(newMessage);
                Log.v("Selected","Send new Message");
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case R.id.pattern:
                Intent PatternEncrypt = new Intent(this, PatternEncrypt.class);
                startActivity(PatternEncrypt);
                return true;

            case R.id.Logout:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
