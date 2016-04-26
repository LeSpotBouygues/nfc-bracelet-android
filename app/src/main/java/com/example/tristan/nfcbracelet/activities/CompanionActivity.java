package com.example.tristan.nfcbracelet.activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.example.tristan.nfcbracelet.adapters.TaskCheckAdapter;
import com.example.tristan.nfcbracelet.database.CompanionDB;
import com.example.tristan.nfcbracelet.database.CompanionTasksDB;
import com.example.tristan.nfcbracelet.database.HistoryDB;
import com.example.tristan.nfcbracelet.database.TaskDB;
import com.example.tristan.nfcbracelet.fragments.CompanionsFragment;
import com.example.tristan.nfcbracelet.models.Companion;
import com.example.tristan.nfcbracelet.models.Data;
import com.example.tristan.nfcbracelet.models.History;
import com.example.tristan.nfcbracelet.models.Task;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CompanionActivity extends AppCompatActivity {

    private static final String TAG = "CompanionActivity";

    private Data mData;
    private Context mContext;
    private TaskAdapter adapter;
    private TaskCheckAdapter taskCheckAdapter;
    private ListView listView;
    private Companion companion;
    private ArrayList<Task> teamTasks;
    private CompanionDB companionDB;
    private CompanionTasksDB companionTasksDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mData = Data.getInstance();
        mContext = this;

        companionDB = new CompanionDB(this);
        companionTasksDB = new CompanionTasksDB(this);

        teamTasks = mData.getTeam().getTasks();

        Bundle bundle = getIntent().getExtras();
        final String companion_id = bundle.getString("companionId");
        //Log.d(TAG, "companionId = " + companion_id);
        //companion = mData.getTeam().getCompanionByUserId(companion_id);
        companionDB.open();
        companion = companionDB.getCompanionByUserId(companion_id);
        companionDB.close();
        /*Log.d(TAG, companion.getFirstName());

        Log.d(TAG, "Number of team tasks : "+ teamTasks.size());
        Log.d(TAG, "Number of tasks in progress : " + companion.getTasksInProgress().size());*/

        //companion.displayTasks();

        new loadUI().execute();

        TextView companionName = (TextView) findViewById(R.id.companionName);
        companionName.setText(companion.getLastName() + " " + companion.getFirstName());
        TextView jobName = (TextView) findViewById(R.id.jobName);
        jobName.setText(companion.getPosition());

        final Button editCompanionButton = (Button) findViewById(R.id.editCompanionButton);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_tasks);

        final ListView tasksCheckList = (ListView) dialog.findViewById(R.id.tasksCheckList);
        taskCheckAdapter = new TaskCheckAdapter(this, teamTasks, companion.getTasksInProgress());
        // Attach the adapter to a ListView
        tasksCheckList.setAdapter(taskCheckAdapter);

        final Button assignateButton = (Button) dialog.findViewById(R.id.assignateButton);
        assignateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "number of tasks to assign : " + tasksCheckList.getCount());
                //Log.d(TAG, "number of tasks to assign (child count) : " + tasksCheckList.getChildCount());

                /*companionTasksDB.open();
                companionTasksDB.displayTable();
                companionTasksDB.close();*/

                adapter.clear();
                companionTasksDB.open();
                companionTasksDB.deleteAllTasksForCompanion(companion);
                for (int i=0; i < tasksCheckList.getCount(); i++) {
                    Task taskToAssign = taskCheckAdapter.getItem(i);
                    //Log.d(TAG, "Task : "+taskToAssign.getLongName());
                    View childView = tasksCheckList.getChildAt(i);
                    if (childView != null) {
                        Log.d(TAG, childView.toString());
                        CheckBox checkBox = (CheckBox) childView.findViewById(R.id.checkTask);
                        if (checkBox.isChecked()) {
                            if (companionTasksDB.getSingleTaskByCompanionId(companion.getUserId(), taskToAssign.getTaskId()) == null)
                                companionTasksDB.insertTaskForCompanion(companion, taskToAssign);
                            else
                                companionTasksDB.updateTaskForCompanion(companion, taskToAssign);
                            adapter.add(taskToAssign);
                        }
                    }
                    else {
                        Log.d(TAG, "childview null");
                    }
                }
                companionTasksDB.close();

                adapter.notifyDataSetChanged();

                /*companionTasksDB.open();
                companionTasksDB.displayTable();
                companionTasksDB.close();*/

                dialog.dismiss();
            }
        });

        final Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        editCompanionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        // debug history table
        /*Button debugHistory = (Button) findViewById(R.id.debugHistory);
        final HistoryDB historyDB = new HistoryDB(this);
        debugHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyDB.open();
                historyDB.displayTable();
                historyDB.close();
            }
        });*/
    }

    private class loadUI extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            adapter = new TaskAdapter(mContext, companion.getTasksInProgress(), companion);
            // Attach the adapter to a ListView
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView = (ListView) findViewById(R.id.tasksList);
                    listView.setAdapter(adapter);
                }
            });

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }


}
