package com.example.tristan.nfcbracelet.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Tristan on 24/03/2016.
 */
public class History {
    private int id;
    private boolean isStarted;
    private String duration;
    private Companion companion;
    private Task task;
    private String date;
    private String lastStart;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Companion getCompanion() {
        return companion;
    }

    public void setCompanion(Companion companion) {
        this.companion = companion;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLastStart() {
        return lastStart;
    }

    public void setLastStart(String lastStart) {
        this.lastStart = lastStart;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    public int isStartedInt() {
        if (isStarted)
            return 1;
        return 0;
    }

    public void setStartedInt(int started) {
        if (started == 1)
            isStarted = true;
        isStarted = false;
    }
}
