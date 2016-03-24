package com.nshmura.strictmodenotifier.internal;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import com.nshmura.strictmodenotifier.R;
import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class ReportActivity extends Activity {

  private ReportAdapter adapter;
  private ReportStore reportStore;

  public static PendingIntent createPendingIntent(Context context, StrictModeReport report) {
    Intent intent = new Intent(context, ReportActivity.class);
    //intent.putExtra(SHOW_LEAK_EXTRA, referenceKey);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    return PendingIntent.getActivity(context, 1, intent, FLAG_UPDATE_CURRENT);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.strictmode_notifier_activity);

    adapter = new ReportAdapter(this);
    ListView listView = (ListView) findViewById(R.id.list_view);
    //noinspection ConstantConditions
    listView.setAdapter(adapter);

    reportStore = new ReportStore(this);
    List<StrictModeReport> reports = reportStore.getAll();
    adapter.addAll(reports);
  }
}
