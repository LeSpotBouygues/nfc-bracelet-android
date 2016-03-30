package com.example.tristan.nfcbracelet.models;

import com.example.tristan.nfcbracelet.utils.RealmString;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Tristan on 18/03/2016.
 */
public class Team {
    private int id;
    private String teamId;
    private String chiefId;
    private Companion chief;
    private ArrayList<Companion> mCompanionsList;
    private ArrayList<Task> mTasksList;

    public Team() {
        mCompanionsList = new ArrayList<>();
        mTasksList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String id) {
        teamId = id;
    }

    public String getChiefId() {
        return chiefId;
    }

    public void setChiefId(String chiefId) {
        this.chiefId = chiefId;
    }

    public void setChief(Companion companion) {
        chief = companion;
    }

    public Companion getChief() {
        return chief;
    }

    public void addCompanion(Companion companion) {
        mCompanionsList.add(companion);
    }

    public Companion getCompanionByIndex(int id) {
        return mCompanionsList.get(id);
    }

    public int getNumberOfCompanions() {
        return mCompanionsList.size();
    }

    public ArrayList<Companion> getCompanions() {
        return mCompanionsList;
    }

    public void addTask(Task task) {
        mTasksList.add(task);
    }

    public Task getTaskByIndex(int id) {
        return mTasksList.get(id);
    }

    public int getNumberOfTasks() {
        return mTasksList.size();
    }

    public ArrayList<Task> getTasks() {
        return mTasksList;
    }
}
