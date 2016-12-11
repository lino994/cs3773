package com.example.linos.testapp;

/**
 * Created by linos on 12/10/2016.
 */

public class Message {
    String sender;
    String recv;
    String msg;

    public Message(String sender, String recv, String msg ){
        this.sender = sender;
        this.recv = recv;
        this.msg = msg;
    }

    public String getSender(){
        return sender;
    }

    public String getMessage(){
        return msg;
    }

    public String getRecv(){
        return recv;
    }
}
