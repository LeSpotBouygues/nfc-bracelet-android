package com.example.tristan.nfcbracelet.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tristan.nfcbracelet.R;
import com.example.tristan.nfcbracelet.database.CompanionDB;
import com.example.tristan.nfcbracelet.database.HistoryDB;
import com.example.tristan.nfcbracelet.database.TaskDB;
import com.example.tristan.nfcbracelet.database.TeamDB;
import com.example.tristan.nfcbracelet.fragments.CompanionsFragment;
import com.example.tristan.nfcbracelet.http.HttpApi;
import com.example.tristan.nfcbracelet.models.Companion;
import com.example.tristan.nfcbracelet.models.Task;
import com.example.tristan.nfcbracelet.models.Team;
import com.example.tristan.nfcbracelet.utils.RealmString;
import com.example.tristan.nfcbracelet.utils.RealmStringDeserializer;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends Activity {

    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";

    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    private HttpApi httpApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        httpApi = HttpApi.getInstance();

        mTextView = (TextView) findViewById(R.id.textView_explanation);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText("NFC is disabled.");
        } else {
            //mTextView.setText(R.string.explanation);
        }

        handleIntent(getIntent());
        fillDatabase();
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
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Log.d("NFC", "TECH");
            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
        else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)){
            Log.d("NFC", "OTHER");
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            new NdefReaderTask().execute(tag);
        }
    }

    private void goToProfile() {
        Intent intent = new Intent(this, HomeActivity.class);
        String message = mTextView.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private void fillDatabase() {
        getCompanionsFromServer();
        getTasksFromServer();
    }

    private void getTasksFromServer() {

        // get all tasks
        Request requestTasks = new Request.Builder()
                .url(httpApi.API_ADDRESS + httpApi.TASKS_ROUTE)
                .build();
        httpApi.getClient().newCall(requestTasks).enqueue(new Callback() {
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
                    Log.d("GET TASKS", responseString);
                    fillTasksInDB(responseString);
                }
            }
        });
    }

    private void getCompanionsFromServer() {

        // get all companions
        Request requestCompanions = new Request.Builder()
                .url(httpApi.API_ADDRESS + httpApi.COMPANIONS_ROUTE)
                .build();
        httpApi.getClient().newCall(requestCompanions).enqueue(new Callback() {
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
                    Log.d("GET COMPANIONS", responseString);
                    fillCompanionsInDB(responseString);
                    getTeamsFromServer();
                }
            }
        });
    }

    private void getTeamsFromServer() {

        // get all teams
        Request requestTeams = new Request.Builder()
                .url(httpApi.API_ADDRESS + httpApi.TEAMS_ROUTE)
                .build();
        httpApi.getClient().newCall(requestTeams).enqueue(new Callback() {
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
                    Log.d("GET TEAMS", responseString);
                    fillTeamsInDB(responseString);
                    //initHistoryTable();
                }
            }
        });
    }

    /*private void initHistoryTable() {
        try {
            HistoryDB historyDB = new HistoryDB(this);

            for (int i=0; i < jsonResponse.length(); i++) {
                JSONObject jsonObject = jsonResponse.getJSONObject(i);
                Task task = new Task();
                task.setTaskId(jsonObject.getString("_id"));
                task.setShortName(jsonObject.getString("label_short"));
                task.setLongName(jsonObject.getString("label_long"));

                taskDB.open();
                taskDB.insertTask(task);
                taskDB.close();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Toast.makeText(this, "Companions updated", Toast.LENGTH_SHORT).show();
    }*/

    private void fillTasksInDB(String response) {
        try {
            JSONArray jsonResponse = new JSONArray(response);
            TaskDB taskDB = new TaskDB(this);

            for (int i=0; i < jsonResponse.length(); i++) {
                JSONObject jsonObject = jsonResponse.getJSONObject(i);
                Task task = new Task();
                task.setTaskId(jsonObject.getString("_id"));
                task.setShortName(jsonObject.getString("label_short"));
                task.setLongName(jsonObject.getString("label_long"));

                taskDB.open();
                taskDB.insertTask(task);
                taskDB.close();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Toast.makeText(this, "Companions updated", Toast.LENGTH_SHORT).show();
    }

    private void fillCompanionsInDB(String response) {
        try {
            JSONArray jsonResponse = new JSONArray(response);
            CompanionDB companionDB = new CompanionDB(this);

            for (int i=0; i < jsonResponse.length(); i++) {
                JSONObject jsonObject = jsonResponse.getJSONObject(i);
                Companion companion = new Companion();
                companion.setUserId(jsonObject.getString("_id"));
                companion.setFirstName(jsonObject.getString("firstName"));
                companion.setLastName(jsonObject.getString("lastName"));
                if (jsonObject.has("position"))
                    companion.setPosition(jsonObject.getString("position"));
                else
                    companion.setPosition("");
                if (jsonObject.has("bracelet_id"))
                    companion.setBraceletId(jsonObject.getString("bracelet_id"));
                else
                    companion.setBraceletId("");
                //companion.setAliasName(jsonObject.getString("aliasName"));
                //companion.setChief(jsonObject.getBoolean("chief"));

                companionDB.open();
                companionDB.insertCompanion(companion);
                companionDB.close();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
       // Toast.makeText(this, "Companions updated", Toast.LENGTH_SHORT).show();
    }

    void fillTeamsInDB(String response) {
        try {
            JSONArray jsonResponse = new JSONArray(response);

            TeamDB teamDB = new TeamDB(this);
            CompanionDB  companionDB = new CompanionDB(this);

            for (int i=0; i < jsonResponse.length(); i++) {
                JSONObject jsonObject = jsonResponse.getJSONObject(i);
                Team team = new Team();
                team.setTeamId(jsonObject.getString("_id"));
                team.setChiefId(jsonObject.getJSONObject("chief").getString("_id"));
                JSONArray companions = jsonObject.getJSONArray("companions");
                for (int j=0; j < companions.length(); j++) {
                    companionDB.open();
                    team.addCompanion(companionDB.getCompanionByUserId(companions.getJSONObject(j).getString("_id")));
                    companionDB.close();
                }
                teamDB.open();
                teamDB.insertTeam(team);
                teamDB.close();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Toast.makeText(this, "Teams updated", Toast.LENGTH_SHORT).show();
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                mTextView.setText("Read content: " + result);
                //mTextView.setText("56e82962eef41015062d408c");
                goToProfile();
            }
        }
    }
}
