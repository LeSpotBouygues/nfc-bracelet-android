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
    private static final String COMPANIONS_COL_PRESENCE = "presence";
    private static final String COMPANIONS_COL_CHIEF = "chief";

    private static final String TABLE_TEAMS_COMPANIONS = "teams_companions_table";
    private static final String TEAMS_COMPANIONS_COL_ID = "id";
    private static final String TEAMS_COMPANIONS_COL_TEAM_ID = "team_id";
    private static final String TEAMS_COMPANIONS_COL_CHIEF_ID = "chief_id";
    private static final String TEAMS_COMPANIONS_COL_COMPANION_ID = "companion_id";

    private static final String TABLE_TEAMS_TASKS = "teams_tasks_table";
    private static final String TEAMS_TASKS_COL_ID = "id";
    private static final String TEAMS_TASKS_COL_TEAM_ID = "team_id";
    private static final String TEAMS_TASKS_COL_CHIEF_ID = "chief_id";
    private static final String TEAMS_TASKS_COL_TASK_ID = "task_id";

    private static final String TABLE_TASKS = "tasks_table";
    private static final String TASKS_COL_ID = "id";
    private static final String TASKS_COL_TASK_ID = "task_id";
    private static final String TASKS_COL_SHORT_NAME = "short_name";
    private static final String TASKS_COL_LONG_NAME = "long_name";
    private static final String TASKS_COL_CODE = "code";

    private static final String TABLE_COMPANION_TASKS = "companion_tasks_table";
    private static final String COMPANION_TASKS_COL_ID = "id";
    private static final String COMPANION_TASKS_COL_COMPANION_ID = "companion_id";
    private static final String COMPANION_TASKS_COL_TASK_ID = "task_id";

    private static final String TABLE_HISTORY = "history_table";
    private static final String HISTORY_COL_ID = "id";
    private static final String HISTORY_COL_COMPANION_ID = "companion_id";
    private static final String HISTORY_COL_TASK_ID = "task_id";
    private static final String HISTORY_COL_DURATION = "duration";
    private static final String HISTORY_COL_DATE = "date";
    private static final String HISTORY_COL_LAST_START = "last_start";
    private static final String HISTORY_COL_STARTED = "started";
    private static final String HISTORY_COL_SENT = "sent";

    private static final String CREATE_TABLE_COMPANIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_COMPANIONS + " ("
            + COMPANIONS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COMPANIONS_COL_USER_ID + " TEXT NOT NULL, "
            + COMPANIONS_COL_FIRSTNAME + " TEXT NOT NULL, "
            + COMPANIONS_COL_LASTNAME + " TEXT NOT NULL, "
            + COMPANIONS_COL_POSITION + " TEXT NOT NULL, "
            + COMPANIONS_COL_BRACELET_ID + " TEXT NOT NULL, "
            + COMPANIONS_COL_PRESENCE + " INTEGER, "
            + COMPANIONS_COL_CHIEF + " INTEGER);";

    private static final String CREATE_TABLE_TEAMS_COMPANIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_TEAMS_COMPANIONS + " ("
            + TEAMS_COMPANIONS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TEAMS_COMPANIONS_COL_TEAM_ID + " TEXT NOT NULL, "
            + TEAMS_COMPANIONS_COL_CHIEF_ID + " TEXT NOT NULL, "
            + TEAMS_COMPANIONS_COL_COMPANION_ID + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_TEAMS_TASKS = "CREATE TABLE IF NOT EXISTS " + TABLE_TEAMS_TASKS + " ("
            + TEAMS_TASKS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TEAMS_TASKS_COL_TEAM_ID + " TEXT NOT NULL, "
            + TEAMS_TASKS_COL_CHIEF_ID + " TEXT NOT NULL, "
            + TEAMS_TASKS_COL_TASK_ID + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_TASKS = "CREATE TABLE IF NOT EXISTS " + TABLE_TASKS + " ("
            + TASKS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASKS_COL_TASK_ID + " TEXT NOT NULL, "
            + TASKS_COL_SHORT_NAME + " TEXT NOT NULL, "
            + TASKS_COL_LONG_NAME + " TEXT NOT NULL, "
            + TASKS_COL_CODE + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_COMPANION_TASKS = "CREATE TABLE IF NOT EXISTS " + TABLE_COMPANION_TASKS + " ("
            + COMPANION_TASKS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COMPANION_TASKS_COL_COMPANION_ID + " TEXT NOT NULL, "
            + COMPANION_TASKS_COL_TASK_ID + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_HISTORY = "CREATE TABLE IF NOT EXISTS " + TABLE_HISTORY + " ("
            + HISTORY_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + HISTORY_COL_COMPANION_ID + " TEXT NOT NULL, "
            + HISTORY_COL_TASK_ID + " TEXT NOT NULL, "
            + HISTORY_COL_DURATION + " TEXT NOT NULL, "
            + HISTORY_COL_DATE + " TEXT NOT NULL, "
            + HISTORY_COL_LAST_START + " TEXT NOT NULL, "
            + HISTORY_COL_STARTED + " INTEGER, "
            + HISTORY_COL_SENT + " INTEGER);";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //on crée la table à partir de la requête écrite dans la variable CREATE_BDD
        db.execSQL(CREATE_TABLE_COMPANIONS);
        db.execSQL(CREATE_TABLE_TEAMS_COMPANIONS);
        db.execSQL(CREATE_TABLE_TEAMS_TASKS);
        db.execSQL(CREATE_TABLE_TASKS);
        db.execSQL(CREATE_TABLE_COMPANION_TASKS);
        db.execSQL(CREATE_TABLE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On peut faire ce qu'on veut ici moi j'ai décidé de supprimer la table et de la recréer
        //comme ça lorsque je change la version les id repartent de 0
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANIONS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS_COMPANIONS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS_TASKS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANION_TASKS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY + ";");
        onCreate(db);
    }
}
