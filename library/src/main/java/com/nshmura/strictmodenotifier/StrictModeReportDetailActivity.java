package com.nshmura.strictmodenotifier;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class StrictModeReportDetailActivity extends Activity {

  private static final String EXTRA_REPORT = "EXTRA_REPORT";

  private ViolationStore violationStore;
  private StrictModeViolation report;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.strictmode_notifier_activity_report_detail);

    violationStore = new ViolationStore(StrictModeReportDetailActivity.this);

    report = (StrictModeViolation) getIntent().getSerializableExtra(EXTRA_REPORT);

    TextView stackTreace = (TextView) findViewById(R.id.__stacktrace_text);

    final String stacktraceText = report.getStacktraceText();
    final Spannable span = new SpannableString(stacktraceText);

    setColor(span, stacktraceText, getPackageName(), Color.WHITE);

    stackTreace.setText(span);

    if (report.violationType != null) {
      ReportActivityUtils.setTitle(this,
          ViolationTypeInfo.convert(report.violationType).violationName());
      ReportActivityUtils.setDisplayHomeAsUpEnabled(this, true);
    }

    findViewById(R.id.__delete_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        violationStore.remove(report);
        finish();
      }
    });
  }

  @Override public boolean onMenuItemSelected(int featureId, MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        break;
    }
    return super.onMenuItemSelected(featureId, item);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.strictmode_notifier_menu, menu);

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      menu.removeItem(R.id.__menu_copy);
    }

    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == R.id.__menu_copy) {
      copy();
      return true;
    } else if (itemId == R.id.__menu_share) {
      share();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) private void copy() {
    ClipData clip = ClipData.newPlainText("info", shareText());
    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    clipboard.setPrimaryClip(clip);
    Toast.makeText(this, R.string.strictmode_notifier_copyped, Toast.LENGTH_LONG).show();
  }

  private void share() {
    Intent sendIntent = new Intent();
    sendIntent.setAction(Intent.ACTION_SEND);
    sendIntent.putExtra(Intent.EXTRA_TEXT, shareText());
    sendIntent.setType("text/plain");

    try {
      startActivity(Intent.createChooser(sendIntent,
          getResources().getText(R.string.strictmode_notifier_menu_share)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String shareText() {
    String shareText;
    if (report.violationType != null) {
      shareText = ViolationTypeInfo.convert(report.violationType).violationName();
      shareText += "\n\n" + report.getStacktraceText();
    } else {
      shareText = report.getStacktraceText();
    }
    return shareText;
  }

  public static void start(Context context, StrictModeViolation report) {
    context.startActivity(createIntent(context, report));
  }

  public static Intent createIntent(Context context, StrictModeViolation report) {
    Intent intent = new Intent(context, StrictModeReportDetailActivity.class);
    intent.putExtra(EXTRA_REPORT, report);
    return intent;
  }

  public void setColor(Spannable span, String stacktraceText, String hilight, int color) {
    final int length = hilight.length();

    int start = 0;
    while (true) {
      int index = stacktraceText.indexOf(hilight, start);
      if (index < 0) {
        break;
      }

      span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), index, index + length,
          Spannable.SPAN_INCLUSIVE_INCLUSIVE);

      span.setSpan(new ForegroundColorSpan(color), index, index + length,
          Spannable.SPAN_INCLUSIVE_INCLUSIVE);

      start = index + length;
    }
  }
}