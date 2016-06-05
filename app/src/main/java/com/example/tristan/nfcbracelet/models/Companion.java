package com.example.tristan.nfcbracelet.models;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Tristan on 18/03/2016.
 */
public class Companion {
    private static final String TAG = "Companion";

    private int id;
    private String userId;
    private String firstName;
    private String lastName;
    private String aliasName;
    private String position;
    //private Array<String> tasksInProgress;
    private boolean chief;
    private boolean presence;
    private String braceletId;

    private ArrayList<Task> tasksInProgress;

    public Companion() {
        tasksInProgress = new ArrayList<>();

    }

    public String getBraceletId() {
        return braceletId;
    }

    public void setBraceletId(String braceletId) {
        this.braceletId = braceletId;
    }

    public ArrayList<Task> getTasksInProgress() {
        return tasksInProgress;
    }

    public void setTasksInProgress(ArrayList<Task> tasksInProgress) {
        this.tasksInProgress = tasksInProgress;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public boolean isChief() {
        return chief;
    }

    public void setChief(boolean chief) {
        this.chief = chief;
    }

    public int getChiefInt() {
        if (chief)
            return 1;
        return 0;
    }

    public void setChiefInt(int chiefInt) {
        if (chiefInt == 1) {
            chief = true;
        }
        else {
            chief = false;
        }
    }

    public boolean isPresent() {
        return presence;
    }

    public void setPresence(boolean presence) {
        this.presence = presence;
    }

    public int getPresenceInt() {
        if (presence)
            return 1;
        return 0;
    }

    public void setPresenceInt(int presenceInt) {
        if (presenceInt == 1) {
            presence = true;
        }
        else {
            presence = false;
        }

    }

    public void displayTasks() {
        Log.d(TAG, firstName +", tasks : ");
        for (Task task : tasksInProgress) {
            Log.d(TAG, task.getLongName());
        }
    }
}
