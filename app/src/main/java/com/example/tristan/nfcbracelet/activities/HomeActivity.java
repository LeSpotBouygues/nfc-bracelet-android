package com.example.tristan.nfcbracelet.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tristan.nfcbracelet.R;
import com.example.tristan.nfcbracelet.database.CompanionDB;
import com.example.tristan.nfcbracelet.database.CompanionTasksDB;
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
import com.example.tristan.nfcbracelet.models.Session;
import com.example.tristan.nfcbracelet.models.Task;
import com.example.tristan.nfcbracelet.models.Team;
import com.example.tristan.nfcbracelet.nfc.NdefMessageParser;
import com.example.tristan.nfcbracelet.nfc.ParsedNdefRecord;
import com.example.tristan.nfcbracelet.utils.Date;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private ArrayList<Companion> mTeamMembers;
    private ArrayList<Task> mTeamTasks;

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;

    private AlertDialog mDialog;

    private static final DateFormat TIME_FORMAT = SimpleDateFormat.getDateTimeInstance();
    private LinearLayout mTagContent;

    private ProgressBarCircularIndeterminate spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Team");

        // NFC
        //mTagContent = (LinearLayout) findViewById(R.id.NFCList);
        Log.d(TAG, "onCreate resolveIntent");
        resolveIntent(getIntent());
        Log.d(TAG, "onCreate resolveIntent end");

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            showMessage(R.string.error, R.string.no_nfc);
            finish();
            return;
        }

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
                "Message from NFC Reader :-)", Locale.ENGLISH, true) });

        //

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // debug database - to comment
        //debugDB();

        if (Session.getInstance().getUser() == null) {
            goToMainActivity();
            return;
        }

        Log.d(TAG, "onCreate before fragment");

        new loadData().execute();

        Log.d(TAG, "onCreate after fragment");

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_home);
        TextView navChiefName = (TextView) headerView.findViewById(R.id.navChiefName);
        navChiefName.setText(Session.getInstance().getUser().getLastName() + " " + Session.getInstance().getUser().getFirstName());
        TextView navChiefPosition = (TextView) headerView.findViewById(R.id.navChiefPosition);
        navChiefPosition.setText(Session.getInstance().getUser().getPosition());
        spinner = (ProgressBarCircularIndeterminate) findViewById(R.id.spinnerHome);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String lastSynchro = sharedPref.getString("synchro", "");
        if (lastSynchro != null && lastSynchro.equals(android.text.format.DateFormat.format("dd:MM:yyyy", new java.util.Date()).toString()))
            Toast.makeText(this, "Warning : Last time data were sent was "+lastSynchro, Toast.LENGTH_LONG).show();
    }

    private void fillData() {
        Log.d(TAG, "fillData");
        if (Data.getInstance().getTeam() != null)
            return;
        // get companions from team
        TeamCompanionDB teamCompanionDB = new TeamCompanionDB(this);
        teamCompanionDB.open();
        Team teamMembers = teamCompanionDB.getTeamByChiefId(Session.getInstance().getUser().getUserId());
        mTeamMembers = teamMembers.getCompanions();
        teamCompanionDB.close();

        CompanionTasksDB companionTasksDB = new CompanionTasksDB(this);
        companionTasksDB.open();
        for (Companion companion : mTeamMembers) {
            companion.setTasksInProgress(companionTasksDB.getTasksByCompanion(companion));
            if (companionTasksDB.getTasksByCompanion(companion) == null) {
                Log.d(TAG, "tasks in progress null from db");
            }

        }
        companionTasksDB.close();

        // debug companions list
        /*Log.d(TAG, "== TEAM MEMBERS ==");
        for (Companion companion : mTeamMembers) {
            Log.d(TAG, companion.getFirstName());
            ArrayList<Task> tasks = companion.getTasksInProgress();
            for (Task task : tasks) {
                Log.d(TAG, "      task : "+task.getTaskId());
            }
            if (tasks == null) {
                Log.d(TAG, "tasks in progress null");
            }
        }*/

        // get tasks from team
        TeamTaskDB teamTaskDB = new TeamTaskDB(this);
        teamTaskDB.open();
        Team teamTasks = teamTaskDB.getTeamByChiefId(Session.getInstance().getUser().getUserId());
        mTeamTasks = teamTasks.getTasks();
        teamTaskDB.close();

        // debug tasks list
        /*Log.d(TAG, "== TEAM TASKS ==");
        for (Task task : mTeamTasks) {
            Log.d(TAG, task.getLongName());
        }*/

        // init data team
        Data.getInstance().setTeam(teamMembers, teamTasks);

        // init history table with tasks and companions affected to the team
        initHistoryTable();
        Log.d(TAG, "fill Data end");
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
                history.setSentInt(0);
                historyDB.open();
                if (historyDB.getHistoryByCompanionIdByTaskIdByDate(companion.getUserId(), task.getTaskId(), date) == null)
                    historyDB.insertHistory(history);
                /*else
                    historyDB.updateHistory(history, mData.getTeam());*/
                historyDB.close();
            }
        }
        /*historyDB.open();
        historyDB.displayTable();
        historyDB.close();*/
    }

    private void debugDB() {

        // debug companions
        Log.d("DB RESULTS", "=== COMPANIONS ===");
        CompanionDB companionDB = new CompanionDB(this);
        companionDB.open();
        ArrayList<Companion> results = companionDB.getAllCompanions();
        companionDB.close();
        for (Companion companion : results) {
            Log.d("DB RESULTS", companion.getFirstName());
        }

        // debug teams
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

        // debug tasks
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

        // debug companion tasks
        CompanionTasksDB companionTasksDB = new CompanionTasksDB(this);
        companionTasksDB.open();
        companionTasksDB.displayTable();
        companionTasksDB.close();
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

   /* @Override
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
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_team) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new CompanionsFragment())
                    .commit();
            getSupportActionBar().setTitle("Team");
        } else if (id == R.id.nav_tasks) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new TasksFragment())
                    .commit();
            getSupportActionBar().setTitle("Tasks");
        } else if (id == R.id.nav_send) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new SynchronizeDataFragment())
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
        if (mNfcAdapter != null) {
            if (!mNfcAdapter.isEnabled()) {
                showWirelessSettingsDialog();
            }
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            mNfcAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
        }
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
            mNfcAdapter.disableForegroundNdefPush(this);
        }

        super.onPause();
    }

    // NFC
    private void showMessage(int title, int message) {
        mDialog.setTitle(title);
        mDialog.setMessage(getText(message));
        mDialog.show();
    }

    private NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    private void showWirelessSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.nfc_disabled);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
        return;
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
                msgs = new NdefMessage[] { msg };
            }
            // Setup the views
            buildTagViews(msgs);
        }
    }

    private String dumpTagData(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        sb.append(getHex(id));

        Log.d(TAG, "NFC : " +sb.toString());
        new checkBraceletId().execute(sb.toString());
        return sb.toString();
    }

    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            /*if (i > 0) {
                sb.append(" ");
            }*/
        }
        return sb.toString();
    }

    void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout content = mTagContent;

        // Parse the first message in the list
        // Build views for all of the sub records
        java.util.Date now = new java.util.Date();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
        final int size = records.size();
        for (int i = 0; i < size; i++) {
            /*TextView timeView = new TextView(this);
            timeView.setText(TIME_FORMAT.format(now));
            content.addView(timeView, 0);*/
            ParsedNdefRecord record = records.get(i);

           /* content.addView(record.getView(this, inflater, content, i), 1 + i);
            content.addView(inflater.inflate(R.layout.tag_divider, content, false), 2 + i);*/
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_main_clear:
                menuMainClearClick();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void menuMainClearClick() {
        for (int i = mTagContent.getChildCount() -1; i >= 0 ; i--) {
            View view = mTagContent.getChildAt(i);
            if (view.getId() != R.id.tag_viewer_text) {
                mTagContent.removeViewAt(i);
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    private class checkBraceletId extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "checkBracelet");
            String braceletId = params[0];
            CompanionDB companionDB = new CompanionDB(getApplicationContext());
            companionDB.open();
            final Companion user = companionDB.getCompanionByBraceletId(braceletId);
            companionDB.close();
            if (user == null) {
                Log.d(TAG, "Companion not found");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Companion not found", Toast.LENGTH_LONG).show();
                    }
                });
                goToMainActivity();
                return "";
            }

            if (user.isChief()) {
                Session.getInstance().setUser(user);
                return "";
            }
            else if (Session.getInstance().getUser() == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Companion is not a chief", Toast.LENGTH_LONG).show();
                    }
                });
                goToMainActivity();
                return "";
            }
            ArrayList<Companion> companions = Data.getInstance().getTeam().getCompanions();
            for (Companion companion : companions) {
                if (companion.getBraceletId().equals(user.getBraceletId())) {
                    if (!companion.isPresent()) {
                        Log.d(TAG, "présence validée pour " + user.getFirstName());
                        Data.getInstance().getTeam().getCompanionByUserId(companion.getUserId()).setPresence(true);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), user.getFirstName() + "'s presence validated", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else {
                        Log.d(TAG, "présence déjà validée pour " + user.getFirstName());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), user.getFirstName() + "'s presence has already been validated", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    return "";
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), user.getFirstName() + " added to the team", Toast.LENGTH_LONG).show();
                }
            });
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "checkBracelet end");
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private class loadData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            fillData();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "checkBracelet end");
            new Handler().post(new Runnable() {
                public void run() {
                    spinner.setVisibility(View.GONE);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new CompanionsFragment())
                            .commit();
                }
            });
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
