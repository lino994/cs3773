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
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 * Used to check the user's answer to
 * the security question is correct
 * This is done for the purpose of
 * resetting the password
 */
public class AnswerQuestion extends AppCompatActivity {
    /* variables to be used */

        //link to file in server
    private static final String LINK = "http://galadriel.cs.utsa.edu/~group5/getAnswer.php";
    Button bSubmit;     //button to submit answer
    String uname;       //username answer is attributed
    String question;    //question to be displayer
    String answer;      //answer given
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        question = intent.getExtras().getString("result");                  //question passed by previous intent

        /**
         * Set Display to Show Question from the User's Info
         * Stored on the Server
         */

        setContentView(R.layout.activity_answer_question);
        TextView tView = (TextView) findViewById(R.id.questionView);
        tView.setText(question);

        /**
         * Initialize variables to be used
         */

        EditText ansQues = (EditText) findViewById(R.id.edAnswer);
        uname = intent.getExtras().getString("username");
        bSubmit = (Button) findViewById(R.id.bSubmitAnswer);

        /*initialize submit button to set connection */
        bSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText ansQues = (EditText) findViewById(R.id.edAnswer);
                answer = ansQues.getText().toString();
                initQuestion(answer, v, uname);
            }
        });
    }

    /**
     * Main Function Creates the ASync Class to connect to database and use the
     * required php file to check if the user's answer is correct
     * @param answer  - answer submitted by user
     * @param v       - view
     * @param uname   - username to check if the answer is correct
     */
    public void initQuestion(final String answer, final View v, final String uname) {
        class QuestionASync extends AsyncTask<String, Void, String> {

            private Dialog loadingDiag;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDiag = ProgressDialog.show(AnswerQuestion.this,"Please Wait","loading..");
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                String answer = params[1];
                String uname = params[2];

                try{
                    /**
                     * Data to be encoded to be sent to php file
                     * username
                     * answer
                     */
                    String data = URLEncoder.encode("uname", "UTF-8")
                            + "=" + URLEncoder.encode(uname, "UTF-8");
                    data += "&" + URLEncoder.encode("answer", "UTF-8")
                            + "=" + URLEncoder.encode(answer, "UTF-8");
                    Log.v("user from answer: ", uname);
                    Log.v("answer from answer", answer);

                    /**
                     * Establish connection and send data string
                     */
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

            /**
             * After connection is established and file returns
             * @param result
             */
            @Override
            protected void onPostExecute(String result) {
                loadingDiag.dismiss();
                String r = result.trim();
                Log.v("result: ", r);

                /**
                 * if answer was incorrect display msg telling user
                 * else take them to resetPassword
                 * */

                if (r.equals("failure")) {
                    Toast.makeText(getApplicationContext(), "Incorrect Answer", Toast.LENGTH_SHORT).show();
                }else {
                    Intent ResetIntent = new Intent(v.getContext(), ResetPassword.class );
                    ResetIntent.putExtra("uname", uname);
                    startActivity(ResetIntent);
                }
            }

        }
        //execute ASync Class
        QuestionASync qas = new QuestionASync();
        qas.execute(LINK ,answer,uname);
    }
}
