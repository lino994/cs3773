package com.example.linos.testapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;

public class SetEncryption extends AppCompatActivity {
    ListView optionList;                //listview to be seen (encryption options)
    EncryptCheckBoxModel options[];     //encryption options
    Button bContinue;                   //button to submit info

    String reciever;                    //reciever of msg to be sent
    String uname;                       //username of sender

    Bundle info;                        //bundle will store username and reciever to pass along until msg is sent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_encryption);
        Intent myIntent = getIntent();

        /*
          username of sender and reciever to be sent to next activity
          after user decides on encryption type it is put in a bundle
          to be passed along until user sends message
         */
        uname = myIntent.getExtras().getString("uname");
        reciever = myIntent.getExtras().getString("recv");
        info = new Bundle();
        info.putString("uname",uname);
        info.putString("reciever",reciever);

        bContinue = (Button) findViewById(R.id.bCont);

        /* set the list of options the user will have */
        optionList = (ListView) findViewById(R.id.listView1);
        options = new EncryptCheckBoxModel[2];
        options[0] = new EncryptCheckBoxModel("Encryption by Key");
        options[1] = new EncryptCheckBoxModel("Encryption by Pattern");

        /* initalize the option listView for user to choose from */
        EncryptOptionAdapter adapter = new EncryptOptionAdapter(this, options);
        optionList.setAdapter(adapter);

        /*checks when user has made a decision */
        bContinue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /*both encryption types selected*/
                if(options[0].getIsChecked() && options[1].getIsChecked()){
                    System.out.println("both Selected");
                    info.putBoolean("needsBoth", true);             //used to check if both encryption will be needed before writing msg
                }
                /*key encryption selected*/
                else if(options[0].getIsChecked() && !options[1].getIsChecked()){
                    System.out.println("key Selected");
                    info.putBoolean("needsBoth", false);           //used to check if both encryption will be needed before writing msg
                }
                /*pattern encryption selected*/
                else if(!options[0].getIsChecked() && options[1].getIsChecked()){
                    Intent newMessage = new Intent(SetEncryption.this, PatternEncrypt.class);
                    info.putBoolean("needsBoth", false);            //used to check if both encryption will be needed before writing msg
                    newMessage.putExtras(info);
                    startActivity(newMessage);
                    finish();
                }
                /*none of the encryptions selected */
                else if(!options[0].getIsChecked() && !options[1].getIsChecked()){
                    Intent newMessage = new Intent(SetEncryption.this, SendMessage.class);
                    newMessage.putExtras(info);
                    startActivity(newMessage);
                    finish();
                }
            }
        });

    }


    @Override
    public void onBackPressed(){
        if(uname.equals("admin")){
            Intent myIntent = new Intent(SetEncryption.this, Admin.class);
            myIntent.putExtra("uname",uname);
            startActivityForResult(myIntent, 0);
            finish();
        }
        else{
            Intent myIntent = new Intent(SetEncryption.this, Messaging.class);
            myIntent.putExtra("uname",uname);
            startActivityForResult(myIntent, 0);
            finish();
        }
    }

}
