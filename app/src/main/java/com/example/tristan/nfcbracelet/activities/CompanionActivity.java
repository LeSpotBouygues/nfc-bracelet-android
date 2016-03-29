package com.example.tristan.nfcbracelet.activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.example.tristan.nfcbracelet.R;

public class CompanionActivity extends AppCompatActivity {

    Button mButton1;
    Button mButton2;
    Button mButton3;
    Button mButton4;
    ImageView mImageView1;
    ImageView mImageView2;
    ImageView mImageView3;
    ImageView mImageView4;
    Chronometer mChrono1;
    Chronometer mChrono2;
    Chronometer mChrono3;
    Chronometer mChrono4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mImageView1 = (ImageView) findViewById(R.id.imageView2);
        mChrono1 = (Chronometer) findViewById(R.id.chronometer);
        mButton1 = (Button) findViewById(R.id.button3);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButton1.getText() == "STOP") {
                    mButton1.setText("START");
                    mImageView1.setImageResource(R.mipmap.reddot);
                    mChrono1.stop();
                } else {
                    mButton1.setText("STOP");
                    mImageView1.setImageResource(R.mipmap.green_dot);
                    mChrono1.setBase(SystemClock.elapsedRealtime());
                    mChrono1.start();
                }
            }
        });
        mChrono1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Process to get Current Time
                long timeElapsed = SystemClock.elapsedRealtime() - mChrono1.getBase();
                int hours = (int) (timeElapsed / 3600000);
                int minutes = (int) (timeElapsed - hours * 3600000) / 60000;

                // Launch Time Picker Dialog
                TimePickerDialog tpd = new TimePickerDialog(CompanionActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // Display Selected time in textbox
                                mChrono1.setText(hourOfDay + ":" + minute);
                            }
                        }, hours, minutes, false);
                tpd.show();
            }
        });

        mImageView2 = (ImageView) findViewById(R.id.imageView8);
        mChrono2 = (Chronometer) findViewById(R.id.chronometer2);
        mButton2 = (Button) findViewById(R.id.button);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButton2.getText() == "STOP") {
                    mButton2.setText("START");
                    mImageView2.setImageResource(R.mipmap.reddot);
                    mChrono2.stop();
                }
                else
                {
                    mButton2.setText("STOP");
                    mImageView2.setImageResource(R.mipmap.green_dot);
                    mChrono2.setBase(SystemClock.elapsedRealtime());
                    mChrono2.start();
                }
            }
        });
        mChrono2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(CompanionActivity.this);
                dialog.setContentView(R.layout.dialog_date);
                //dialog.setTitle("Title...");

                // set the custom dialog components - text, image and button
                /*TextView text = (TextView) dialog.findViewById(R.id.text);
                text.setText("Android custom dialog ele!");
                ImageView image = (ImageView) dialog.findViewById(R.id.image);
                image.setImageResource(R.drawable.ic_launcher);*/
                final NumberPicker hours = (NumberPicker) dialog.findViewById(R.id.hours);
                hours.setMaxValue(23);
                hours.setMinValue(0);
                hours.setWrapSelectorWheel(false);
                final NumberPicker minutes = (NumberPicker) dialog.findViewById(R.id.minutes);
                minutes.setMaxValue(59);
                minutes.setMinValue(0);
                minutes.setWrapSelectorWheel(false);
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mChrono2.setText(String.valueOf(hours.getValue() + ":" + minutes.getValue()));
                        dialog.dismiss();
                    }
                });
                Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
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

        mImageView3 = (ImageView) findViewById(R.id.imageView3);
        mChrono3 = (Chronometer) findViewById(R.id.chronometer3);
        mButton3 = (Button) findViewById(R.id.button4);
        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButton3.getText() == "STOP") {
                    mButton3.setText("START");
                    mImageView3.setImageResource(R.mipmap.reddot);
                    mChrono3.stop();
                }
                else
                {
                    mButton3.setText("STOP");
                    mImageView3.setImageResource(R.mipmap.green_dot);
                    mChrono3.setBase(SystemClock.elapsedRealtime());
                    mChrono3.start();
                }
            }
        });

        mImageView4 = (ImageView) findViewById(R.id.imageView4);
        mChrono4 = (Chronometer) findViewById(R.id.chronometer4);
        mButton4 = (Button) findViewById(R.id.button5);
        mButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButton4.getText() == "STOP") {
                    mButton4.setText("START");
                    mImageView4.setImageResource(R.mipmap.reddot);
                    mChrono4.stop();
                }
                else
                {
                    mButton4.setText("STOP");
                    mImageView4.setImageResource(R.mipmap.green_dot);
                    mChrono4.setBase(SystemClock.elapsedRealtime());
                    mChrono4.start();
                }
            }
        });
    }

}
