package com.example.linos.testapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ResetPassword extends AppCompatActivity {
    private static final String LINK = "http://galadriel.cs.utsa.edu/~group5/setPassword.php";
    Button bSubmit;
    EditText edPass1;
    EditText edPass2;
    String uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Intent ResetIntent  = getIntent();
        uname = ResetIntent.getExtras().getString("uname");
        edPass1 = (EditText) findViewById(R.id.edNewPass);
        edPass2 = (EditText) findViewById(R.id.ednewPass2);
        bSubmit = (Button) findViewById(R.id.bSubmitNewPass);

        bSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String pass1 = edPass1.getText().toString();
                String pass2 = edPass2.getText().toString();
                Log.v("pass1", pass1);
                Log.v("pass2", pass2);

                if(!pass1.equals(pass2)){
                    Toast.makeText(getApplicationContext(), "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                }else {
                    String pass = pass1;
                    initSubmit(pass, v, uname);
                }
            }
        });
    }


    public void initSubmit(final String pass, final View v, final String uname) {
        class QuestionASync extends AsyncTask<String, Void, String> {

            private Dialog loadingDiag;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDiag = ProgressDialog.show(ResetPassword.this,"Please Wait","loading..");
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                String uname = params[1];
                String pass = params[2];

                try{
                    /******************TEST USERS****************************
                     Username : UserOne
                     Password : p@ss1

                     Username : UserTwo
                     Password : p@ss2

                     ****************************************************/
                    String data = URLEncoder.encode("uname", "UTF-8")
                            + "=" + URLEncoder.encode(uname, "UTF-8");
                    data += "&" + URLEncoder.encode("pass", "UTF-8")
                            + "=" + URLEncoder.encode(pass, "UTF-8");

                    //Log.v("user: ", uname);
                    //Log.v("answer", answer);

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
                Intent backToMain = new Intent(v.getContext(), MainActivity.class);
                startActivity(backToMain);
            }

        }
        QuestionASync qas = new QuestionASync();
        qas.execute(LINK , uname, pass);
    }
}
