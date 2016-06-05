package com.example.tristan.nfcbracelet.models;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

/**
 * Created by Tristan on 30/03/2016.
 */
public class Data {
    private Team mTeam;

    private static Data ourInstance = new Data();

    public static Data getInstance() {
        return ourInstance;
    }

    private Data() {
    }

    public void setTeam(Team teamCompanions, Team teamTasks) {
        mTeam = new Team();
        mTeam.setId(-1);
        mTeam.setTeamId(teamCompanions.getTeamId());
        mTeam.setChiefId(teamCompanions.getChiefId());
        mTeam.setCompanions(teamCompanions.getCompanions());
        if (teamTasks != null)
            mTeam.setTasks(teamTasks.getTasks());
        else
            mTeam.setTasks(new ArrayList<Task>());
    }

    public Team getTeam() {
        return mTeam;
    }

}
