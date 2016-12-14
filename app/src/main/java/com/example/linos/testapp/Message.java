package com.example.linos.testapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by linos on 12/10/2016.
 */

public class Message {
    private static final String DATABASE_NAME = "random.db";
    public static final String TABLE_NAME = "messages_table";

    String sender;
    String senderName;
    String recv;
    String msg;
    int messageNumber;

    boolean read;

    int encryptMethod;


    Date time;
    DateFormat dateFormat;
    String timeString;

    public Message(String sender, String recv, String msg , int messageNumber, int encryptMethod) {
        dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        this.sender = sender;
        senderName = "";
        this.recv = recv;
        this.msg = msg;
        this.messageNumber = messageNumber;
        read = false;

        this.encryptMethod = encryptMethod;

    }

    public int getEncryptMethod() {
        return encryptMethod;
    }

    public void setEncryptMethod(int encryptMethod) {
        this.encryptMethod = encryptMethod;
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

    public int getMessageNumber() { return messageNumber; }

    public void setRead() {
        read = true;
    }

    public boolean read() {
        return read;
    }


    public Date getTime() {
        return time;
    }

    public String getTimeString() {

        return timeString;
    }

    public void setTime(Date time) {
        this.time = time;
    }


    public void setTimeString(String timeString) {
        try {
            time = dateFormat.parse(timeString);
            this.timeString = timeString;
        } catch (Exception e) {
            System.out.println("Date error:" + e.getMessage());
        }
    }

    public void setSenderName(String name) {
        senderName=name;
    }

    public String getSenderName() {
        return senderName;
    }

    public Bundle getMessageBundle () {
        Bundle b = new Bundle();

        b.putString("messageText", msg);
        b.putString("sender", sender);
        b.putString("recv", recv);
        b.putString("time", timeString);
        b.putInt("encrypt", encryptMethod);
        b.putBoolean("read", read);
        b.putInt("messageNumber", messageNumber);

        return b;
    }

    public void setMessageText(String messageText) {
        this.msg = messageText;
    }
}
