package com.example.linos.testapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

/**
 * Sets Pattern Encryption
 */
public class PatternEncrypt extends AppCompatActivity implements View.OnClickListener{
    String code ="";
    //String uname;
    Bundle info;             //info including current user and reciever of msg to be sent
    Boolean needsBoth;       //will key encryption be nessecary
    int count = 0;
    String encryptedMessage = "";
    Button bNext;            //button used for testing to go to next instance
    String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pinpattern);

        /**
         * Grab sender and reciever to be used in send message
         **/
        Intent ResetIntent  = getIntent();
        info = ResetIntent.getExtras();
        needsBoth = info.getBoolean("needsBoth");           //check if key pattern is needed before going to send msg
        info.putBoolean("needsPattern",true);
        action = info.getString("action");
        bNext = (Button) findViewById(R.id.bNext);          //go automatically to next screen (TESTING ONLY)

        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(needsBoth){
                    Intent keyAct = new Intent(PatternEncrypt.this, EnterKey.class);
                    info.putString("patternKey", code);
                    keyAct.putExtras(info);
                    startActivity(keyAct);
                    finish();
                }else{
                    Intent newIntend = new Intent(PatternEncrypt.this, SendMessage.class);
                    if (action.equals("readMessage")) {
                        newIntend = new Intent(PatternEncrypt.this, MessageDisplay.class);
                    }
                    info.putString("patternKey",code);
                    newIntend.putExtras(info);
                    startActivity(newIntend);
                    finish();
                }
            }
        });

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
        count++;

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

    public String getCode() {
        return code;
    }
}