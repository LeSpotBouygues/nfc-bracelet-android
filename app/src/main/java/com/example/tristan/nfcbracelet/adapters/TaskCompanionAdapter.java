package com.example.tristan.nfcbracelet.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tristan.nfcbracelet.R;
import com.example.tristan.nfcbracelet.activities.CompanionActivity;
import com.example.tristan.nfcbracelet.database.HistoryDB;
import com.example.tristan.nfcbracelet.models.Companion;
import com.example.tristan.nfcbracelet.models.History;
import com.example.tristan.nfcbracelet.models.Task;
import com.example.tristan.nfcbracelet.utils.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Tristan on 30/03/2016.
 */
public class TaskCompanionAdapter extends ArrayAdapter<Companion> {

    private static final String TAG = "TaskCompanionAdapter";
    private Context mContext;
    private Task mTask;


    public TaskCompanionAdapter(Context context, ArrayList<Companion> companions, Task task) {
        super(context, 0, companions);

        mContext = context;
        mTask = task;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Companion companion = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task_companion, parent, false);
        }

        final String date = DateFormat.format("dd:MM:yyyy", new java.util.Date()).toString();
        final String time = DateFormat.format("HH:mm:ss", new java.util.Date()).toString();

        final HistoryDB historyDB = new HistoryDB(mContext);
        historyDB.open();
        final History history = historyDB.getHistoryByCompanionIdByTaskIdByDate(companion.getUserId(), mTask.getTaskId(), date);
        historyDB.close();

        // Lookup view for data population
        final ImageView light = (ImageView) convertView.findViewById(R.id.taskCompanionLight);
        final TextView companionName = (TextView) convertView.findViewById(R.id.taskCompanion);
        final Button startTaskButton = (Button) convertView.findViewById(R.id.startTaskCompanion);
        startTaskButton.setText("START");

        if (history.isStarted()) {
            //Log.d(TAG, "HISTORY STARTED");
            startTaskButton.setText("STOP");
            light.setImageResource(R.mipmap.green_dot);
        }

        // Populate the data into the template view using the data object
        companionName.setText(companion.getLastName() + " " + companion.getFirstName());
        companionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (companion.isPresent()) {
                    Intent intent = new Intent(getContext(), CompanionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("companionId", companion.getUserId());
                    intent.putExtras(bundle);
                    getContext().startActivity(intent);
                } else {
                        Toast.makeText(getContext(), companionName.getText() + "'s presence hasn't been checked", Toast.LENGTH_LONG).show();
                    }
                }
        });
        startTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (!companion.isPresent()) {
                    Toast.makeText(getContext(), companionName.getText() + "'s presence hasn't been checked", Toast.LENGTH_LONG).show();
                    return;
                }*/

                if (startTaskButton.getText() == "START") {
                    if (!companion.isPresent()) {
                        Toast.makeText(getContext(), companionName.getText() + "'s presence hasn't been checked", Toast.LENGTH_LONG).show();
                        return;
                    }
                    startTaskButton.setText("STOP");
                    light.setImageResource(R.mipmap.green_dot);
                    history.setStarted(true);
                    history.setLastStart(time);
                    history.setDate(date);
                    historyDB.open();
                    historyDB.updateSingleHistory(history);
                    historyDB.close();
                }
                else if (startTaskButton.getText() == "STOP") {
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
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
