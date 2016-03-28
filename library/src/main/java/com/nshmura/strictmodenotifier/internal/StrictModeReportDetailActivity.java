package com.nshmura.strictmodenotifier.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import com.nshmura.strictmodenotifier.R;

public class StrictModeReportDetailActivity extends Activity {

  private static final String EXTRA_REPORT = "EXTRA_REPORT";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.strictmode_notifier_activity_report_detail);

    StrictModeReport report = (StrictModeReport) getIntent().getSerializableExtra(EXTRA_REPORT);

    TextView stackTreace = (TextView) findViewById(R.id.__stacktrace_text);
    stackTreace.setText(String.format("%s\n%s", report.note, report.getStacktraceText()));

    if (report.violationType != null) {
      ReportActivityUtils.setTitle(this, report.violationType.violationName());
      ReportActivityUtils.setDisplayHomeAsUpEnabled(this, true);
    }
  }

  @Override public boolean onMenuItemSelected(int featureId, MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        break;
    }
    return super.onMenuItemSelected(featureId, item);
  }


  public static void start(Context context, StrictModeReport report) {
    context.startActivity(createIntent(context, report));
  }

  public static Intent createIntent(Context context, StrictModeReport report) {
    Intent intent = new Intent(context, StrictModeReportDetailActivity.class);
    intent.putExtra(EXTRA_REPORT, report);
    return intent;
  }
}