package com.example.linos.testapp;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Messaging extends AppCompatActivity{
    String uname;
    Button bLogout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.options);
        setSupportActionBar(myToolbar);

        Intent thisIntent = getIntent();
        uname = thisIntent.getExtras().getString("uname");

        bLogout = (Button) findViewById(R.id.bLogout);
        bLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Spinner element


        // Spinner Drop down elements

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
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
                startActivity(setQuest);
                Log.v("Selected","security");
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;
            case R.id.pattern:
                Intent PatternEncrypt = new Intent(this, PatternEncrypt.class);
                startActivity(PatternEncrypt);

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