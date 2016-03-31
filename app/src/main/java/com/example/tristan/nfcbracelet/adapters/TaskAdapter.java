package com.example.tristan.nfcbracelet.adapters;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
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

    private ArrayList<History> mHistoryList;
    private HistoryDB historyDB;

    public TaskAdapter(Context context, ArrayList<Task> tasks, ArrayList<History> historyList) {
        super(context, 0, tasks);

        mHistoryList = historyList;
        historyDB = new HistoryDB(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Task task = getItem(position);
        History historySave = null;
        for (History historyIter : mHistoryList) {
            if (task.getTaskId().equals(historyIter.getTask().getTaskId())) {
                historySave = historyIter;
            }
        }
        final History history = historySave;
        final String date = Date.getInstance().getDateToString();
        final String time = Date.getInstance().getTimeNowToString();

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
        if (history.isStarted()) {
            startTaskButton.setText("STOP");
            light.setImageResource(R.mipmap.green_dot);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                java.util.Date lastStart = sdf.parse(history.getLastStart());
                chronometer.setBase(SystemClock.elapsedRealtime() - lastStart.getTime());
                chronometer.start();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        startTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startTaskButton.getText().equals("START")) {
                    startTaskButton.setText("STOP");
                    light.setImageResource(R.mipmap.green_dot);
                    chronometer.setBase(SystemClock.elapsedRealtime());
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
                    history.setDuration("50");
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

                    history.setStarted(true);
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
                Log.d(TAG, "hours = "+Integer.toString(hours));
                Log.d(TAG, "minutes = "+Integer.toString(minutes));

                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_date);

                final Slider sliderHours = (Slider) dialog.findViewById(R.id.sliderHours);
                sliderHours.setValue(hours);
                final TextView hoursValue = (TextView) dialog.findViewById(R.id.hoursValue);
                hoursValue.setText(Integer.toString(sliderHours.getValue()));

                final Slider sliderMinutes = (Slider) dialog.findViewById(R.id.sliderMinutes);
                sliderMinutes.setValue(minutes);
                final TextView minutesValue = (TextView) dialog.findViewById(R.id.minutesValue);
                minutesValue.setText(Integer.toString(sliderMinutes.getValue()));

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
                        chronometer.setText(String.valueOf(sliderHours.getValue() + ":" + sliderMinutes.getValue()));
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
