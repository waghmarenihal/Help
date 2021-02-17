package com.technova.help;

/**
 * Created by Training on 8/6/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "HelpDB.db";
    public static final String TABLE_NAME = "groups";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "group_name";
    public static final String COLUMN_CONTACT_PERSON= "contact_persons";
    public static final String COLUMN_CONTACT_NUMBER = "contact_number";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table groups " +
                        "(id integer primary key, group_name text,contact_persons text,contact_number text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertContact (String group_name, String contact_persons, String contact_number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("group_name", group_name);
        contentValues.put("contact_persons", contact_persons);
        contentValues.put("contact_number", contact_number);
        db.insert("groups", null, contentValues);
        return true;
    }

    public Cursor getGroup(String group_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from groups where group_name like '%"+group_name+"%'", null );
        return res;
    }
    public Cursor getGroupNo(String group_no) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from groups where contact_number like '%"+group_no+"%'", null );
        return res;
    }
    public Cursor getGroupMember(String group_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from groups where contact_persons like '%"+group_name+"%'", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (String group_name, String contact_persons, String contact_number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("group_name", group_name);
        contentValues.put("contact_persons", contact_persons);
        contentValues.put("contact_number", contact_number);
        db.update("groups", contentValues, "group_name = ? ", new String[] { group_name } );
        return true;
    }

    public Integer deleteContact (String group_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("groups",
                "group_name = ? ",
                new String[] { group_name });
    }

    public Cursor getAllGroups() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from groups order by group_name", null );
        res.moveToFirst();
        return res;
    }
}