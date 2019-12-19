package com.juanimoli.strictmodenotifier.testapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class LeakedClosableObjectsActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, LeakedClosableObjectsActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            OutputStreamWriter output_writer =
                    new OutputStreamWriter(openFileOutput("FILETEST", MODE_PRIVATE));
            BufferedWriter writer = new BufferedWriter(output_writer);
            writer.write("Hi This is my File to test StrictMode");
            //writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        finish();
    }

    private void fireLeakedClosableObjects() {

    }
}
