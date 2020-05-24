package com.example.pelicanremote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ExecutionException;

import static com.example.pelicanremote.PelicanUrlBuilder.Endpoint;

public class MainActivity extends AppCompatActivity {

    public static final String ACTIVATED = "ACTIVATED";
    public static final String DEACTIVATED = "DEACTIVATED";
    public static final String MODIFYING = "MODIFYING";
    public static final String STATUS = "status";
    public static final String LAST_CHANGE = "lastChange";
    public static final String LAST_CHANGE_BY = "lastChangeBy";
    public static final String SCHEDULED_DEACTIVATION = "scheduledDeactivation";

    private final Handler statusHandler = new Handler();
    private final Runnable statusUpdaterRunnable = statusUpdaterRunnable();
    private PelicanUrlBuilder urlBuilder = null;
    private SharedPreferences settings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.urlBuilder = new PelicanUrlBuilder(
                getString(R.string.default_protocol),
                getString(R.string.default_address),
                getString(R.string.default_port)
        );
        this.settings = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        setStatusToggleButtonClickListener(R.id.deactivateButton, urlBuilder.build(Endpoint.DEACTIVE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            }
        }
        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        refreshUrlBuilder();
        statusHandler.postDelayed(statusUpdaterRunnable, 0);
        setStatusToggleButtonClickListener(R.id.activateButton, urlBuilder.build(Endpoint.ACTIVATE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        statusHandler.removeCallbacks(statusUpdaterRunnable);
    }

    private void refreshUrlBuilder() {
        urlBuilder.setServerProtocol(settings.getString("protocol", getString(R.string.default_protocol)));
        urlBuilder.setServerAddress(settings.getString("address", getString(R.string.default_address)));
        urlBuilder.setServerPort(settings.getString("port", getString(R.string.default_port)));
        urlBuilder.setAutomaticDeactivationTimeoutSeconds(settings.getString("deactivation_timeout", getString(R.string.default_deactivation_timeout)));
    }

    private Runnable statusUpdaterRunnable() {
        return new Runnable() {
                public void run() {
                    statusHandler.postDelayed(this, Integer.parseInt(settings.getString(
                            "statusPollIntervalMillis",
                            getString(R.string.default_status_poll_interval_millis))
                    ));
                    refreshStatus();
                }
            };
    }

    private void refreshStatus() {
        AsyncTask<String, String, String> result = new PelicanRequest().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR, urlBuilder.build(Endpoint.STATUS)
        );

        try {
            String serverResponse = result.get();
            populateStatusLabels(serverResponse);
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
            changeElementsToNotConnectedMode();
        }
    }

    private void changeElementsToNotConnectedMode(){
        TextView statusResultLabel = findViewById(R.id.status_result_label);
        statusResultLabel.setText(getString(R.string.status_not_connected));
        statusResultLabel.setTextColor(Color.GRAY);
        findViewById(R.id.activateButton).setEnabled(false);
        findViewById(R.id.deactivateButton).setEnabled(false);
        TextView lastChangeResultLabel = findViewById(R.id.last_change_result_label);
        lastChangeResultLabel.setText(R.string.last_change_not_found);
        TextView lastChangeByResultLabel = findViewById(R.id.last_change_by_result_label);
        lastChangeByResultLabel.setText(R.string.last_change_by_not_found);
        TextView deactivationTimeResultLabel = findViewById(R.id.deactivation_time_result_label);
        deactivationTimeResultLabel.setText(R.string.last_change_not_found);
    }

    private void populateStatusLabels(String serverResponse) throws JSONException {
        JSONObject serverResponseJson = new JSONObject(serverResponse);
        setStatusLabelText(serverResponseJson);
        setLastChangeLabelText(serverResponseJson);
        setLastChangeByLabelText(serverResponseJson);
        setDeactivationTimeLabelText(serverResponseJson);
        disableAppropriateActionButton(serverResponseJson);
    }

    private void disableAppropriateActionButton(JSONObject serverResponseJson) throws JSONException {
        String status = serverResponseJson.getString(STATUS);
        switch (status) {
            case ACTIVATED:
                findViewById(R.id.activateButton).setEnabled(false);
                findViewById(R.id.deactivateButton).setEnabled(true);
                break;
            case DEACTIVATED:
                findViewById(R.id.activateButton).setEnabled(true);
                findViewById(R.id.deactivateButton).setEnabled(false);
                break;
            case MODIFYING:
                findViewById(R.id.activateButton).setEnabled(false);
                findViewById(R.id.deactivateButton).setEnabled(false);
                break;
            default:
                // Enable both buttons so that actions can be performed as a backup
                findViewById(R.id.activateButton).setEnabled(true);
                findViewById(R.id.deactivateButton).setEnabled(true);
                break;
        }
    }

    private void setStatusLabelText(JSONObject serverResponseJson) throws JSONException {
        String status = serverResponseJson.getString(STATUS);
        TextView statusResultLabel = findViewById(R.id.status_result_label);
        statusResultLabel.setText(status);
        switch (status) {
            case ACTIVATED:
                statusResultLabel.setTextColor(Color.GREEN);
                break;
            case DEACTIVATED:
                statusResultLabel.setTextColor(Color.RED);
                break;
            case MODIFYING:
                statusResultLabel.setTextColor(Color.BLUE);
                break;
            default:
                statusResultLabel.setTextColor(Color.GRAY);
                break;
        }
    }

    private void setLastChangeLabelText(JSONObject serverResponseJson) throws JSONException {
        extractAndSetDateLabel(R.id.last_change_result_label, serverResponseJson, LAST_CHANGE,
                "yyyy-MM-dd HH:mm:ss.n", getString(R.string.last_change_not_found));
    }

    private void extractAndSetDateLabel(int labelId, JSONObject serverResponseJson, String key,
                                        String incomingFormat, String defaultValue) throws JSONException {
        String result = defaultValue;
        String lastChange = serverResponseJson.getString(key);
        try {
            result = normaliseDateFormat(lastChange, incomingFormat);
        }
        catch(DateTimeParseException ignored) {}
        TextView lastChangeResultLabel = findViewById(labelId);
        lastChangeResultLabel.setText(result);
    }

    private void setDeactivationTimeLabelText(JSONObject serverResponseJson) throws JSONException {
        extractAndSetDateLabel(R.id.deactivation_time_result_label, serverResponseJson,
                SCHEDULED_DEACTIVATION, "yyyy-MM-dd HH:mm:ss", getString(R.string.deactivation_time_not_found));
    }

    private String normaliseDateFormat(String dateString, String incomingFormat){
        LocalDateTime dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(incomingFormat));
        DateTimeFormatter outgoingFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd\nHH:mm:ss");
        return dateTime.format(outgoingFormat);
    }

    private void setLastChangeByLabelText(JSONObject serverResponseJson) throws JSONException {
        String lastChangeBy = serverResponseJson.getString(LAST_CHANGE_BY);
        TextView lastChangeByResultLabel = findViewById(R.id.last_change_by_result_label);
        lastChangeByResultLabel.setText(lastChangeBy);
    }

    private void setStatusToggleButtonClickListener(final int buttonId, final String endpoint) {
        findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new PelicanRequest().execute(endpoint);
            }
        });
    }

}
