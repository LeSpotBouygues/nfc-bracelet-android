package com.example.tristan.nfcbracelet.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tristan.nfcbracelet.R;
import com.example.tristan.nfcbracelet.database.HistoryDB;
import com.example.tristan.nfcbracelet.http.HttpApi;
import com.example.tristan.nfcbracelet.models.Companion;
import com.example.tristan.nfcbracelet.models.Data;
import com.example.tristan.nfcbracelet.models.History;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    private static final String TAG = "SynchronizeDataFragment";

    private Data mData;
    private HttpApi httpApi;

    private ProgressBarCircularIndeterminate spinner;
    private TextView sendDataText;

    public SynchronizeDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mData = Data.getInstance();
        httpApi = HttpApi.getInstance();
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
        spinner = (ProgressBarCircularIndeterminate) view.findViewById(R.id.spinner2);
        spinner.setVisibility(View.GONE);
        sendDataText = (TextView) view.findViewById(R.id.sendButtonText);

        return view;
    }


    void displayToast() {
        Toast.makeText(getContext(), "Data successfully sent", Toast.LENGTH_LONG).show();
    }

    void sendData() {
        spinner.setVisibility(View.VISIBLE);

        HistoryDB historyDB = new HistoryDB(getContext());
        ArrayList<Companion> companions = mData.getTeam().getCompanions();
        for (Companion companion : companions) {
            historyDB.open();
            ArrayList<History> historyList = historyDB.getAllHistoryByCompanionId(companion.getUserId());
            historyDB.close();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("companion", companion.getUserId());
                JSONArray tasks = new JSONArray();
                for (History history : historyList) {
                    JSONObject task = new JSONObject();
                    task.put("id", history.getTask().getTaskId());
                    task.put("duration", history.getDuration());
                    tasks.put(task);
                }
                jsonObject.put("taskInProgress", tasks);
                // TODO check data with locale db
                Log.d(TAG, DateFormat.format("yyyy.MM.dd", new Date()).toString());
                jsonObject.put("date", DateFormat.format("yyyy:MM:dd", new Date()).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
            Request request = new Request.Builder()
                    .url(httpApi.API_ADDRESS + httpApi.HISTORY_ROUTE)
                    .post(body)
                    .build();
            Log.d("SEND DATA", jsonObject.toString());
            httpApi.getClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner.setVisibility(View.GONE);
                            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            Calendar calendar = Calendar.getInstance();
                            int day = calendar.get(Calendar.DAY_OF_WEEK);
                            String dayOfWeek = "";
                            switch (day) {
                                case Calendar.SUNDAY:
                                    dayOfWeek = "Sunday";

                                case Calendar.MONDAY:
                                    dayOfWeek = "Monday";

                                case Calendar.TUESDAY:
                                    dayOfWeek = "Tuesday";

                                case Calendar.WEDNESDAY:
                                    dayOfWeek = "Wednesday";

                                case Calendar.THURSDAY:
                                    dayOfWeek = "Thursday";

                                case Calendar.FRIDAY:
                                    dayOfWeek = "Friday";

                                case Calendar.SATURDAY:
                                    dayOfWeek = "Saturday";
                            }
                            String sendDataString = "Last synchronization : "
                                    + dayOfWeek + " "
                                    + android.text.format.DateFormat.format("dd:MM:yyyy", new java.util.Date()).toString()
                                    + " at "
                                    + android.text.format.DateFormat.format("HH:mm:ss", new java.util.Date()).toString();
                            editor.putString("synchro", DateFormat.format("dd:MM:yyyy", new java.util.Date()).toString());
                            editor.commit();
                            sendDataText.setText(sendDataString);
                        }
                    });
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
}
