package com.example.tristan.nfcbracelet.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tristan.nfcbracelet.R;
import com.example.tristan.nfcbracelet.database.CompanionDB;
import com.example.tristan.nfcbracelet.database.CompanionTasksDB;
import com.example.tristan.nfcbracelet.database.TaskDB;
import com.example.tristan.nfcbracelet.database.TeamCompanionDB;
import com.example.tristan.nfcbracelet.database.TeamTaskDB;
import com.example.tristan.nfcbracelet.http.HttpApi;
import com.example.tristan.nfcbracelet.models.Companion;
import com.example.tristan.nfcbracelet.models.Data;
import com.example.tristan.nfcbracelet.models.Task;
import com.example.tristan.nfcbracelet.models.Team;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";

    private TextView mTextView;
    private ProgressBarCircularIndeterminate spinner;

    private HttpApi httpApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        httpApi = HttpApi.getInstance();

        mTextView = (TextView) findViewById(R.id.textView_explanation);
        spinner = (ProgressBarCircularIndeterminate) findViewById(R.id.spinner1);

        //new loadDB().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void goToProfile() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void fillDatabase() {
        getTasksFromServer();
    }

    // GET DATA FROM SERVER

    private void getTasksFromServer() {

        // get all tasks
        Request requestTasks = new Request.Builder()
                .url(httpApi.API_ADDRESS + httpApi.TASKS_ROUTE)
                .build();
        httpApi.getClient().newCall(requestTasks).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String responseString = response.body().string();
                    Log.d("GET TASKS", responseString);
                    fillTasksInDB(responseString);
                    getCompanionsFromServer();
                }
            }
        });
    }

    private void getCompanionsFromServer() {

        // get all companions
        Request requestCompanions = new Request.Builder()
                .url(httpApi.API_ADDRESS + httpApi.COMPANIONS_ROUTE)
                .build();
        httpApi.getClient().newCall(requestCompanions).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String responseString = response.body().string();
                    Log.d("GET COMPANIONS", responseString);
                    fillCompanionsInDB(responseString);
                    getTeamsFromServer();
                }
            }
        });
    }

    private void getTeamsFromServer() {

        // get all teams
        Request requestTeams = new Request.Builder()
                .url(httpApi.API_ADDRESS + httpApi.TEAMS_ROUTE)
                .build();
        httpApi.getClient().newCall(requestTeams).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String responseString = response.body().string();
                    Log.d("GET TEAMS", responseString);
                    fillTeamsInDB(responseString);
                }
            }
        });
    }

    // FILL LOCAL DB WITH SERVER DATA

    private void fillTasksInDB(String response) {

        try {
            JSONArray jsonResponse = new JSONArray(response);
            TaskDB taskDB = new TaskDB(this);

            for (int i=0; i < jsonResponse.length(); i++) {
                JSONObject jsonObject = jsonResponse.getJSONObject(i);
                Task task = new Task();
                task.setTaskId(jsonObject.getString("_id"));
                task.setShortName(jsonObject.getString("label_short"));
                task.setLongName(jsonObject.getString("label_long"));
                task.setCode(jsonObject.getString("code"));

                taskDB.open();
                taskDB.insertTask(task);
                taskDB.close();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillCompanionsInDB(String response) {
        try {
            JSONArray jsonResponse = new JSONArray(response);
            CompanionDB companionDB = new CompanionDB(this);

            for (int i=0; i < jsonResponse.length(); i++) {
                JSONObject jsonObject = jsonResponse.getJSONObject(i);
                Companion companion = new Companion();
                companion.setUserId(jsonObject.getString("_id"));
                companion.setFirstName(jsonObject.getString("firstName"));
                companion.setLastName(jsonObject.getString("lastName"));
                if (jsonObject.has("position"))
                    companion.setPosition(jsonObject.getString("position"));
                else
                    companion.setPosition("");
                if (jsonObject.has("idBracelet"))
                    companion.setBraceletId(jsonObject.getString("idBracelet"));
                else
                    companion.setBraceletId("");
                companion.setChief(jsonObject.getBoolean("chief"));
                companion.setPresence(false);
                //companion.setAliasName(jsonObject.getString("aliasName"));
                CompanionTasksDB companionTasksDB = new CompanionTasksDB(this);
                TaskDB taskDB = new TaskDB(this);
                JSONArray tasks = jsonObject.getJSONArray("tasksInProgress");
                for (int k=0; k < tasks.length(); k++) {
                    taskDB.open();
                    Task task = taskDB.getTaskByTaskId(tasks.getJSONObject(k).getString("_id"));
                    taskDB.close();
                    companionTasksDB.open();
                    if (companionTasksDB.getSingleTaskByCompanionId(companion.getUserId(), task.getTaskId()) == null)
                        companionTasksDB.insertTaskForCompanion(companion, task);
                    else
                        companionTasksDB.updateTaskForCompanion(companion, task);
                    taskDB.close();
                }

                companionDB.open();
                if (companionDB.getCompanionByUserId(companion.getUserId()) == null)
                    companionDB.insertCompanion(companion);
                else
                    companionDB.updateCompanion(companion.getUserId(), companion);
                companionDB.close();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void fillTeamsInDB(String response) {
        Log.d(TAG, "fillTeamsInDB");
        try {
            JSONArray jsonResponse = new JSONArray(response);

            TeamCompanionDB teamCompanionDB = new TeamCompanionDB(this);
            TeamTaskDB teamTaskDB = new TeamTaskDB(this);
            CompanionDB  companionDB = new CompanionDB(this);
            TaskDB taskDB = new TaskDB(this);

            for (int i=0; i < jsonResponse.length(); i++) {
                //Log.d(TAG, "Team "+ Integer.toString(i));
                JSONObject jsonObject = jsonResponse.getJSONObject(i);
                Team team = new Team();
                team.setTeamId(jsonObject.getString("_id"));
                team.setChiefId(jsonObject.getJSONObject("chief").getString("_id"));
                JSONArray companions = jsonObject.getJSONArray("companions");
                for (int j=0; j < companions.length(); j++) {
                    companionDB.open();
                    Companion companion = companionDB.getCompanionByUserId(companions.getJSONObject(j).getString("_id"));
                    team.addCompanion(companion);
                    //Log.d(TAG, "add companion "+ companion.getUserId());
                    companionDB.close();
                }
                JSONArray tasks = jsonObject.getJSONArray("tasks");
                for (int k=0; k < tasks.length(); k++) {
                    taskDB.open();
                    Task task = taskDB.getTaskByTaskId(tasks.getJSONObject(k).getString("_id"));
                    team.addTask(task);
                    //Log.d(TAG, "add task "+ task.getTaskId());
                    taskDB.close();
                }
                //Log.d(TAG, "team "+ team.getTeamId() + " precreated");
                teamCompanionDB.open();
                if (teamCompanionDB.getTeamByTeamId(team.getTeamId()) == null)
                    teamCompanionDB.insertTeam(team);
                else
                    teamCompanionDB.updateTeam(team);
                teamCompanionDB.close();
                teamTaskDB.open();
                if (teamTaskDB.getTeamByTeamId(team.getTeamId()) == null)
                    teamTaskDB.insertTeam(team);
                else
                    teamTaskDB.updateTeam(team);
                teamTaskDB.close();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class loadDB extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            fillDatabase();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    spinner.setVisibility(View.GONE);
                }
            });
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
