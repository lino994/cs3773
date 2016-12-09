package com.example.linos.testapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity to create new user
 * only available to System Admin
 */
public class Create extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
        //link to php file to update database
    private static final String LINK = "http://galadriel.cs.utsa.edu/~group5/setNewUser.php";
    private int count;
    Button bCreateNew;       // button tosubmit and create new user
    EditText edaname;       // where name will be inputted
    EditText eduname;       // where username will be inputted
    String uName;           // username will be stored
    String aName;           // actual name will be stored
    String newPassword;     // where RNG Password will be stored

    /*when activity starts*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user);

        /**
         * initalize variables to be used
         */
        edaname = (EditText) findViewById(R.id.edActualname);
        eduname = (EditText) findViewById(R.id.edUsername);
        bCreateNew = (Button) findViewById(R.id.createnew);
        bCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                aName = edaname.getText().toString();
                uName=eduname.getText().toString();
                PasswordGenerator pg = new PasswordGenerator();         //new password generator

                newPassword = pg.GenerateRandomString(8,15,1,1,1,1); // generate new password and set it to newPassword
                String pass = newPassword;

                /**
                 * check if any or the fields are empty to prevent
                 * null values being inputed to server table
                 */
                if(uName.equals("") || aName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Field Empty", Toast.LENGTH_SHORT).show();
                }else {
                    callCreate(uName, aName, pass, v);
                    Toast.makeText(getApplicationContext(), "Created User", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        count = count +1;
        // On selecting a spinner item
        if (count >1) {
            String item = parent.getItemAtPosition(position).toString();
            Log.v("Selected", item);
            // Showing selected spinner item
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }


    /**
     * Main function that connects to database to input a new user into
     * the userTable in the database by creating and executing createASync class
     *
     * @param uname     - username
     * @param aname     - full name
     * @param pass      - password
     * @param view      - view
     */
    public void callCreate(final String uname,final String aname,final String pass, final View view){
        class createASync extends AsyncTask<String, Void, String> {

            //Log.v("Username",uname);
            //Log.v("Password",pass);
            private Dialog loadingDiag;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDiag = ProgressDialog.show(Create.this,"Please Wait","loading..");
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                String uname = params[1];
                String aname =params[3];
                String password = params[2];

                try{
                    Log.v("encode","en");
                    String data = URLEncoder.encode("uname", "UTF-8")
                            + "=" + URLEncoder.encode(uname, "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8")
                            + "=" + URLEncoder.encode(password, "UTF-8");
                    data += "&" + URLEncoder.encode("aname", "UTF-8")
                            + "=" + URLEncoder.encode(aname, "UTF-8");

                    //Log.v("encode","en");

                    URL url = new URL(uri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    Log.v("Connected","success");
                    connection.setDoOutput(true);
                    OutputStreamWriter osWrite = new OutputStreamWriter(connection.getOutputStream());
                    Log.v("Write","success");
                    osWrite.write(data);
                    Log.v("Write","success");
                    osWrite.flush();

                    BufferedReader bReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while((line = bReader.readLine()) != null) {
                       // Log.v("line: ", line);
                        sb.append(line + "\n");
                    }
                    Log.v("string from create:", sb.toString());
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

                /*Displays New Password to be given to the user to login for teh first time */
                TextView newPasswordView = (TextView) findViewById(R.id.newPassGen);
                TextView PasswordTagView = (TextView) findViewById(R.id.passTag);
                Button bOK = (Button) findViewById(R.id.bOK);

                PasswordTagView.setVisibility(TextView.VISIBLE);
                newPasswordView.setText(newPassword);
                newPasswordView.setVisibility(TextView.VISIBLE);
                /* When OK Button is pressed
                 * app goes back to main page for admin
                */
                bOK.setVisibility(Button.VISIBLE);

                bOK.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent myIntent = new Intent(v.getContext(), Admin.class);
                        myIntent.putExtra("uname",uName);
                        startActivityForResult(myIntent, 0);
                        finish();
                    }
                });
            }

        }

        /*execute ASync class */
        createASync cas = new createASync();
        cas.execute(LINK ,uname,pass, aname);
    }
}
