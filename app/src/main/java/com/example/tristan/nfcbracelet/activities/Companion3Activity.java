package com.example.tristan.nfcbracelet.activities;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;

import com.example.tristan.nfcbracelet.R;

public class Companion3Activity extends AppCompatActivity {

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
        setContentView(R.layout.activity_companion3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mImageView1 = (ImageView) findViewById(R.id.imageView2);
        mChrono1 = (Chronometer) findViewById(R.id.chronometer);
        mButton1 = (Button) findViewById(R.id.button3);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButton1.getText() == "STOP") {
                    mButton1.setText("START");
                    mImageView1.setImageResource(R.mipmap.red_dot);
                    mChrono1.stop();
                } else {
                    mButton1.setText("STOP");
                    mImageView1.setImageResource(R.mipmap.green_dot);
                    mChrono1.setBase(SystemClock.elapsedRealtime());
                    mChrono1.start();
                }
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
                    mImageView2.setImageResource(R.mipmap.red_dot);
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

        mImageView3 = (ImageView) findViewById(R.id.imageView3);
        mChrono3 = (Chronometer) findViewById(R.id.chronometer3);
        mButton3 = (Button) findViewById(R.id.button4);
        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButton3.getText() == "STOP") {
                    mButton3.setText("START");
                    mImageView3.setImageResource(R.mipmap.red_dot);
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
                    mImageView4.setImageResource(R.mipmap.red_dot);
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
