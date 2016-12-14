package com.example.linos.testapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by nique on 12/10/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "random.db";
    public static final String TABLE_NAME = "messages_table";
    public static final String COL_1 = "id";
    public static final String COL_2 = "message";
    public static final String COL_3 = "sender";
    public static final String COL_4 = "time";
    public static final String COL_5 = "messageNumber";
    public static final String COL_6 = "recv";
    public static final String COL_7 = "read";
    public static final String COL_8 = "encrypt";
    public static final String COL_9 = "try";
    private boolean[] messageStatus;
    //private static final int DATABASE_VERSION = 2;



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT,message TEXT,sender Text," +
                "time TEXT, messageNumber INT, recv TEXT, read INT, encrypt INT, try INT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE OF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    public boolean insertData(String message,String sender,String time,
                              int messageNumber, String recv, int read, int encrypt){

        if (oldMessage(messageNumber)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, message);
        contentValues.put(COL_3, sender);
        contentValues.put(COL_4, time);
        contentValues.put(COL_5, messageNumber);
        contentValues.put(COL_6, recv);
        contentValues.put(COL_7, read);
        contentValues.put(COL_8, encrypt);
        contentValues.put(COL_9, 0);
        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAllMessages(String uname){
        SQLiteDatabase db = this.getWritableDatabase();
        int d = db.getVersion();
        Log.v("db",Integer.toString(d));
        Cursor res = db.rawQuery("select * from " + TABLE_NAME ,null);
        return res;
    }

    public Cursor getMessageAt(int messageNumber){
        SQLiteDatabase db = this.getWritableDatabase();
        int d = db.getVersion();
        Log.v("db",Integer.toString(d));
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE messageNumber=?"
                , new String[]{String.valueOf(messageNumber)});
        return res;
    }

    public void delete(){
        SQLiteDatabase db = this.getWritableDatabase();
        if(db != null) {
            db.execSQL("delete from " + TABLE_NAME);
            onCreate(db);
        }
    }

    public boolean updateMessageText(int messageNumber, String messageText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_2, messageText);
        int result = db.update(TABLE_NAME, cv, "messageNumber = ?"
                , new String[]{String.valueOf(messageNumber)});
        return result > 0;
    }
    public boolean updateRead(int messageNumber, int read) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("read", 1);
        int result = db.update(TABLE_NAME, cv, "messageNumber = ?"
                , new String[]{String.valueOf(messageNumber)});
        return result > 0;
    }
    public boolean updateEncrypt(int messageNumber, int encrypt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_8, encrypt);
        int result = db.update(TABLE_NAME, cv, "messageNumber = ?"
                , new String[]{String.valueOf(messageNumber)});
        return result > 0;
    }

    public boolean updateTry(int messageNumber, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_9, value);
        int result = db.update(TABLE_NAME, cv, "messageNumber = ?"
                , new String[]{String.valueOf(messageNumber)});
        return result > 0;
    }

    public boolean updateTime(int messageNumber, String timeString) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_4, timeString);

        int result = db.update(TABLE_NAME, cv, "messageNumber = ?"
                , new String[]{String.valueOf(messageNumber)});
        return result > 0;
    }

    private boolean oldMessage(int messageNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME +
                " where " + COL_5 + "= ?", new String[] {String.valueOf(messageNumber)});
        int count = res.getCount();
        if (count > 0) {
            System.out.println("count = " + count);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteMessage(int messageNumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(TABLE_NAME, "messageNumber = ?"
                , new String[]{String.valueOf(messageNumber)});
        return result > 0;
    }

}