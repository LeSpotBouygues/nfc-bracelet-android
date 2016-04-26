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
import android.widget.ListView;
import android.widget.TextView;

import com.example.tristan.nfcbracelet.R;
import com.example.tristan.nfcbracelet.adapters.CompanionAdapter;
import com.example.tristan.nfcbracelet.adapters.TaskCompanionAdapter;
import com.example.tristan.nfcbracelet.models.Data;
import com.example.tristan.nfcbracelet.models.Task;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TaskFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {

    private Data mData;
    private TaskCompanionAdapter adapter;
    private ListView listView;
    private Task task;

    public TaskFragment() {
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

        Bundle bundle = getArguments();
        task = mData.getTeam().getTaskByTaskId(bundle.getString("taskId"));

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);
        adapter = new TaskCompanionAdapter(getContext(), mData.getTeam().getCompanions(), task);
        // Attach the adapter to a ListView
        listView = (ListView) rootView.findViewById(R.id.taskCompanionsList);
        listView.setAdapter(adapter);

        TextView taskName = (TextView) rootView.findViewById(R.id.taskName);
        taskName.setText(task.getLongName());
        TextView taskCode = (TextView) rootView.findViewById(R.id.taskCode);
        taskCode.setText(task.getCode());

        final Button startAllTasksButton = (Button) rootView.findViewById(R.id.startAllTasks);
        startAllTasksButton.setText("START");
        startAllTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startAllTasksButton.getText() == "START") {
                    startAllTasksButton.setText("STOP");
                    for (int i = 0; i < listView.getCount(); i++) {
                        View view = listView.getChildAt(i);
                        if (view != null) {
                            ((ImageView) view.findViewById(R.id.taskCompanionLight)).setImageResource(R.mipmap.green_dot);
                            ((Button) view.findViewById(R.id.startTaskCompanion)).setText("STOP");
                        }
                    }
                } else if (startAllTasksButton.getText() == "STOP") {
                    startAllTasksButton.setText("START");
                    for (int i = 0; i < listView.getCount(); i++) {
                        View view = listView.getChildAt(i);
                        if (view != null) {
                            ((ImageView) view.findViewById(R.id.taskCompanionLight)).setImageResource(R.mipmap.reddot);
                            ((Button) view.findViewById(R.id.startTaskCompanion)).setText("START");
                        }
                    }
                }
            }
        });

        return rootView;
    }
}
