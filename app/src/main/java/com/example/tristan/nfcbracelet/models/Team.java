package com.example.tristan.nfcbracelet.models;

import com.example.tristan.nfcbracelet.utils.RealmString;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Tristan on 18/03/2016.
 */
public class Team extends RealmObject {
    @PrimaryKey
    private String id;
    private String chiefId;
    private RealmList<RealmString> companions;
    private RealmList<RealmString> tasks;
    //private ArrayList<Companion> mCompanionsList;

    public Team() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChiefId() {
        return chiefId;
    }

    public void setChiefId(String chiefId) {
        this.chiefId = chiefId;
    }

    public RealmList<RealmString> getCompanions() {
        return companions;
    }

    public void setCompanions(RealmList<RealmString> companions) {
        this.companions = companions;
    }

    public RealmList<RealmString> getTasks() {
        return tasks;
    }

    public void setTasks(RealmList<RealmString> tasks) {
        this.tasks = tasks;
    }
}
