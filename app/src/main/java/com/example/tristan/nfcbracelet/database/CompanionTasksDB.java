package com.example.tristan.nfcbracelet.database;

/**
 * Created by Tristan on 02/04/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.tristan.nfcbracelet.models.Task;

import java.util.ArrayList;

import com.example.tristan.nfcbracelet.models.Companion;

import java.util.ArrayList;

/**
 * Created by Tristan on 30/03/2016.
 */
public class CompanionTasksDB {
    public static final String TAG = "CompanionTasksDB";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "COP78.db";

    private static final String TABLE_COMPANION_TASKS = "companion_tasks_table";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_COMPANION_ID = "companion_id";
    private static final int NUM_COL_COMPANION_ID = 1;
    private static final String COL_TASK_ID = "task_id";
    private static final int NUM_COL_TASK_ID = 2;

    private SQLiteDatabase db;
    private Context mContext;
    private DBHelper DBHelper;

    public CompanionTasksDB(Context context){
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

    public void insertTaskForCompanion(Companion companion, Task task){

        //Log.d(TAG, "insert task "+task.getTaskId()+" for companion "+companion.getUserId());
            //Création d'un ContentValues (fonctionne comme une HashMap)
            ContentValues values = new ContentValues();
            //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
            values.put(COL_COMPANION_ID, companion.getUserId());
            values.put(COL_TASK_ID, task.getTaskId());
        db.insert(TABLE_COMPANION_TASKS, null, values);
    }

    public void updateTaskForCompanion(Companion companion, Task task){

        //Log.d(TAG, "update task "+task.getTaskId()+" for companion "+companion.getUserId());
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_COMPANION_ID, companion.getUserId());
        values.put(COL_TASK_ID, task.getTaskId());
        db.update(TABLE_COMPANION_TASKS, values, COL_COMPANION_ID + " LIKE \"" + companion.getUserId() + "\" AND " + COL_TASK_ID + " LIKE \"" + task.getTaskId() + "\"", null);
    }

    public void deleteAllTasksForCompanion(Companion companion) {
        db.delete(TABLE_COMPANION_TASKS, COL_COMPANION_ID + " LIKE \"" + companion.getUserId() + "\"", null);
    }

    public Task getSingleTaskByCompanionId(String companionId, String taskId){
        //Récupère dans un Cursor les valeurs correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
        Cursor c = db.query(TABLE_COMPANION_TASKS, new String[] {COL_ID, COL_COMPANION_ID, COL_TASK_ID}, COL_COMPANION_ID + " LIKE \"" + companionId + "\" AND " + COL_TASK_ID + " LIKE \"" + taskId + "\"", null, null, null, null);
        if (c.getCount() == 0)
            return null;
        c.moveToFirst();
        TaskDB taskDB = new TaskDB(mContext);
        taskDB.open();
        Task task = taskDB.getTaskByTaskId(c.getString(NUM_COL_TASK_ID));
        taskDB.close();
        return task;
    }

    public void displayTable() {
        Log.d(TAG, "display table");
        Cursor c = db.query(TABLE_COMPANION_TASKS, new String[] {COL_ID, COL_COMPANION_ID, COL_TASK_ID}, null, null, null, null, null);
        if (c.getCount() == 0)
            return;
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            //Log.d(TAG, "id=" + c.getString(NUM_COL_ID) + ", companion_id=" + c.getString(NUM_COL_COMPANION_ID) +", task_id="+c.getString(NUM_COL_TASK_ID));
            c.moveToNext();
        }
    }

    public ArrayList<Task> getTasksByCompanion(Companion companion) {
        ArrayList<Task> tasks = new ArrayList<>();

        Cursor c = db.query(TABLE_COMPANION_TASKS, new String[] {COL_TASK_ID}, COL_COMPANION_ID + " LIKE \"" + companion.getUserId() +"\"", null, null, null, null, null);
        if (c.getCount() == 0)
            return null;

        TaskDB taskDB = new TaskDB(mContext);
        c.moveToFirst();
        //Log.d(TAG, "number of tasks for companion "+companion.getUserId() +" = " + Integer.toString(c.getCount()));
        for (int i=0; i < c.getCount(); i++) {
            taskDB.open();
            Task task = taskDB.getTaskByTaskId(c.getString(0));
            taskDB.close();
            if (task != null)
                tasks.add(task);
            c.moveToNext();
        }

        return tasks;
    }
}
