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


public class Create extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String LINK = "http://galadriel.cs.utsa.edu/~group5/setNewUser.php";
    private GoogleApiClient client;
    private int count;
    Button createNew;
    EditText edaname;
    EditText eduname;
    EditText edanswer;
    String uName;
    String sQuestion;
    String aName;
    String sAnswer;
    String newPassword;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user);
        edaname = (EditText) findViewById(R.id.edActualname);
        eduname = (EditText) findViewById(R.id.edUsername);
        edanswer = (EditText) findViewById(R.id.edAnswer);
        createNew = (Button) findViewById(R.id.createnew);
        createNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aName = edaname.getText().toString();
                uName=eduname.getText().toString();
               // sAnswer=edanswer.getText().toString();
                String quest = "what";
                PasswordGenerator pg = new PasswordGenerator();

                /*
                GenerateRandomString(int minLength, int maxLength, int minLCaseCount, int minUCaseCount, int minNumCount, int minSpecialCount
                 */
                newPassword = pg.GenerateRandomString(8,15,1,1,1,1);
                String pass = newPassword;
                if(uName.equals("") || aName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Field Empty", Toast.LENGTH_SHORT).show();
                }else {
                    callCreate(uName, aName, pass, v);
                    Toast.makeText(getApplicationContext(), "Created User", Toast.LENGTH_SHORT).show();
                }
               // Intent myIntent = new Intent(v.getContext(), Admin.class);
                //startActivityForResult(myIntent, 0);
                //finish();



            }
        });

/*
        // Spinner element
       Spinner spinner = (Spinner) findViewById(R.id.Security);

         Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> questions = new ArrayList<String>();
      //  questions.add("What university did you attend?");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, questions);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
*/
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

    public void callCreate(final String uname,final String aname,final String pass, final View view){
        class createASync extends AsyncTask<String, Void, String> {
            //uname=medit.getText().toString();
            //String pass=epass.getText().toString();

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
                    /******************TEST USERS****************************
                     Username : UserOne
                     Password : p@ss1

                     Username : UserTwo
                     Password : p@ss2

                     ****************************************************/
                    Log.v("encode","en");
                    String data = URLEncoder.encode("uname", "UTF-8")
                            + "=" + URLEncoder.encode(uname, "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8")
                            + "=" + URLEncoder.encode(password, "UTF-8");
                    data += "&" + URLEncoder.encode("aname", "UTF-8")
                            + "=" + URLEncoder.encode(aname, "UTF-8");
/*                    data += "&" + URLEncoder.encode("sQuestion", "UTF-8")
                            + "=" + URLEncoder.encode(sQuestion, "UTF-8");
                    data += "&" + URLEncoder.encode("sAnswer", "UTF-8")
                            + "=" + URLEncoder.encode(sAnswer, "UTF-8");
*/                    Log.v("encode","en");

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
                TextView newPasswordView = (TextView) findViewById(R.id.newPassGen);
                TextView PasswordTagView = (TextView) findViewById(R.id.passTag);
                Button bOK = (Button) findViewById(R.id.bOK);

                PasswordTagView.setVisibility(TextView.VISIBLE);
                newPasswordView.setText(newPassword);
                newPasswordView.setVisibility(TextView.VISIBLE);

                bOK.setVisibility(Button.VISIBLE);

                bOK.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent myIntent = new Intent(v.getContext(), Admin.class);
                        startActivityForResult(myIntent, 0);
                        finish();
                    }
                });
            }

        }

        createASync las = new createASync();
        las.execute(LINK ,uname,pass, aname);
    }
}
