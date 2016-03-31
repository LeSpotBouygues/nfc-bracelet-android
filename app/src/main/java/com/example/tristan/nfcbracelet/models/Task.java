package com.example.tristan.nfcbracelet.models;

/**
 * Created by Tristan on 18/03/2016.
 */
public class Task {
    private int id;
    private String taskId;
    private String shortName;
    private String longName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public Task() {

    }
}
