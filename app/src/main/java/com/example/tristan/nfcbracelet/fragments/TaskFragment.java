package com.example.tristan.nfcbracelet.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;

import com.example.tristan.nfcbracelet.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TaskFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {

    Button mButton1;
    Button mButton2;
    Button mButton3;
    Button mButton4;
    ImageView mImageView2;
    ImageView mImageView3;
    ImageView mImageView4;

    public TaskFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);

        mButton1 = (Button) rootView.findViewById(R.id.button6);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButton1.getText() == "STOP") {
                    mButton1.setText("START");
                    mButton2.setText("START");
                    mImageView2.setImageResource(R.mipmap.reddot);
                    mButton3.setText("START");
                    mImageView3.setImageResource(R.mipmap.reddot);
                    mButton4.setText("START");
                    mImageView4.setImageResource(R.mipmap.reddot);

                } else {
                    mButton1.setText("STOP");
                    mButton2.setText("STOP");
                    mImageView2.setImageResource(R.mipmap.green_dot);
                    mButton3.setText("STOP");
                    mImageView3.setImageResource(R.mipmap.green_dot);
                    mButton4.setText("STOP");
                    mImageView4.setImageResource(R.mipmap.green_dot);
                }
            }
        });

        mImageView2 = (ImageView) rootView.findViewById(R.id.imageView12);
        mButton2 = (Button) rootView.findViewById(R.id.button10);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButton2.getText() == "STOP") {
                    mButton2.setText("START");
                    mImageView2.setImageResource(R.mipmap.reddot);
                } else {
                    mButton2.setText("STOP");
                    mImageView2.setImageResource(R.mipmap.green_dot);
                }
            }
        });

        mImageView3 = (ImageView) rootView.findViewById(R.id.imageView13);
        mButton3 = (Button) rootView.findViewById(R.id.button7);
        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButton3.getText() == "STOP") {
                    mButton3.setText("START");
                    mImageView3.setImageResource(R.mipmap.reddot);
                } else {
                    mButton3.setText("STOP");
                    mImageView3.setImageResource(R.mipmap.green_dot);
                }
            }
        });

        mImageView4 = (ImageView) rootView.findViewById(R.id.imageView14);
        mButton4 = (Button) rootView.findViewById(R.id.button8);
        mButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButton4.getText() == "STOP") {
                    mButton4.setText("START");
                    mImageView4.setImageResource(R.mipmap.reddot);
                } else {
                    mButton4.setText("STOP");
                    mImageView4.setImageResource(R.mipmap.green_dot);
                }
            }
        });

        return rootView;
    }
}
