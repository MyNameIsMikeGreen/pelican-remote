package com.example.pelicanremote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.activateButton).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                new PelicanRequest().execute(getString(R.string.endpoint_activate));
            }
        });

        findViewById(R.id.deactivateButton).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                new PelicanRequest().execute(getString(R.string.endpoint_deactivate));
            }
        });

        findViewById(R.id.statusButton).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                AsyncTask<String, String, String> result = new PelicanRequest().execute(getString(R.string.endpoint_status));
                try {
                    String responseBody = result.get();
                    TextView tv = findViewById(R.id.resultText);
                    tv.setText(responseBody);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
