package com.example.tristan.nfcbracelet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.tristan.nfcbracelet.models.Companion;

import java.util.ArrayList;

/**
 * Created by Tristan on 29/03/2016.
 */
public class CompanionDB {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "COP78.db";

    private static final String TABLE_COMPANIONS = "companions_table";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_USER_ID = "user_id";
    private static final int NUM_COL_USER_ID = 1;
    private static final String COL_FIRSTNAME = "firstname";
    private static final int NUM_COL_FIRSTNAME = 2;
    private static final String COL_LASTNAME = "lastname";
    private static final int NUM_COL_LASTNAME = 3;
    private static final String COL_POSITION = "position";
    private static final int NUM_COL_POSITION = 4;
    private static final String COL_BRACELET_ID = "bracelet_id";
    private static final int NUM_COL_BRACELET_ID = 5;
    private static final String COL_CHIEF = "chief";
    private static final int NUM_COL_CHIEF = 6;

    private SQLiteDatabase db;

    private DBHelper DBHelper;

    public CompanionDB(Context context){
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

    public long insertCompanion(Companion companion){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_USER_ID, companion.getUserId());
        values.put(COL_FIRSTNAME, companion.getFirstName());
        values.put(COL_LASTNAME, companion.getLastName());
        values.put(COL_POSITION, companion.getPosition());
        values.put(COL_BRACELET_ID, companion.getBraceletId());

        //on insère l'objet dans la BDD via le ContentValues
        return db.insert(TABLE_COMPANIONS, null, values);
    }

    public int updateCompanion(String userId, Companion companion){
        //La mise à jour d'un livre dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simplement préciser quel livre on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_FIRSTNAME, companion.getFirstName());
        values.put(COL_LASTNAME, companion.getLastName());
        values.put(COL_POSITION, companion.getPosition());
        values.put(COL_BRACELET_ID, companion.getBraceletId());
        return db.update(TABLE_COMPANIONS, values, COL_USER_ID  + " LIKE \"" + userId +"\"", null);
    }

    public int deleteCompanionById(int id){
        //Suppression d'un livre de la BDD grâce à l'ID
        return db.delete(TABLE_COMPANIONS, COL_ID + " = " + id, null);
    }

    public Companion getCompanionByBraceletId(String braceletId){
        //Récupère dans un Cursor les valeurs correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
        Cursor c = db.query(TABLE_COMPANIONS, new String[] {COL_ID, COL_USER_ID, COL_FIRSTNAME, COL_LASTNAME, COL_POSITION, COL_BRACELET_ID}, COL_BRACELET_ID + " LIKE \"" + braceletId +"\"", null, null, null, null);
        return cursorToCompanion(c);
    }

    public Companion getCompanionByUserId(String userId) {
        Cursor c = db.query(TABLE_COMPANIONS, new String[] {COL_ID, COL_USER_ID, COL_FIRSTNAME, COL_LASTNAME, COL_POSITION, COL_BRACELET_ID}, COL_USER_ID + " LIKE \"" + userId +"\"", null, null, null, null);
        return cursorToCompanion(c);
    }

    public ArrayList<Companion> getAllCompanions() {
        ArrayList<Companion> companions = new ArrayList<>();

        Cursor c = db.query(TABLE_COMPANIONS, new String[] {COL_ID, COL_USER_ID, COL_FIRSTNAME, COL_LASTNAME, COL_POSITION, COL_BRACELET_ID}, null, null, null, null, null);
        if (c.getCount() == 0)
            return null;

        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {
            Companion companion = new Companion();
            companion.setId(c.getInt(NUM_COL_ID));
            companion.setUserId(c.getString(NUM_COL_USER_ID));
            companion.setFirstName(c.getString(NUM_COL_FIRSTNAME));
            companion.setLastName(c.getString(NUM_COL_LASTNAME));
            companion.setPosition(c.getString(NUM_COL_POSITION));
            companion.setBraceletId(c.getString(NUM_COL_BRACELET_ID));
            companions.add(companion);
            c.moveToNext();
        }

        return companions;
    }

    //Cette méthode permet de convertir un cursor en un livre
    private Companion cursorToCompanion(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        //On créé un livre
        Companion companion = new Companion();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        //
        companion.setId(c.getInt(NUM_COL_ID));
        companion.setUserId(c.getString(NUM_COL_USER_ID));
        companion.setFirstName(c.getString(NUM_COL_FIRSTNAME));
        companion.setLastName(c.getString(NUM_COL_LASTNAME));
        companion.setPosition(c.getString(NUM_COL_POSITION));
        companion.setBraceletId(c.getString(NUM_COL_BRACELET_ID));

        //On ferme le cursor
        c.close();

        //On retourne le livre
        return companion;
    }
}
