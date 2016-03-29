package com.example.tristan.nfcbracelet.utils;

import io.realm.RealmObject;

/**
 * Created by Tristan on 24/03/2016.
 */
public class RealmString extends RealmObject {

    private String stringValue;

    public RealmString(){}

    public RealmString(String stringValue){
        this.stringValue =  stringValue;
    }


    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

}
