package com.nshmura.strictmodenotifier.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class ClassInstanceLimitActivity extends AppCompatActivity {

  private static final String EXTRA_COUNT = "EXTRA_COUNT";

  public static void start(Context context, int count) {
    Intent starter = new Intent(context, ClassInstanceLimitActivity.class);
    starter.putExtra(EXTRA_COUNT, count);
    context.startActivity(starter);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Post a message and delay its execution for 10 minutes.
    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
      }
    }, 1000 * 60 * 10);

    int count = getIntent().getIntExtra(EXTRA_COUNT, 0);
    if (count > 0) {
      start(this, --count);
    }

    finish();
  }
}
