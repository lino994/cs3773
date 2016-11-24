package com.example.linos.testapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    Button btn;
    EditText medit;
    EditText epass;
    String uname;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button)findViewById(R.id.bLogin);
        medit   = (EditText)findViewById(R.id.edUsername);
        epass = (EditText)findViewById(R.id.edPassword);

        btn.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        uname = medit.getText().toString();
                        pass = epass.getText().toString();
                        Log.v("Username", uname);
                        Log.v("Password", pass);
                        if(pass.equals("admin") && uname.equals("admin")){
                            Intent myIntent = new Intent(view.getContext(), Messaging.class);
                            startActivityForResult(myIntent, 0);
                        }
                        else {

                            Toast.makeText(getApplicationContext(),"incorrect username/password",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void Login(View view) {


        Toast.makeText(getApplicationContext(),"login",Toast.LENGTH_SHORT).show();
    }
}
