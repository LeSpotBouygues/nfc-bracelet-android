package com.example.tristan.nfcbracelet.http;

import okhttp3.OkHttpClient;

/**
 * Created by Tristan on 29/03/2016.
 */
public class HttpApi {
    public final String API_ADDRESS =  " http://cop78.lespot-bouygues.com:3000";
    public final String COMPANIONS_ROUTE = "/companions";
    public final String TASKS_ROUTE = "/tasks";
    public final String TEAMS_ROUTE = "/teams";
    public final String HISTORY_ROUTE = "/history";

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
