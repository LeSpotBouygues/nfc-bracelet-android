package com.example.tristan.nfcbracelet.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tristan.nfcbracelet.R;
import com.example.tristan.nfcbracelet.database.CompanionDB;
import com.example.tristan.nfcbracelet.database.HistoryDB;
import com.example.tristan.nfcbracelet.database.TaskDB;
import com.example.tristan.nfcbracelet.database.TeamCompanionDB;
import com.example.tristan.nfcbracelet.database.TeamTaskDB;
import com.example.tristan.nfcbracelet.fragments.CompanionsFragment;
import com.example.tristan.nfcbracelet.fragments.SynchronizeDataFragment;
import com.example.tristan.nfcbracelet.fragments.TasksFragment;
import com.example.tristan.nfcbracelet.models.Companion;
import com.example.tristan.nfcbracelet.models.Data;
import com.example.tristan.nfcbracelet.models.History;
import com.example.tristan.nfcbracelet.models.Task;
import com.example.tristan.nfcbracelet.models.Team;
import com.example.tristan.nfcbracelet.utils.Date;

import java.util.ArrayList;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    public static final String MIME_TEXT_PLAIN = "text/plain";

    private Data mData;
    private Companion mUser;
    private ArrayList<Companion> mTeamMembers;
    private ArrayList<Task> mTeamTasks;

    NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Team");

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // debug database - to comment
        debugDB();

        CompanionDB companionDB = new CompanionDB(this);
        companionDB.open();
        //mUser = companionDB.getCompanionByBraceletId("");
        //82a1b0e4
        // temporary
        mUser = companionDB.getCompanionByUserId("56ea7f5bdf04853d33736c16");
        companionDB.close();

        // get companions from team
        TeamCompanionDB teamCompanionDB = new TeamCompanionDB(this);
        teamCompanionDB.open();
        Team teamMembers = teamCompanionDB.getTeamByChiefId(mUser.getUserId());
        mTeamMembers = teamMembers.getCompanions();
        teamCompanionDB.close();

        // debug companions list
        Log.d(TAG, "== TEAM MEMBERS ==");
        for (Companion companion : mTeamMembers) {
            Log.d(TAG, companion.getFirstName());
        }

        // get tasks from team
        TeamTaskDB teamTaskDB = new TeamTaskDB(this);
        teamTaskDB.open();
        Team teamTasks = teamTaskDB.getTeamByChiefId(mUser.getUserId());
        mTeamTasks = teamTasks.getTasks();
        teamTaskDB.close();

        // debug tasks list
        Log.d(TAG, "== TEAM TASKS ==");
        for (Task task : mTeamTasks) {
            Log.d(TAG, task.getLongName());
        }

        // init data team
        mData = Data.getInstance();
        mData.setTeam(teamMembers, teamTasks);

        // init history table with tasks and companions affected to the team
        initHistoryTable();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, new CompanionsFragment())
                .commit();
    }

    private void initHistoryTable() {
        String date = Date.getInstance().getDateToString();
        String time = Date.getInstance().getTimeNowToString();
        HistoryDB historyDB = new HistoryDB(this);
        for (Companion companion : mTeamMembers) {
            for (Task task : mTeamTasks) {
                History history = new History();
                history.setCompanion(companion);
                history.setDuration("0");
                history.setDate(date);
                history.setTask(task);
                history.setStartedInt(0);
                history.setLastStart(time);
                historyDB.open();
                if (historyDB.getHistoryByCompanionIdByTaskIdByDate(companion.getUserId(), task.getTaskId(), date) == null)
                    historyDB.insertHistory(history);
                else
                    historyDB.updateHistory(history, mData.getTeam());
                historyDB.close();
            }
        }
        historyDB.open();
        historyDB.displayTable();
        historyDB.close();
    }

    private void debugDB() {
        Log.d("DB RESULTS", "=== COMPANIONS ===");
        CompanionDB companionDB = new CompanionDB(this);
        companionDB.open();
        ArrayList<Companion> results = companionDB.getAllCompanions();
        companionDB.close();
        for (Companion companion : results) {
            Log.d("DB RESULTS", companion.getFirstName());
        }
        Log.d("DB RESULTS", "=== TEAMS ===");
        TeamCompanionDB teamCompanionDB = new TeamCompanionDB(this);
        teamCompanionDB.open();
        teamCompanionDB.displayTable();
        ArrayList<Team> allTeams = teamCompanionDB.getAllTeams();
        teamCompanionDB.close();
        for (Team team : allTeams) {
            Log.d("DB RESULTS", "TEAM = " + team.getTeamId());
            //Log.d("DB RESULTS", team.getChief().getFirstName());
            for (Companion companion : team.getCompanions()) {
                Log.d("DB RESULTS", "    " + companion.getFirstName());
            }
        }
        Log.d("DB RESULTS", "=== TASKS ===");
        TaskDB taskDB = new TaskDB(this);
        taskDB.open();
        ArrayList<Task> taskResults = taskDB.getAllTasks();
        taskDB.close();
        for (Task task : taskResults) {
            Log.d("DB RESULTS", task.getLongName());
        }
        TeamTaskDB teamTaskDB = new TeamTaskDB(this);
        teamTaskDB.open();
        teamTaskDB.displayTable();
        ArrayList<Team> allTeamsTasks = teamTaskDB.getAllTeams();
        teamTaskDB.close();
        for (Team team : allTeamsTasks) {
            Log.d("DB RESULTS", "TEAM = " + team.getTeamId());
            //Log.d("DB RESULTS", team.getChief().getFirstName());
            for (Task task : team.getTasks()) {
                Log.d("DB RESULTS", "    " + task.getLongName());
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_team) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, new CompanionsFragment())
                    .commit();
            getSupportActionBar().setTitle("Team");
        } else if (id == R.id.nav_tasks) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, new TasksFragment())
                    .commit();
            getSupportActionBar().setTitle("Tasks");
        } else if (id == R.id.nav_send) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, new SynchronizeDataFragment())
                    .commit();
            getSupportActionBar().setTitle("Send data");
        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link BaseActivity} requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }


    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Log.d("NFC", "NDEF");
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                //new NdefReaderTask().execute(tag);

            } else {
                //Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Log.d("NFC", "TECH");
            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    //new NdefReaderTask().execute(tag);
                    break;
                }
            }
        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Log.d("NFC", "OTHER");
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            //new NdefReaderTask().execute(tag);
        }
        Toast.makeText(this, "Présence validée", Toast.LENGTH_LONG).show();
        // Setup the views
        //buildTagViews(msgs);
    }
}
