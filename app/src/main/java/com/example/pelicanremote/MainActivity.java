package com.example.pelicanremote;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

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

        makeResponseBoxScrollable();

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
        TextView statusResultLabel = findViewById(R.id.status_result_label);
        try {
            String serverResponse = result.get();
            String status = new JSONObject(serverResponse).getString("status");
            statusResultLabel.setText(status);
            findViewById(R.id.activateButton).setEnabled(true);
            findViewById(R.id.deactivateButton).setEnabled(true);
            if (status.equals("ACTIVATED")){
                statusResultLabel.setTextColor(Color.GREEN);
            }
            else {
                statusResultLabel.setTextColor(Color.RED);
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
            statusResultLabel.setText(getString(R.string.status_not_connected));
            statusResultLabel.setTextColor(Color.GRAY);
            findViewById(R.id.activateButton).setEnabled(false);
            findViewById(R.id.deactivateButton).setEnabled(false);
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
        TextView resultBox = findViewById(R.id.responseBox);
        resultBox.setText(statusJson.toString(4));
    }

    private void makeResponseBoxScrollable() {
        TextView resultText = findViewById(R.id.responseBox);
        resultText.setMovementMethod(new ScrollingMovementMethod());
    }

}
