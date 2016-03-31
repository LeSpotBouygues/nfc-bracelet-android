package com.example.tristan.nfcbracelet.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.tristan.nfcbracelet.models.Companion;

import java.util.ArrayList;

/**
 * Created by Tristan on 30/03/2016.
 */
public class TaskCompanionAdapter extends ArrayAdapter<Companion> {

    private static final String TAG = "TaskCompanionAdapter";

    public TaskCompanionAdapter(Context context, ArrayList<Companion> companions) {
        super(context, 0, companions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Companion companion = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task_companion, parent, false);
        }
        // Lookup view for data population
        final ImageView light = (ImageView) convertView.findViewById(R.id.taskCompanionLight);
        final TextView companionName = (TextView) convertView.findViewById(R.id.taskCompanion);
        final Button startTaskButton = (Button) convertView.findViewById(R.id.startTaskCompanion);
        startTaskButton.setText("START");

        // Populate the data into the template view using the data object
        companionName.setText(companion.getLastName() + " " + companion.getFirstName());
        companionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : check presence
                //if (checkBox.isChecked()) {
                    Intent intent = new Intent(getContext(), CompanionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("companionId", companion.getUserId());
                    intent.putExtras(bundle);
                    getContext().startActivity(intent);
                /*} else {
                    Toast.makeText(getContext(), companionName.getText() + "'s presence hasn't been checked", Toast.LENGTH_LONG).show();
                }*/
            }
        });
        startTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : link to DB
                if (startTaskButton.getText() == "START") {
                    startTaskButton.setText("STOP");
                    light.setImageResource(R.mipmap.green_dot);
                }
                else if (startTaskButton.getText() == "STOP") {
                    startTaskButton.setText("START");
                    light.setImageResource(R.mipmap.reddot);
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
