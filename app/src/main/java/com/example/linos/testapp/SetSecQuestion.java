package com.example.linos.testapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;



/*
    for some reason choosing the
    mother's maiden name question does not
    add it to the security table...

    Other 2 Questions seem to work fine
 */
public class SetSecQuestion extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /*link of php file to be used*/
    private static final String LINK = "http://galadriel.cs.utsa.edu/~group5/setQuestion.php";
    private static final String LINKUPDATE = "http://galadriel.cs.utsa.edu/~group5/updateQuestion.php";

    /* variables to be used */
    String secQuestion;     //hold security Question Chosen
    String secAnswer;       //hold security Answer Chosen
    String uname;           //will hold username (passed by calling activity)
    int isNew;
    EditText edAnswer;      //will hold user's inputted EditText
    Button bSubmit;         //button to submit into table
    int count;              // count of (?)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_sec_question);

        /* grab info from the user's input to be set in Security Table */
        Intent thisIntent = getIntent();
        isNew = thisIntent.getExtras().getInt("isNew");
        uname = thisIntent.getExtras().getString("uname");
        edAnswer = (EditText) findViewById(R.id.edAnswer);
        bSubmit = (Button) findViewById(R.id.bSubmitSec);

        bSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                secAnswer = edAnswer.getText().toString();
                if(uname == null || secQuestion == null || secAnswer == null){
                    Log.v("uname",uname);
                    Log.v("secQuest",secQuestion);
                    Log.v("secAns",secAnswer);
                    Toast.makeText(getApplicationContext(), "Field Empty", Toast.LENGTH_SHORT).show();
                }else {
                    Log.v("secAns",secAnswer);
                    setQuestion(uname, secQuestion, secAnswer, v);
                }
            }
        });

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinnerQuest);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Select Question");
        categories.add("Name of First Pet?");
        categories.add("What University Did You Attend?");
        categories.add("Mother Maiden Name?");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onBackPressed(){
        if(uname.equals("admin")) {
                Intent myIntent = new Intent(SetSecQuestion.this, Admin.class);
                myIntent.putExtra("uname", uname);
                startActivity(myIntent);
                finish();
        }else{
                Intent myIntent = new Intent(SetSecQuestion.this, Messaging.class);
                myIntent.putExtra("uname", uname);
                startActivity(myIntent);
                finish();
        }
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
            secQuestion = item;
        }
    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setQuestion(final String uname, final String question, final String answer, final View v) {
        class QuestionASync extends AsyncTask<String, Void, String> {

            private Dialog loadingDiag;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDiag = ProgressDialog.show(SetSecQuestion.this,"Please Wait","loading..");
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                String uname = params[1];
                String question = params[2];
                String answer = params[3];

                try{
                    /******************TEST USERS****************************
                     Username : UserOne
                     Password : p@ss1

                     Username : UserTwo
                     Password : p@ss2

                     ****************************************************/
                    String data = URLEncoder.encode("uname", "UTF-8")
                            + "=" + URLEncoder.encode(uname, "UTF-8");
                    data += "&" + URLEncoder.encode("question", "UTF-8")
                            + "=" + URLEncoder.encode(question, "UTF-8");
                    data += "&" + URLEncoder.encode("answer", "UTF-8")
                            + "=" + URLEncoder.encode(answer, "UTF-8");

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
                if(uname == "admin") {
                    Intent myIntent = new Intent(SetSecQuestion.this, Admin.class);
                    myIntent.putExtra("uname", uname);
                    startActivity(myIntent);
                    finish();
                }else{
                    Intent myIntent = new Intent(SetSecQuestion.this, Messaging.class);
                    myIntent.putExtra("uname", uname);
                    startActivity(myIntent);
                    finish();
                }
            }

        }

        QuestionASync qas = new QuestionASync();
        //if question has to be updated or is it a new question
        if(isNew == 1) {
            qas.execute(LINKUPDATE, uname, question, answer);
        }else{
            qas.execute(LINK, uname, question, answer);
        }
    }
}
