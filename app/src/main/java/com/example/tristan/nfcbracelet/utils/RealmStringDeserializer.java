package com.example.tristan.nfcbracelet.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import io.realm.RealmList;

/**
 * Created by Tristan on 24/03/2016.
 */
public class RealmStringDeserializer implements
        JsonDeserializer<RealmList<RealmString>> {

    @Override
    public RealmList<RealmString> deserialize(JsonElement json, Type typeOfT,
                                              JsonDeserializationContext context) throws JsonParseException {

        RealmList<RealmString> realmStrings = new RealmList<>();
        JsonArray stringList = json.getAsJsonArray();

        for (JsonElement stringElement : stringList) {
            realmStrings.add(new RealmString(stringElement.getAsString()));
        }

        return realmStrings;
    }
}
