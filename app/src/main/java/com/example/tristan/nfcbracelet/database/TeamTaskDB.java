package com.example.tristan.nfcbracelet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.tristan.nfcbracelet.models.Companion;
import com.example.tristan.nfcbracelet.models.Task;
import com.example.tristan.nfcbracelet.models.Team;

import java.util.ArrayList;

/**
 * Created by Tristan on 30/03/2016.
 */
public class TeamTaskDB {
    public static final String TAG = "TeamTaskDB";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "COP78.db";

    private static final String TABLE_TEAMS = "teams_tasks_table";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_TEAM_ID = "team_id";
    private static final int NUM_COL_TEAM_ID = 1;
    private static final String COL_CHIEF_ID = "chief_id";
    private static final int NUM_COL_CHIEF_ID = 2;
    private static final String COL_TASK_ID = "task_id";
    private static final int NUM_COL_TASK_ID = 3;

    private SQLiteDatabase db;
    private Context mContext;
    private DBHelper DBHelper;

    public TeamTaskDB(Context context){
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

    public void insertTeam(Team team){

        //Log.d(TAG, "insert team " + team.getTeamId());
        int size = team.getNumberOfTasks();
        for (int i=0; i < size; i++) {
            //Création d'un ContentValues (fonctionne comme une HashMap)
            ContentValues values = new ContentValues();
            //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
            values.put(COL_TEAM_ID, team.getTeamId());
            values.put(COL_CHIEF_ID, team.getChiefId());
            values.put(COL_TASK_ID, team.getTaskByIndex(i).getTaskId());
            db.insert(TABLE_TEAMS, null, values);
        }
    }

    public void updateTeam(Team team){

        //Log.d(TAG, "update team " + team.getTeamId());
        int size = team.getNumberOfTasks();
        for (int i=0; i < size; i++) {
            //Création d'un ContentValues (fonctionne comme une HashMap)
            ContentValues values = new ContentValues();
            //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
            values.put(COL_TEAM_ID, team.getTeamId());
            values.put(COL_CHIEF_ID, team.getChiefId());
            String taskId = team.getTaskByIndex(i).getTaskId();
            values.put(COL_TASK_ID, taskId);
            db.update(TABLE_TEAMS, values, COL_TASK_ID + " LIKE \"" + taskId + "\" AND " + COL_TEAM_ID + " LIKE \"" + team.getTeamId() + "\"", null);
        }
    }

    public Team getTeamByChiefId(String chiefId){
        //Récupère dans un Cursor les valeurs correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
        Cursor c = db.query(TABLE_TEAMS, new String[] {COL_ID, COL_TEAM_ID, COL_CHIEF_ID, COL_TASK_ID}, COL_CHIEF_ID + " LIKE \"" + chiefId +"\"", null, null, null, null);
        return cursorToTeam(c);
    }

    public Team getTeamByTeamId(String teamId){
        //Récupère dans un Cursor les valeurs correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
        Cursor c = db.query(TABLE_TEAMS, new String[] {COL_ID, COL_TEAM_ID, COL_CHIEF_ID, COL_TASK_ID}, COL_TEAM_ID + " LIKE \"" + teamId +"\"", null, null, null, null);
        return cursorToTeam(c);
    }

    public void displayTable() {
        Log.d(TAG, "display table");
        Cursor c = db.query(TABLE_TEAMS, new String[] {COL_ID, COL_TEAM_ID, COL_CHIEF_ID, COL_TASK_ID}, null, null, null, null, null);
        if (c.getCount() == 0)
            return;
        c.moveToFirst();
        for (int i=0; i < c.getCount(); i++) {
            Log.d(TAG, "id="+c.getString(NUM_COL_ID) +", team_id="+c.getString(NUM_COL_TEAM_ID) +", chief_id="+c.getString(NUM_COL_CHIEF_ID) +", companion_id=" + c.getString(NUM_COL_TASK_ID));
            c.moveToNext();
        }
    }

    public ArrayList<Team> getAllTeams() {
        ArrayList<Team> teams = new ArrayList<>();

        Cursor c = db.query(true, TABLE_TEAMS, new String[] {COL_TEAM_ID}, null, null, null, null, null, null);
        if (c.getCount() == 0)
            return null;

        c.moveToFirst();
        //Log.d(TAG, "getAllTeams count = " + Integer.toString(c.getCount()));
        for (int i=0; i < c.getCount(); i++) {
            Team team = getTeamByTeamId(c.getString(0));
            if (team != null)
                teams.add(team);
            c.moveToNext();
        }

        return teams;
    }

    //Cette méthode permet de convertir un cursor en un livre
    private Team cursorToTeam(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        //On créé un livre
        Team team = new Team();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        //
        team.setId(c.getInt(NUM_COL_ID));
        team.setTeamId(c.getString(NUM_COL_TEAM_ID));
        team.setChiefId(c.getString(NUM_COL_CHIEF_ID));

        TaskDB taskDB = new TaskDB(mContext);
        taskDB.open();
        for (int i=0; i < c.getCount(); i++) {
            Task task = taskDB.getTaskByTaskId(c.getString(NUM_COL_TASK_ID));
            team.addTask(task);
            c.moveToNext();
        }
        taskDB.close();

        //On ferme le cursor
        c.close();

        //On retourne le livre
        return team;
    }
}
