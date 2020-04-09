package com.example.pelicanremote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusRefreshButtonClickListener();
        setStatusToggleButtonClickListener(R.id.activateButton, getString(R.string.endpoint_activate));
        setStatusToggleButtonClickListener(R.id.deactivateButton, getString(R.string.endpoint_deactivate));

    }

    private void setStatusRefreshButtonClickListener() {
        findViewById(R.id.refreshStatusButton).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                AsyncTask<String, String, String> result = new PelicanRequest().execute(getString(R.string.endpoint_status));
                try {
                    String serverResponse = result.get();
                    printResponse(serverResponse);
                    JSONObject statusJson = new JSONObject(serverResponse);
                    String status = statusJson.getString("status");
                    TextView statusResultLabel = findViewById(R.id.status_result_label);
                    statusResultLabel.setText(status);
                } catch (ExecutionException | InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
