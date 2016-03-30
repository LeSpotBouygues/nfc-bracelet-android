package com.example.tristan.nfcbracelet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tristan.nfcbracelet.R;
import com.example.tristan.nfcbracelet.database.CompanionDB;
import com.example.tristan.nfcbracelet.models.Companion;

import java.util.ArrayList;

/**
 * Created by Tristan on 30/03/2016.
 */
public class CompanionAdapter extends ArrayAdapter<Companion> {
    public CompanionAdapter(Context context, ArrayList<Companion> companions) {
        super(context, 0, companions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Companion companion = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_companion, parent, false);
        }
        // Lookup view for data population
        TextView companionName = (TextView) convertView.findViewById(R.id.companionName);
        // Populate the data into the template view using the data object
        companionName.setText(companion.getFirstName() + " " + companion.getLastName());
        // Return the completed view to render on screen
        return convertView;
    }
}
