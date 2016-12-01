package com.example.linos.testapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class Admin extends Activity implements AdapterView.OnItemSelectedListener {
    private int count;
    Button bLogout;
    String uname;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        Intent ResetIntent  = getIntent();
        uname = ResetIntent.getExtras().getString("uname");
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
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Select Option");
        categories.add("Change Password");
        categories.add("Create New User");
        categories.add("Set Security Question");
        categories.add("Encryption By Key");
        categories.add("Encryption By Pattern");



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
            if(item.equals("Create New User")){
                Intent myIntent = new Intent(view.getContext(), Create.class);
                startActivityForResult(myIntent, 0);

            }else if(item.equals("Change Password")){
                Intent changePass = new Intent(view.getContext(), ResetPassword.class);
                changePass.putExtra("uname",uname);
                startActivity(changePass);
                finish();

            }else if(item.equals("Set Security Question")){
                Intent setQuest = new Intent(view.getContext(), SetSecQuestion.class);
                setQuest.putExtra("uname",uname);
                startActivity(setQuest);
            }
        }
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
