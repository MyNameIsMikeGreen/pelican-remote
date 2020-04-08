package com.example.pelicanremote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.activateButton).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                new PelicanActivationRequest().execute();
            }
        });
    }
}
