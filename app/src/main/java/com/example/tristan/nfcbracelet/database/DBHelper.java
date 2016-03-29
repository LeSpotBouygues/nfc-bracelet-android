package com.example.tristan.nfcbracelet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tristan on 14/03/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "COP78.db";
    public static final String COMPANIONS_TABLE_NAME = "companions";
    public static final String COMPANIONS_COLUMN_ID = "id";
    public static final String COMPANIONS_COLUMN_NAME = "name";
    public static final String COMPANIONS_COLUMN_JOB = "job";
    public static final String COMPANIONS_COLUMN_STATUS = "status";

    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table companions " +
                        "(id integer primary key, name text, job text, status integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS companions");
        onCreate(db);
    }

    public boolean insertContact  (String name, String job, int status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("job", job);
        contentValues.put("status", status);
        db.insert("companions", null, contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from companions where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, COMPANIONS_TABLE_NAME);
        return numRows;
    }

    public boolean updateCompanion (Integer id, String name, String job, int status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("job", job);
        contentValues.put("status", status);
        db.update("companions", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteCompanion (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("companions",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllCompanions()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from companions", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(res.getString(res.getColumnIndex(COMPANIONS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }
}
