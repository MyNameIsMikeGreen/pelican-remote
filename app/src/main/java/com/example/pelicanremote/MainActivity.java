package com.example.pelicanremote;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

import static com.example.pelicanremote.PelicanUrlBuilder.Endpoint;

public class MainActivity extends AppCompatActivity {

    private final static int STATUS_POLL_INTERVAL_MILLIS = 3000;

    private final PelicanUrlBuilder urlBuilder = new PelicanUrlBuilder("http", "192.168.5.80", 8000);
    private final Handler statusHandler = new Handler();
    private final Runnable statusUpdaterRunnable = statusUpdaterRunnable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusToggleButtonClickListener(R.id.activateButton, urlBuilder.build(Endpoint.ACTIVATE));
        setStatusToggleButtonClickListener(R.id.deactivateButton, urlBuilder.build(Endpoint.DEACTIVE));

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        statusHandler.postDelayed(statusUpdaterRunnable, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        statusHandler.removeCallbacks(statusUpdaterRunnable);
    }

    private Runnable statusUpdaterRunnable() {
        return new Runnable() {
                public void run() {
                    statusHandler.postDelayed(this, STATUS_POLL_INTERVAL_MILLIS);
                    refreshStatus();
                }
            };
    }

    private void refreshStatus() {
        AsyncTask<String, String, String> result = new PelicanRequest().execute(urlBuilder.build(Endpoint.STATUS));

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
        lastChangeByResultLabel.setText(R.string.last_change_not_found);
    }

    private void populateStatusLabels(String serverResponse) throws JSONException {
        JSONObject serverResponseJson = new JSONObject(serverResponse);
        setStatusLabelText(serverResponseJson);
        setLastChangeLabelText(serverResponseJson);
        setLastChangeByLabelText(serverResponseJson);
        disableAppropriateActionButton(serverResponseJson);
    }

    private void disableAppropriateActionButton(JSONObject serverResponseJson) throws JSONException {
        String status = serverResponseJson.getString("status");
        if (status.equals("ACTIVATED")){
            findViewById(R.id.activateButton).setEnabled(false);
            findViewById(R.id.deactivateButton).setEnabled(true);
        }
        if (status.equals("DEACTIVATED")){
            findViewById(R.id.activateButton).setEnabled(true);
            findViewById(R.id.deactivateButton).setEnabled(false);
        }
        else {
            // Enable both buttons so that actions can be performed as a backup
            findViewById(R.id.activateButton).setEnabled(true);
            findViewById(R.id.deactivateButton).setEnabled(true);
        }
    }

    private void setStatusLabelText(JSONObject serverResponseJson) throws JSONException {
        String status = serverResponseJson.getString("status");
        TextView statusResultLabel = findViewById(R.id.status_result_label);
        statusResultLabel.setText(status);
        if (status.equals("ACTIVATED")){
            statusResultLabel.setTextColor(Color.GREEN);
        }
        else {
            statusResultLabel.setTextColor(Color.RED);
        }
    }

    private void setLastChangeLabelText(JSONObject serverResponseJson) throws JSONException {
        String lastChange = serverResponseJson.getString("lastChange");
        DateTimeFormatter incomingFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.n");
        LocalDateTime dateTime = LocalDateTime.parse(lastChange, incomingFormat);
        DateTimeFormatter outgoingFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd @ HH:mm:ss");
        TextView lastChangeResultLabel = findViewById(R.id.last_change_result_label);
        lastChangeResultLabel.setText(dateTime.format(outgoingFormat));
    }

    private void setLastChangeByLabelText(JSONObject serverResponseJson) throws JSONException {
        String lastChangeBy = serverResponseJson.getString("lastChangeBy");
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
