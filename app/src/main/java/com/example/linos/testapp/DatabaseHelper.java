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
    public static final String COL_3 = "username";
    public static final String COL_4 = "time";
    //private static final int DATABASE_VERSION = 2;



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT,message TEXT,username Text,time TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE OF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    public boolean insertData(String message,String username,String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,message);
        contentValues.put(COL_3,username);
        contentValues.put(COL_4,time);
        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1){
            return false;
        }
        else{
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
    public void delete(){
        SQLiteDatabase db = this.getWritableDatabase();
        if(db != null) {
            db.execSQL("delete from " + TABLE_NAME);
            onCreate(db);
        }
    }

}