package com.example.tristan.nfcbracelet.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.example.tristan.nfcbracelet.R;
import com.example.tristan.nfcbracelet.activities.CompanionActivity;
import com.example.tristan.nfcbracelet.adapters.CompanionAdapter;
import com.example.tristan.nfcbracelet.models.Companion;
import com.example.tristan.nfcbracelet.models.Data;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompanionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompanionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompanionsFragment extends Fragment {
    private static final String TAG = "CompanionsFragment";

    private Data mData;

    public CompanionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mData = Data.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_companions,
                container, false);
        final CompanionAdapter adapter = new CompanionAdapter(getContext(), mData.getTeam().getCompanions());
        // Attach the adapter to a ListView
        ListView listView = (ListView) rootView.findViewById(R.id.companionsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Companion companion = adapter.getItem(position);
                Log.d(TAG, "Click on " + companion.getFirstName());
                CheckBox checkBox = (CheckBox) adapter.getView(position, view.findViewById(R.id.checkBox), parent);
                if (checkBox.isChecked()) {
                    Intent intent = new Intent(getActivity(), CompanionActivity.class);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }
}
