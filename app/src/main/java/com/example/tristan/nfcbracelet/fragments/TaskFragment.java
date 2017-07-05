package com.example.tristan.nfcbracelet.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tristan.nfcbracelet.R;
import com.example.tristan.nfcbracelet.adapters.CompanionAdapter;
import com.example.tristan.nfcbracelet.adapters.TaskCompanionAdapter;
import com.example.tristan.nfcbracelet.database.HistoryDB;
import com.example.tristan.nfcbracelet.models.Companion;
import com.example.tristan.nfcbracelet.models.Data;
import com.example.tristan.nfcbracelet.models.History;
import com.example.tristan.nfcbracelet.models.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TaskFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {

    private static final String TAG = "TaskFragment";

    private Data mData;
    private ArrayList<Companion> companions;
    private TaskCompanionAdapter adapter;
    private ListView listView;
    private Task task;

    public TaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
        mData = Data.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        Bundle bundle = getArguments();
        task = mData.getTeam().getTaskByTaskId(bundle.getString("taskId"));

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);

        // check if companions are assigned to task
        getCompanionsByTask();

        adapter = new TaskCompanionAdapter(getContext(), companions, task);
        // Attach the adapter to a ListView
        listView = (ListView) rootView.findViewById(R.id.taskCompanionsList);
        listView.setAdapter(adapter);

        TextView taskName = (TextView) rootView.findViewById(R.id.taskName);
        taskName.setText(task.getLongName());
        TextView taskCode = (TextView) rootView.findViewById(R.id.taskCode);
        taskCode.setText(task.getCode());

        final Button startAllTasksButton = (Button) rootView.findViewById(R.id.startAllTasks);
        startAllTasksButton.setText("START");
        startAllTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startAllTasksButton.getText() == "START") {
                    startAllTasksButton.setText("STOP");
                    for (int i = 0; i < listView.getCount(); i++) {
                        View view = listView.getChildAt(i);
                        if (view != null) {
                            startTaskForCompanion(adapter.getItem(i), view);
                        }
                    }
                } else if (startAllTasksButton.getText() == "STOP") {
                    startAllTasksButton.setText("START");
                    for (int i = 0; i < listView.getCount(); i++) {
                        View view = listView.getChildAt(i);
                        if (view != null) {
                            stopTaskForCompanion(adapter.getItem(i), view);
                        }
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    private void getCompanionsByTask() {
        companions = new ArrayList<>();
        ArrayList<Companion> teamCompanions = mData.getTeam().getCompanions();
        for (Companion companion : teamCompanions) {
            if (companion.isAssignedToTask(task)) {
                companions.add(companion);
            }
        }
    }

    private void startTaskForCompanion(Companion companion, View view) {
        if (!companion.isPresent()) {
            Toast.makeText(getContext(), companion.getFirstName() + "'s presence hasn't been checked", Toast.LENGTH_LONG).show();
            return;
        }

        // Lookup view for data population
        final ImageView light = (ImageView) view.findViewById(R.id.taskCompanionLight);
        final TextView companionName = (TextView) view.findViewById(R.id.taskCompanion);
        final Button startTaskButton = (Button) view.findViewById(R.id.startTaskCompanion);

        if (startTaskButton.getText() == "STOP") {
            return;
        }

        final String date = DateFormat.format("dd:MM:yyyy", new java.util.Date()).toString();
        final String time = DateFormat.format("HH:mm:ss", new java.util.Date()).toString();

        final HistoryDB historyDB = new HistoryDB(getContext());
        historyDB.open();
        final History history = historyDB.getHistoryByCompanionIdByTaskIdByDate(companion.getUserId(), task.getTaskId(), date);
        historyDB.close();

        // start task
        startTaskButton.setText("STOP");
        light.setImageResource(R.mipmap.green_dot);
        history.setStarted(true);
        history.setLastStart(time);
        history.setDate(date);
        historyDB.open();
        historyDB.updateSingleHistory(history);
        historyDB.close();

    }

    private void stopTaskForCompanion(Companion companion, View view) {
        /*if (!companion.isPresent()) {
            Toast.makeText(getContext(), companion.getFirstName() + "'s presence hasn't been checked", Toast.LENGTH_LONG).show();
            return;
        }*/
        // Lookup view for data population
        final ImageView light = (ImageView) view.findViewById(R.id.taskCompanionLight);
        final TextView companionName = (TextView) view.findViewById(R.id.taskCompanion);
        final Button startTaskButton = (Button) view.findViewById(R.id.startTaskCompanion);

        if (startTaskButton.getText() == "START") {
            return;
        }

        final String date = DateFormat.format("dd:MM:yyyy", new java.util.Date()).toString();
        final String time = DateFormat.format("HH:mm:ss", new java.util.Date()).toString();

        final HistoryDB historyDB = new HistoryDB(getContext());
        historyDB.open();
        final History history = historyDB.getHistoryByCompanionIdByTaskIdByDate(companion.getUserId(), task.getTaskId(), date);
        historyDB.close();

        // stop task
        startTaskButton.setText("START");
        light.setImageResource(R.mipmap.reddot);
        history.setStarted(false);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            java.util.Date lastStart = sdf.parse(history.getLastStart());
            //Log.d(TAG, "lastStart = "+history.getLastStart());
            long lastStartLong = lastStart.getTime();
            //Log.d(TAG, "lastStartLong = " + String.valueOf(lastStartLong));
            String now = DateFormat.format("HH:mm:ss", new java.util.Date()).toString();
            long nowLong = sdf.parse(now).getTime();
            //Log.d(TAG, "now = " + sdf.parse(now));
            //Log.d(TAG, "nowLong = " + String.valueOf(nowLong));
            String duration = Long.toString((nowLong - lastStartLong)/60000 + Integer.parseInt(history.getDuration()));
            //Log.d(TAG, "duration = "+duration);
            history.setDuration(duration);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        historyDB.open();
        historyDB.updateSingleHistory(history);
        historyDB.close();

    }
}
