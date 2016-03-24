package com.nshmura.strictmodenotifier.internal;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.nshmura.strictmodenotifier.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LogWatchService extends IntentService {

  private static final long NOTIFICATION_DELY = 1000; //ms
  private final ReportStore reportStore;

  private Map<String, List<StrictModeLog>> logsMap = new HashMap<>();
  private Map<String, Timer> timersMap = new HashMap<>();

  public LogWatchService() {
    this(LogWatchService.class.getSimpleName());
  }

  public LogWatchService(String name) {
    super(name);
    reportStore = new ReportStore(this);
  }

  @Override protected void onHandleIntent(Intent intent) {
    BufferedReader reader = null;
    try {
      //clear log
      Runtime.getRuntime().exec("logcat -c");

      //read only StrictMode error
      Process proc = Runtime.getRuntime().exec("logcat -v time -s StrictMode:*");
      reader = new BufferedReader(new InputStreamReader(proc.getInputStream()), 1024);

      //noinspection InfiniteLoopStatement
      while (true) {
        String line = reader.readLine();
        if (line != null && line.length() != 0) {
          Log.d("LogWatch", line);

          StrictModeLog log = parseLine(line);
          if (log != null) {
            synchronized (this) {
              storeLog(log);
              startReportTimer(log);
            }
          }
        } else {
          sleep();
        }
      }
    } catch (IOException e) {
      error("StrictModeNotificator error");
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          //ignore
        }
      }
    }
  }

  private StrictModeLog parseLine(String line) {
    String[] split = line.split("StrictMode(\\([ 0-9]+\\))?: ");
    if (split.length < 2) {
      return null;
    }
    return new StrictModeLog(split[0], split[1], System.currentTimeMillis());
  }

  private void storeLog(StrictModeLog log) {
    List<StrictModeLog> logs = logsMap.get(log.header);
    if (logs == null) {
      logs = new ArrayList<>();
    }
    logs.add(log);
    logsMap.put(log.header, logs);
  }

  private void startReportTimer(final StrictModeLog log) {
    if (timersMap.get(log.header) != null) {
      return;
    }

    Timer timer = new Timer(true);
    timer.schedule(new TimerTask() {
      @Override public void run() {
        synchronized (LogWatchService.this) {
          List<StrictModeLog> logs = logsMap.get(log.header);
          logsMap.remove(log.header);
          timersMap.remove(log.header);
          reportLog(logs);
        }
      }
    }, NOTIFICATION_DELY);

    timersMap.put(log.header, timer);
  }

  private void reportLog(List<StrictModeLog> logs) {

    ArrayList<String> stacktreace = new ArrayList<>(logs.size());
    String title = "";
    String logKey = "";
    long time = 0;
    for (StrictModeLog log : logs) {
      if (TextUtils.isEmpty(title)) {
        title = log.message;
        logKey = log.header;
        time = log.time;
      } else {
        stacktreace.add(log.message);
      }
    }
    StrictModeReport report = new StrictModeReport(title, logKey, stacktreace, time);

    try {
      reportStore.append(report);
    } catch (IOException e) {
      e.printStackTrace(); //TODO
    }

    StrictModeNotifierInternals.showNotification(this, getString(R.string.strictmode_notifier_title),
        report.title, ReportActivity.createPendingIntent(this, report));
  }

  private void sleep() {
    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
      //ignore
    }
  }

  private void error(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }
}
