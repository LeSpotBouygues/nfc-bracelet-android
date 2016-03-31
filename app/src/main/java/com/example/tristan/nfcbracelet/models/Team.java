package com.example.tristan.nfcbracelet.models;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Tristan on 18/03/2016.
 */
public class Team {
    final private static String TAG = "Team";

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

    public Companion getCompanionByUserId(String id) {
        for (Companion companion : mCompanionsList) {
            Log.d(TAG, "companion_id="+companion.getUserId());
            if (companion.getUserId().equals(id))
                return companion;
        }
        return null;
    }

    public int getNumberOfCompanions() {
        return mCompanionsList.size();
    }

    public ArrayList<Companion> getCompanions() {
        return mCompanionsList;
    }

    public void setCompanions(ArrayList<Companion> companions) {
        mCompanionsList = companions;
    }

    public void addTask(Task task) {
        mTasksList.add(task);
    }

    public Task getTaskByIndex(int id) {
        return mTasksList.get(id);
    }

    public Task getTaskByTaskId(String id) {
        for (Task task : mTasksList) {
            if (task.getTaskId().equals(id))
                return task;
        }
        return null;
    }

    public int getNumberOfTasks() {
        return mTasksList.size();
    }

    public ArrayList<Task> getTasks() {
        return mTasksList;
    }

    public void setTasks(ArrayList<Task> tasks) {
        mTasksList = tasks;
    }
}
