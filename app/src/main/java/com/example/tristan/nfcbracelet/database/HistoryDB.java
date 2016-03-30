package com.example.tristan.nfcbracelet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tristan.nfcbracelet.models.History;
import com.example.tristan.nfcbracelet.models.Task;

import java.util.ArrayList;

/**
 * Created by Tristan on 30/03/2016.
 */
public class HistoryDB {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "COP78.db";

    private static final String TABLE_HISTORY = "history_table";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_COMPANION_ID = "companion_id";
    private static final int NUM_COL_COMPANION_ID = 1;
    private static final String COL_TASK_ID = "task_id";
    private static final int NUM_COL_TASK_ID = 2;
    private static final String COL_DURATION = "duration";
    private static final int NUM_COL_DURATION = 3;
    private static final String COL_DATE = "date";
    private static final int NUM_COL_DATE = 4;

    private SQLiteDatabase db;
    private Context mContext;

    private DBHelper DBHelper;

    public HistoryDB(Context context){
        //On crée la BDD et sa table
        DBHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    public void open(){
        //on ouvre la BDD en écriture
        db = DBHelper.getWritableDatabase();
    }

    public void close(){
        //on ferme l'accès à la BDD
        db.close();
    }

    public SQLiteDatabase getDB(){
        return db;
    }

    public long insertHistory(History history){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_COMPANION_ID, history.getCompanion().getUserId());
        values.put(COL_TASK_ID, history.getTask().getTaskId());
        values.put(COL_DURATION, history.getDuration());
        values.put(COL_DATE, history.getDate());

        //on insère l'objet dans la BDD via le ContentValues
        return db.insert(TABLE_HISTORY, null, values);
    }

    public ArrayList<History> getAllHistoryByCompanionId(int companionId) {
        ArrayList<History> historyList = new ArrayList<>();

        Cursor c = db.query(TABLE_HISTORY, new String[] {COL_ID, COL_COMPANION_ID, COL_TASK_ID, COL_DURATION, COL_DATE}, COL_COMPANION_ID + " LIKE \"" + companionId +"\"", null, null, null, null);
        if (c.getCount() == 0)
            return null;

        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {
            History history = new History();
            history.setId(c.getInt(NUM_COL_ID));
            CompanionDB companionDB = new CompanionDB(mContext);
            companionDB.open();
            history.setCompanion(companionDB.getCompanionByUserId(c.getString(NUM_COL_COMPANION_ID)));
            companionDB.close();
            TaskDB taskDB = new TaskDB(mContext);
            taskDB.open();
            history.setTask(taskDB.getTaskByTaskId(c.getString(NUM_COL_TASK_ID)));
            taskDB.close();
            history.setDuration(c.getString(NUM_COL_DURATION));
            history.setDate(c.getString(NUM_COL_DATE));
            historyList.add(history);
            c.moveToNext();
        }

        return historyList;
    }
}
