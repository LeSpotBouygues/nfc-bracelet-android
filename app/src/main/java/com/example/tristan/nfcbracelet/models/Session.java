package com.example.tristan.nfcbracelet.models;

/**
 * Created by Tristan on 26/04/2016.
 */
public class Session {
    private Companion mUser;

    private static Session ourInstance = new Session();

    public static Session getInstance() {
        return ourInstance;
    }

    private Session() {
    }

    public Companion getUser() {
        return mUser;
    }

    public void setUser(Companion companion) {
        mUser = companion;
    }
}
