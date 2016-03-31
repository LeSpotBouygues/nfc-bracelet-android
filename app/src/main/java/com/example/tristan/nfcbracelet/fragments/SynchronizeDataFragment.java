package com.example.tristan.nfcbracelet.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.tristan.nfcbracelet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SynchronizeDataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SynchronizeDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SynchronizeDataFragment extends Fragment {
    public SynchronizeDataFragment() {
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
        View view = inflater.inflate(R.layout.fragment_synchronize_data,
                container, false);
        final Button button = (Button) view.findViewById(R.id.sendDataButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData();
            }
        });
        return view;
    }


    void displayToast() {
        Toast.makeText(getContext(), "Données mises à jour", Toast.LENGTH_LONG).show();
    }

    void sendData() {
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("companion", "56ea7f5bdf04853d33736c16");
            JSONArray tasks = new JSONArray();
            JSONObject task1 = new JSONObject();
            task1.put("id", "56ea821adf04853d33736e5b");
            task1.put("duration", "5000");
            tasks.put(task1);
            JSONObject task2 = new JSONObject();
            task2.put("id", "56ea821adf04853d33736e5d");
            task2.put("duration", "10000");
            tasks.put(task2);
            jsonObject.put("taskInProgress", tasks);
            jsonObject.put("date", "2016:03:23");
        } catch (JSONException e) {

        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        Request request = new Request.Builder()
                .url("http://54.86.80.245:3000/history")
                .post(body)
                .build();
        Log.d("SEND DATA", jsonObject.toString());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String responseString = response.body().string();
                    Log.d("SEND DATA", responseString);
                }
            }
        });
    }
}
