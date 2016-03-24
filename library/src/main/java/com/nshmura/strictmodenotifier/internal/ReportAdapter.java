package com.nshmura.strictmodenotifier.internal;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.nshmura.strictmodenotifier.R;
import java.util.ArrayList;
import java.util.List;

import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;

class ReportAdapter extends BaseAdapter {
  List<StrictModeReport> reports = new ArrayList<>();
  private ReportActivity reportActivity;

  public ReportAdapter(ReportActivity reportActivity) {
    this.reportActivity = reportActivity;
  }

  @Override public int getCount() {
    return reports.size();
  }

  @Override public StrictModeReport getItem(int position) {
    return reports.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    StrictModeReport report = getItem(position);

    if (convertView == null) {
      convertView = LayoutInflater.from(reportActivity)
          .inflate(R.layout.strictmode_notifier_row, parent, false);
      convertView.setTag(new ViewHolder(convertView));
    }

    ViewHolder holder = (ViewHolder) convertView.getTag();
    holder.numberText.setText(String.format("%02d.", getCount() - position));
    holder.dateText.setText(getDateText(report.time));
    holder.titleText.setText(report.title);
    holder.stackTreace.setText(getStacktraceText(report.stacktreace));
    holder.titleText.setVisibility(View.VISIBLE);
    holder.expandedContainer.setVisibility(View.GONE);

    return convertView;
  }

  private String getDateText(long time) {
    return DateUtils.formatDateTime(reportActivity, time, FORMAT_SHOW_TIME | FORMAT_SHOW_DATE);
  }

  public void addAll(List<StrictModeReport> reports) {
    this.reports.addAll(reports);
  }

  private String getStacktraceText(ArrayList<String> stacktreace) {
    StringBuilder builder = new StringBuilder();
    for (String line : stacktreace) {
      if (builder.length() > 0) {
        builder.append("\n");
      }
      builder.append(line);
    }
    return builder.toString();
  }

  private class ViewHolder {

    final TextView numberText;
    final TextView dateText;
    final TextView titleText;
    final View expandedContainer;
    final TextView stackTreace;

    public ViewHolder(View convertView) {
      numberText = (TextView) convertView.findViewById(R.id.number);
      dateText = (TextView) convertView.findViewById(R.id.date);
      titleText = (TextView) convertView.findViewById(R.id.title_text);
      expandedContainer = convertView.findViewById(R.id.expanded_container);
      stackTreace = (TextView) convertView.findViewById(R.id.stacktrace_text);

      convertView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          toggle();
        }
      });

      stackTreace.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          toggle();
        }
      });
    }

    private void toggle() {
      if (expandedContainer.getVisibility() == View.GONE) {
        expandedContainer.setVisibility(View.VISIBLE);
      } else {
        expandedContainer.setVisibility(View.GONE);
      }
    }
  }
}
