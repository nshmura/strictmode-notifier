package com.juanimoli.strictmodenotifier.demo;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.fire_error).setOnClickListener(v -> StrictMode.noteSlowCall("fireSlowCall"));
    }
}
