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

public class SetSecQuestion extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LINK = "http://galadriel.cs.utsa.edu/~group5/setSecurityQuestion.php";
    String secQuestion;
    String secAnswer;
    String uname;
    EditText edAnswer;
    Button bSubmit;
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_sec_question);
        Intent thisIntent = getIntent();

        uname = thisIntent.getExtras().getString("uname");
        edAnswer = (EditText) findViewById(R.id.edAnswer);
        bSubmit = (Button) findViewById(R.id.bSubmitSec);

        bSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(uname == null || secQuestion == null || secAnswer == null){
                    Toast.makeText(getApplicationContext(), "Field Empty", Toast.LENGTH_SHORT).show();
                }else {
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
        categories.add("Mother's Maiden Name?");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
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
            if(item.equals("Name of First Pet?")){
                secQuestion = "Name of First Pet?";

            }else if(item.equals("Mother's Maiden Name?")){
                secQuestion = "Mother's Maiden Name?";

            }else if(item.equals("Set Security Question")){
                secQuestion = "Set Security Question?";

            }
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
                String answer = params[2];

                try{
                    /******************TEST USERS****************************
                     Username : UserOne
                     Password : p@ss1

                     Username : UserTwo
                     Password : p@ss2

                     ****************************************************/
                    String data = URLEncoder.encode("uname", "UTF-8")
                            + "=" + URLEncoder.encode(uname, "UTF-8");

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
                if (r.equals("failure to find userName")) {
                    Toast.makeText(getApplicationContext(), "Unable to find Username", Toast.LENGTH_SHORT).show();
                }else {
                    // TextView qedit = (TextView) findViewById(R.id.questionView);
                    // qedit.setText(result);
                    Intent newintent = new Intent(v.getContext(), AnswerQuestion.class );
                    newintent.putExtra("result", result);
                    newintent.putExtra("username",uname);
                    startActivity(newintent);
                }
            }

        }

        QuestionASync qas = new QuestionASync();
        qas.execute(LINK ,uname, question, answer);
    }
}
