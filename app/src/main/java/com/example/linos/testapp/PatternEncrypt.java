package com.example.linos.testapp;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import android.widget.*;

/**
 * Sets Pattern Encryption
 */
public class PatternEncrypt extends AppCompatActivity implements View.OnClickListener{
    String code ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pattern);


        /* Capture our button from layout */
        Button b1 = (Button)findViewById(R.id.button1);
        Button b2 = (Button)findViewById(R.id.button2);
        Button b3 = (Button)findViewById(R.id.button3);
        Button b4 = (Button)findViewById(R.id.button4);
        Button b5 = (Button)findViewById(R.id.button5);
        Button b6 = (Button)findViewById(R.id.button6);
        Button b7 = (Button)findViewById(R.id.button7);
        Button b8 = (Button)findViewById(R.id.button8);
        Button b9 = (Button)findViewById(R.id.button9);
        Button b0 = (Button)findViewById(R.id.button0);
        // Register the onClick listener with the implementation above
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        b7.setOnClickListener(this);
        b8.setOnClickListener(this);
        b9.setOnClickListener(this);
        b0.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // do something when the button is clicked
        // Yes we will handle click here but which button clicked??? We don't know

        // So we will make
        switch (v.getId() /*to get clicked view id**/) {
            case R.id.button1:
                code += "1";

                System.out.println("I am a button1");
                System.out.println(code);
                // do something when the button1 is clicked

                break;
            case R.id.button2:
                System.out.println("I am a button2");
                code += "2";
                // do something when the button2 is clicked
                System.out.println(code);
                break;
            case R.id.button3:
                System.out.println("I am a button3");
                // do something when the button3 is clicked
                code += "3";
                System.out.println(code);
                break;
            case R.id.button4:
                System.out.println("I am a button4");
                // do something when the button3 is clicked
                code += "4";
                System.out.println(code);
                break;

            case R.id.button5:
                System.out.println("I am a button5");
                // do something when the button3 is clicked
                code += "5";
                System.out.println(code);
                break;
            case R.id.button6:
                System.out.println("I am a button6");
                // do something when the button3 is clicked
                code += "6";
                System.out.println(code);
                break;
            case R.id.button7:
                System.out.println("I am a button7");
                // do something when the button3 is clicked
                code += "7";
                System.out.println(code);
                break;
            case R.id.button8:
                System.out.println("I am a button8");
                // do something when the button3 is clicked
                code += "8";
                System.out.println(code);
                break;
            case R.id.button9:
                System.out.println("I am a button9");
                // do something when the button3 is clicked
                code += "9";
                System.out.println(code);
                break;
            case R.id.button0:
                System.out.println("I am a button0");
                // do something when the button3 is clicked
                code += "0";
                System.out.println(code);
                break;
            default:
                break;
        }
    }
}