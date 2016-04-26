package com.example.tristan.nfcbracelet.adapters;

import android.app.Dialog;
import android.app.TimePickerDialog;
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
public class TaskCheckAdapter extends ArrayAdapter<Task> {
    private static final String TAG = "TaskCheckAdapter";

    private ArrayList<Task> mCompanionTasks;

    public TaskCheckAdapter(Context context, ArrayList<Task> teamTasks, ArrayList<Task> companionTasks) {
        super(context, 0, teamTasks);

        mCompanionTasks = companionTasks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Task task = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_check_task, parent, false);
        }
        // Lookup view for data population
        final TextView taskName = (TextView) convertView.findViewById(R.id.checkTaskName);
        final CheckBox checkTask = (CheckBox) convertView.findViewById(R.id.checkTask);

        // Populate the data into the template view using the data object
        taskName.setText(task.getLongName());
        if (isInTaskList(task, mCompanionTasks))
            checkTask.setChecked(true);
        else
            checkTask.setChecked(false);


        // Return the completed view to render on screen
        return convertView;
    }

    private boolean isInTaskList(Task task, ArrayList<Task> tasks) {
        if (tasks == null)
            return false;

        for (Task taskIter : tasks) {
            if (task.equals(taskIter))
                return true;
        }
        return false;
    }
}
