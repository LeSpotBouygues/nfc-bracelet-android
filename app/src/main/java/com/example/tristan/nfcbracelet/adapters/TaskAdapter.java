package com.example.tristan.nfcbracelet.adapters;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tristan.nfcbracelet.R;
import com.example.tristan.nfcbracelet.activities.CompanionActivity;
import com.example.tristan.nfcbracelet.database.HistoryDB;
import com.example.tristan.nfcbracelet.models.Companion;
import com.example.tristan.nfcbracelet.models.History;
import com.example.tristan.nfcbracelet.models.Task;
import com.example.tristan.nfcbracelet.utils.Date;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.Slider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Tristan on 31/03/2016.
 */
public class TaskAdapter extends ArrayAdapter<Task> {
    private static final String TAG = "TaskAdapter";

    private HistoryDB historyDB;
    private Companion mCompanion;

    public TaskAdapter(Context context, ArrayList<Task> tasks, Companion companion) {
        super(context, 0, tasks);

        historyDB = new HistoryDB(context);
        mCompanion = companion;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Task task = getItem(position);

        final String date = DateFormat.format("dd:MM:yyyy", new java.util.Date()).toString();
        final String time = DateFormat.format("HH:mm:ss", new java.util.Date()).toString();

        historyDB.open();
        final History history = historyDB.getHistoryByCompanionIdByTaskIdByDate(mCompanion.getUserId(), task.getTaskId(), date);
        historyDB.close();

        /*Log.d(TAG, "HISTORY");
        Log.d(TAG, "companion_id="+history.getCompanion().getUserId()
                +", task_name="+history.getTask().getLongName()
                +", duration="+history.getDuration()
                +", date="+history.getDate()
                +", started="+(history.isStarted() ? "1" : "0")
                +", last_start="+history.getLastStart());*/

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }

        // Lookup view for data population
        final TextView taskName = (TextView) convertView.findViewById(R.id.taskName);
        final ImageView light = (ImageView) convertView.findViewById(R.id.taskLight);
        final Button startTaskButton = (Button) convertView.findViewById(R.id.startTask);
        final Chronometer chronometer = (Chronometer) convertView.findViewById(R.id.chrono);
        final ButtonFlat editButton = (ButtonFlat) convertView.findViewById(R.id.editButton);

        // Populate the data into the template view using the data object
        taskName.setText(task.getLongName());
        startTaskButton.setText("START");

        if (history != null && history.isStarted()) {
            //Log.d(TAG, "HISTORY STARTED");
            startTaskButton.setText("STOP");
            light.setImageResource(R.mipmap.green_dot);

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                java.util.Date lastStart = sdf.parse(history.getLastStart());
                long lastStartLong = lastStart.getTime();
                //Log.d(TAG, "lastStartLong = " + String.valueOf(lastStartLong));
                String now = DateFormat.format("HH:mm:ss", new java.util.Date()).toString();
                long nowLong = sdf.parse(now).getTime();
                //Log.d(TAG, "nowLong = " + String.valueOf(nowLong));
                //Log.d(TAG, "elapsedRealtime = " + String.valueOf(SystemClock.elapsedRealtime()));
                long chronoValue = SystemClock.elapsedRealtime() - (nowLong - lastStartLong + Integer.parseInt(history.getDuration()) * 60000);
                chronometer.setBase(chronoValue);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            chronometer.start();

        } else {
            //Log.d(TAG, "HISTORY STOPPED");

            /*if (history.getLastStart() != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    java.util.Date lastStart = sdf.parse(history.getLastStart());
                    long lastStartLong = lastStart.getTime();
                    Log.d(TAG, "lastStartLong = " + String.valueOf(lastStartLong));
                    String now = DateFormat.format("HH:mm:ss", new java.util.Date()).toString();
                    long nowLong = sdf.parse(now).getTime();
                    Log.d(TAG, "nowLong = " + String.valueOf(nowLong));
                    Log.d(TAG, "elapsedRealtime = " + String.valueOf(SystemClock.elapsedRealtime()));
                    long chronoValue = SystemClock.elapsedRealtime() - (nowLong - lastStartLong + Integer.parseInt(history.getDuration()) * 60000);
                    chronometer.setBase(chronoValue);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }*/
        }

        if (history == null) {
            return convertView;
        }

        startTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startTaskButton.getText().equals("START")) {
                    startTaskButton.setText("STOP");
                    light.setImageResource(R.mipmap.green_dot);
                    chronometer.setBase(SystemClock.elapsedRealtime() - (Integer.parseInt(history.getDuration()) * 60000));
                    chronometer.start();

                    history.setStarted(true);
                    history.setLastStart(time);
                    history.setDate(date);
                    historyDB.open();
                    historyDB.updateSingleHistory(history);
                    historyDB.close();
                } else if (startTaskButton.getText().equals("STOP")) {
                    startTaskButton.setText("START");
                    light.setImageResource(R.mipmap.reddot);
                    chronometer.stop();
                    history.setStarted(false);
                    long timeElapsed = SystemClock.elapsedRealtime() - chronometer.getBase();
                    final int hours = (int) (timeElapsed / 3600000);
                    final int minutes = (int) (timeElapsed - hours * 3600000) / 60000;
                    //Log.d(TAG, "hours = " + Integer.toString(hours));
                    //Log.d(TAG, "minutes = " + Integer.toString(minutes));
                    String duration = Integer.toString((hours * 60 + minutes));
                    //Log.d(TAG, "duration = "+duration);
                    history.setDuration(duration);
                    historyDB.open();
                    historyDB.updateSingleHistory(history);
                    historyDB.close();
                }
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // stop chrono if it's running
                if (startTaskButton.getText().equals("STOP")) {
                    startTaskButton.setText("START");
                    light.setImageResource(R.mipmap.reddot);
                    chronometer.stop();

                    history.setStarted(false);
                    history.setLastStart(time);
                    history.setDate(date);
                    historyDB.open();
                    historyDB.updateSingleHistory(history);
                    historyDB.close();
                }

                // Process to get Current Time
                long timeElapsed = SystemClock.elapsedRealtime() - chronometer.getBase();
                final int hours = (int) (timeElapsed / 3600000);
                final int minutes = (int) (timeElapsed - hours * 3600000) / 60000;
                //Log.d(TAG, "hours = "+Integer.toString(hours));
                //Log.d(TAG, "minutes = "+Integer.toString(minutes));

                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_date);

                final Slider sliderHours = (Slider) dialog.findViewById(R.id.sliderHours);
                sliderHours.setValue(hours);
                final TextView hoursValue = (TextView) dialog.findViewById(R.id.hoursValue);
                hoursValue.setText(Integer.toString(hours));

                final Slider sliderMinutes = (Slider) dialog.findViewById(R.id.sliderMinutes);
                sliderMinutes.setValue(minutes);
                final TextView minutesValue = (TextView) dialog.findViewById(R.id.minutesValue);
                minutesValue.setText(Integer.toString(minutes));

                sliderHours.setOnValueChangedListener(new Slider.OnValueChangedListener() {
                    @Override
                    public void onValueChanged(int i) {
                        hoursValue.setText(Integer.toString(sliderHours.getValue()));
                    }
                });

                sliderMinutes.setOnValueChangedListener(new Slider.OnValueChangedListener() {
                    @Override
                    public void onValueChanged(int i) {
                        minutesValue.setText(Integer.toString(sliderMinutes.getValue()));
                    }
                });

                ButtonFlat okButton = (ButtonFlat) dialog.findViewById(R.id.OKButton);
                // if button is clicked, close the custom dialog
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //chronometer.setText(String.valueOf(sliderHours.getValue() + ":" + sliderMinutes.getValue()));
                        String duration = Integer.toString(sliderHours.getValue() * 60 + sliderMinutes.getValue());
                        Log.d(TAG, "duration = "+duration);
                        history.setDuration(duration);
                        historyDB.open();
                        historyDB.updateSingleHistory(history);
                        historyDB.close();
                        dialog.dismiss();
                    }
                });

                ButtonFlat dialogButtonCancel = (ButtonFlat) dialog.findViewById(R.id.cancelButton);
                // if button is clicked, close the custom dialog
                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });



        // Return the completed view to render on screen
        return convertView;
    }
}
