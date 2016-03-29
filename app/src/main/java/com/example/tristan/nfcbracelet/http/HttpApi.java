package com.example.tristan.nfcbracelet.http;

import okhttp3.OkHttpClient;

/**
 * Created by Tristan on 29/03/2016.
 */
public class HttpApi {
    public final String API_ADDRESS =  "http://54.173.237.75:3000";
    public final String COMPANIONS_ROUTE = "/companions";
    public final String TASKS_ROUTE = "/tasks";
    public final String TEAMS_ROUTE = "/teams";

    private OkHttpClient mOkHttpClient;


    private static HttpApi ourInstance = new HttpApi();

    public static HttpApi getInstance() {
        return ourInstance;
    }

    private HttpApi() {
        mOkHttpClient = new OkHttpClient();
    }

    public OkHttpClient getClient() {
        return mOkHttpClient;
    }
}
