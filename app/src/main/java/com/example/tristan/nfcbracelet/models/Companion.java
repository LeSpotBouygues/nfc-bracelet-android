package com.example.tristan.nfcbracelet.models;

import java.util.ArrayList;

/**
 * Created by Tristan on 18/03/2016.
 */
public class Companion {

    private int id;
    private String userId;
    private String firstName;
    private String lastName;
    private String aliasName;
    private String position;
    //private Array<String> tasksInProgress;
    private boolean chief;
    private String braceletId;

    private ArrayList<Task> tasksInProgress;

    public Companion() {

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
}
