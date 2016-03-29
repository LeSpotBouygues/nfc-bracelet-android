package com.example.tristan.nfcbracelet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tristan.nfcbracelet.models.Companion;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tristan on 14/03/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "COP78.db";

    private static final String TABLE_COMPANIONS = "companions_table";
    private static final String COMPANIONS_COL_ID = "id";
    private static final String COMPANIONS_COL_USER_ID = "user_id";
    private static final String COMPANIONS_COL_FIRSTNAME = "firstname";
    private static final String COMPANIONS_COL_LASTNAME = "lastname";
    private static final String COMPANIONS_COL_POSITION = "position";
    private static final String COMPANIONS_COL_BRACELET_ID = "bracelet_id";
    private static final String COMPANIONS_COL_CHIEF = "chief";

    private static final String TABLE_TEAMS = "teams_table";
    private static final String TEAMS_COL_ID = "id";
    private static final String TEAMS_COL_TEAM_ID = "team_id";
    private static final String TEAMS_COL_CHIEF_ID = "chief_id";
    private static final String TEAMS_COL_COMPANION_ID = "companion_id";

    private static final String CREATE_TABLE_COMPANIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_COMPANIONS + " ("
            + COMPANIONS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COMPANIONS_COL_USER_ID + " TEXT NOT NULL, "
            + COMPANIONS_COL_FIRSTNAME + " TEXT NOT NULL, "
            + COMPANIONS_COL_LASTNAME + " TEXT NOT NULL, "
            + COMPANIONS_COL_POSITION + " TEXT NOT NULL, "
            + COMPANIONS_COL_BRACELET_ID + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_TEAMS = "CREATE TABLE IF NOT EXISTS " + TABLE_TEAMS + " ("
            + TEAMS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TEAMS_COL_TEAM_ID + " TEXT NOT NULL, "
            + TEAMS_COL_CHIEF_ID + " TEXT NOT NULL, "
            + TEAMS_COL_COMPANION_ID + " TEXT NOT NULL);";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //on crée la table à partir de la requête écrite dans la variable CREATE_BDD
        //db.execSQL(CREATE_TABLE_COMPANIONS);
        db.execSQL(CREATE_TABLE_TEAMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On peut faire ce qu'on veut ici moi j'ai décidé de supprimer la table et de la recréer
        //comme ça lorsque je change la version les id repartent de 0
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANIONS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS + ";");
        onCreate(db);
    }
}
