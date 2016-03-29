package com.example.tristan.nfcbracelet.models;

import com.example.tristan.nfcbracelet.utils.RealmString;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Tristan on 18/03/2016.
 */
public class Companion extends RealmObject {

    @PrimaryKey
    private String _id;

    private String firstName;
    private String lastName;
    private String aliasName;
    private String position;
    //private Array<String> tasksInProgress;
    private boolean chief;
    private String braceletId;

    private RealmList<Task> tasksInProgress;

    public Companion() {

    }

    public String getBraceletId() {
        return braceletId;
    }

    public void setBraceletId(String braceletId) {
        this.braceletId = braceletId;
    }

    public RealmList<Task> getTasksInProgress() {
        return tasksInProgress;
    }

    public void setTasksInProgress(RealmList<Task> tasksInProgress) {
        this.tasksInProgress = tasksInProgress;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String get_id() {

        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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
