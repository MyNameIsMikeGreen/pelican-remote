package com.example.pelicanremote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    final private int STATUS_POLL_INTERVAL_MILLIS = 3000;
    final Handler STATUS_HANDLER = new Handler();
    final Runnable STATUS_UPDATER_RUNNABLE = statusUpdaterRunnable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusToggleButtonClickListener(R.id.activateButton, getString(R.string.endpoint_activate));
        setStatusToggleButtonClickListener(R.id.deactivateButton, getString(R.string.endpoint_deactivate));

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        STATUS_HANDLER.postDelayed(STATUS_UPDATER_RUNNABLE, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        STATUS_HANDLER.removeCallbacks(STATUS_UPDATER_RUNNABLE);
    }

    private Runnable statusUpdaterRunnable() {
        return new Runnable() {
                public void run() {
                    STATUS_HANDLER.postDelayed(this, STATUS_POLL_INTERVAL_MILLIS);
                    refreshStatus();
                }
            };
    }

    private void refreshStatus() {
        AsyncTask<String, String, String> result = new PelicanRequest().execute(getString(R.string.endpoint_status));
        TextView statusResultLabel = findViewById(R.id.status_result_label);
        try {
            String serverResponse = result.get();
            JSONObject statusJson = new JSONObject(serverResponse);
            String status = statusJson.getString("status");
            statusResultLabel.setText(status);
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
            statusResultLabel.setText(getString(R.string.status_not_connected));
        }
    }

    private void setStatusToggleButtonClickListener(final int buttonId, final String endpoint) {
        findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AsyncTask<String, String, String> result = new PelicanRequest().execute(endpoint);
                String serverResponse = null;
                try {
                    serverResponse = result.get();
                    printResponse(serverResponse);
                } catch (ExecutionException | InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void printResponse(String response) throws JSONException {
        JSONObject statusJson = new JSONObject(response);
        TextView resultBox = findViewById(R.id.resultText);
        resultBox.setText(statusJson.toString(4));
    }

}
