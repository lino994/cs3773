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

/**
 * Reset Password Activity where user can choose a new password
 * it then updates the database if password meets specifications
 * it has to contain:
 *     - 8 to 10 Characters
 *     - One UpperCase
 *     - One LowerCase
 *     - One Special Char
 *     - One Number
 *
 */
public class ResetPassword extends AppCompatActivity {
    private static final String LINK = "http://galadriel.cs.utsa.edu/~group5/setPassword.php";  //link to connect to database
    Button bSubmit;         //submit new password
    EditText edPass1;       //password field one
    EditText edPass2;       /*password field two
                             (user has to enter password twice)*/
    String uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Intent ResetIntent  = getIntent();

        /* initalize the variables to be used */
        uname = ResetIntent.getExtras().getString("uname");
        edPass1 = (EditText) findViewById(R.id.edNewPass);
        edPass2 = (EditText) findViewById(R.id.ednewPass2);
        bSubmit = (Button) findViewById(R.id.bSubmitNewPass);

        //if button is clicked
        bSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //set text entered to string
                String pass1 = edPass1.getText().toString();
                String pass2 = edPass2.getText().toString();

                //initalize a new password Checker
                PasswordChecker pCheck = new PasswordChecker();
                Log.v("pass1", pass1);
                Log.v("pass2", pass2);

                    //if password fields don't match give user a message
                if(!pass1.equals(pass2)){
                    Toast.makeText(getApplicationContext(), "Passwords Do Not Match", Toast.LENGTH_SHORT).show();

                    //if they do equal  but password is not valid give the user an error
                }else if(pass1.equals(pass2) && !pCheck.isValidPassword(pass1)) {
                    Toast.makeText(getApplicationContext(), "Invalid Password Type", Toast.LENGTH_SHORT).show();

                    //if they are equal and password is valid initSubmit is called
                }else if(pass1.equals(pass2) && pCheck.isValidPassword(pass1)){
                    String pass = pass1;
                    initSubmit(pass, v, uname);
                }
            }
        });
    }

    @Override
    //if back button is pressed it prevents users from returning to this screen
    public void onBackPressed(){

        if(uname.equals("admin")){
            Intent myIntent = new Intent(ResetPassword.this, Admin.class);
            myIntent.putExtra("uname",uname);
            startActivityForResult(myIntent, 0);
            finish();
        }
        else{
            Intent myIntent = new Intent(ResetPassword.this, Messaging.class);
            myIntent.putExtra("uname",uname);
            startActivityForResult(myIntent, 0);
            finish();
        }


    }

    /**
     *  connect to database and update the users
     *  new password using their username
     *  reference to find them in the userTable
     */
    public void initSubmit(final String pass, final View v, final String uname) {
        class ResetASync extends AsyncTask<String, Void, String> {

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
                  //set data to be written
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
                       // Log.v("line: ", line);
                        sb.append(line + "\n");
                    }
                    Log.v("string from reset:", sb.toString());
                    return sb.toString();

                }catch(Exception e){
                    Log.v("Conn Error  :", e.getMessage());
                    return new String ("Exception: " + e.getMessage());
                }
            }
            @Override
            /**
             * once password is reset it takes the user back to
             * the login page
             */
            protected void onPostExecute(String result) {
                loadingDiag.dismiss();
                String r = result.trim();
                Log.v("result: ", r);
                Intent backToMain = new Intent(v.getContext(), MainActivity.class);
                startActivity(backToMain);
                finish();
            }

        }
        //execute async
        ResetASync ras = new ResetASync();
        ras.execute(LINK , uname, pass);
    }
}
