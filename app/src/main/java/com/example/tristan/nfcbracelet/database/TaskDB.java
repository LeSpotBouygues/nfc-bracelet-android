package com.example.tristan.nfcbracelet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tristan.nfcbracelet.models.Companion;
import com.example.tristan.nfcbracelet.models.Task;

import java.util.ArrayList;

/**
 * Created by Tristan on 30/03/2016.
 */
public class TaskDB {
        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "COP78.db";

        private static final String TABLE_TASKS = "tasks_table";
        private static final String COL_ID = "id";
        private static final int NUM_COL_ID = 0;
        private static final String COL_TASK_ID = "task_id";
        private static final int NUM_COL_TASK_ID = 1;
        private static final String COL_SHORT_NAME = "short_name";
        private static final int NUM_COL_SHORT_NAME = 2;
        private static final String COL_LONG_NAME = "long_name";
        private static final int NUM_COL_LONG_NAME = 3;
        private static final String COL_CODE = "code";
        private static final int NUM_COL_CODE = 4;

        private SQLiteDatabase db;

        private DBHelper DBHelper;

        public TaskDB(Context context){
            //On crée la BDD et sa table
            DBHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
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

        public long insertTask(Task task){
            //Création d'un ContentValues (fonctionne comme une HashMap)
            ContentValues values = new ContentValues();
            //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
            values.put(COL_TASK_ID, task.getTaskId());
            values.put(COL_SHORT_NAME, task.getShortName());
            values.put(COL_LONG_NAME, task.getLongName());
            values.put(COL_CODE, task.getCode());

            //on insère l'objet dans la BDD via le ContentValues
            return db.insert(TABLE_TASKS, null, values);
        }

        public Task getTaskByTaskId(String taskId) {
            Cursor c = db.query(TABLE_TASKS, new String[] {COL_ID, COL_TASK_ID, COL_SHORT_NAME, COL_LONG_NAME, COL_CODE}, COL_TASK_ID + " LIKE \"" + taskId +"\"", null, null, null, null);
            return cursorToTask(c);
        }

        public ArrayList<Task> getAllTasks() {
            ArrayList<Task> tasks = new ArrayList<>();

            Cursor c = db.query(TABLE_TASKS, new String[] {COL_ID, COL_TASK_ID, COL_SHORT_NAME, COL_LONG_NAME, COL_CODE}, null, null, null, null, null);
            if (c.getCount() == 0)
                return null;

            c.moveToFirst();

            for (int i = 0; i < c.getCount(); i++) {
                Task task = new Task();
                task.setId(c.getInt(NUM_COL_ID));
                task.setTaskId(c.getString(NUM_COL_TASK_ID));
                task.setShortName(c.getString(NUM_COL_SHORT_NAME));
                task.setLongName(c.getString(NUM_COL_LONG_NAME));
                task.setCode(c.getString(NUM_COL_CODE));
                tasks.add(task);
                c.moveToNext();
            }

            return tasks;
        }

        //Cette méthode permet de convertir un cursor en un livre
        private Task cursorToTask(Cursor c){
            //si aucun élément n'a été retourné dans la requête, on renvoie null
            if (c.getCount() == 0)
                return null;

            //Sinon on se place sur le premier élément
            c.moveToFirst();
            //On créé un livre
            Task task = new Task();
            //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
            //
            task.setId(c.getInt(NUM_COL_ID));
            task.setTaskId(c.getString(NUM_COL_TASK_ID));
            task.setShortName(c.getString(NUM_COL_SHORT_NAME));
            task.setLongName(c.getString(NUM_COL_LONG_NAME));
            task.setCode(c.getString(NUM_COL_CODE));

            //On ferme le cursor
            c.close();

            //On retourne le livre
            return task;
        }
}
