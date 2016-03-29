package com.example.tristan.nfcbracelet.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.tristan.nfcbracelet.R;
import com.example.tristan.nfcbracelet.activities.Companion2Activity;
import com.example.tristan.nfcbracelet.activities.CompanionActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompanionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompanionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompanionsFragment extends Fragment {
    public CompanionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_companions,
                container, false);
        LinearLayout companion1 = (LinearLayout) rootView.findViewById(R.id.companion1);
        final ImageView imageView1 = (ImageView) rootView.findViewById(R.id.imageView11);
        companion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (imageView1.getIm)
                Intent intent = new Intent(getActivity(), CompanionActivity.class);
                startActivity(intent);
            }
        });
        /*LinearLayout companion2 = (LinearLayout) rootView.findViewById(R.id.companion2);
        companion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Companion2Activity.class);
                startActivity(intent);
            }
        });
        LinearLayout companion3 = (LinearLayout) rootView.findViewById(R.id.companion3);
        companion3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Companion2Activity.class);
                startActivity(intent);
            }
        });*/

        return rootView;
    }
}
