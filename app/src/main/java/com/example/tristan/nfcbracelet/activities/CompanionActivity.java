package com.example.tristan.nfcbracelet.activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.tristan.nfcbracelet.R;
import com.example.tristan.nfcbracelet.adapters.CompanionAdapter;
import com.example.tristan.nfcbracelet.adapters.TaskAdapter;
import com.example.tristan.nfcbracelet.database.HistoryDB;
import com.example.tristan.nfcbracelet.models.Companion;
import com.example.tristan.nfcbracelet.models.Data;
import com.example.tristan.nfcbracelet.models.History;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CompanionActivity extends AppCompatActivity {

    private static final String TAG = "CompanionActivity";

    private Data mData;
    private TaskAdapter adapter;
    private ListView listView;
    private Companion companion;
    private ArrayList<History> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mData = Data.getInstance();

        Bundle bundle = getIntent().getExtras();
        String companion_id = bundle.getString("companionId");
        Log.d(TAG, "companionId = " + companion_id);
        companion = mData.getTeam().getCompanionByUserId(companion_id);
        Log.d(TAG, companion.getFirstName());

        // TODO get only today history
        final HistoryDB historyDB = new HistoryDB(this);
        historyDB.open();
        historyList = historyDB.getAllHistoryByCompanionId(companion_id);
        historyDB.close();

        adapter = new TaskAdapter(this, mData.getTeam().getTasks(), historyList);
        // Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.tasksList);
        listView.setAdapter(adapter);

        TextView companionName = (TextView) findViewById(R.id.companionName);
        companionName.setText(companion.getLastName() + " " + companion.getFirstName());
        TextView jobName = (TextView) findViewById(R.id.jobName);
        jobName.setText(companion.getPosition());

        // debug history table
        Button debugHistory = (Button) findViewById(R.id.debugHistory);
        debugHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyDB.open();
                historyDB.displayTable();
                historyDB.close();
            }
        });
    }

}
