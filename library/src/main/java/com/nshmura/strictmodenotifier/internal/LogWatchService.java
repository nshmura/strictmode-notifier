package com.nshmura.strictmodenotifier.internal;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import com.nshmura.strictmodenotifier.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LogWatchService extends Service {

  private static final String THREAD_NAME = LogWatchService.class.getSimpleName();
  private static final String TAG = THREAD_NAME;

  protected static boolean debugMode;
  protected static boolean headupEnabled = true;

  private static final String LOGCAT_COMMAND = "logcat -v time -s StrictMode:* System.err:*";
  private static final java.lang.String PARSE_REGEXP = "(StrictMode|System.err)(\\([ 0-9]+\\))?:";
  private static final CharSequence EXCEPTION_KEY = "System.err";

  private static final long NOTIFICATION_DELY = 2000; //ms
  private static final long LOG_DELY = 1000; //ms

  private Process proc;
  private final ViolationStore violationStore;
  private List<StrictModeLog> logs = new ArrayList<>();
  private Timer timer = null;
  private ViolationType[] values = ViolationType.values();

  public LogWatchService() {
    violationStore = new ViolationStore(this);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    new Thread(new Runnable() {
      @Override public void run() {
        readLoop();
      }
    }, THREAD_NAME).start();

    return START_STICKY;
  }

  @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    log("onDestroy");

    if (proc != null) {
      proc.destroy();
      proc = null;
    }
  }

  /**
   * notify the StrictModeViolation.
   */
  protected void notifyViolation(StrictModeViolation violation) {

    String notificationTitle;
    if (violation.violationType != null) {
      notificationTitle = violation.violationType.violationName();
    } else {
      notificationTitle = getString(R.string.strictmode_notifier_title);
    }

    StrictModeNotifierInternals.showNotification(this,
        notificationTitle,
        getString(R.string.strictmode_notifier_more_detail),
        headupEnabled,
        StrictModeReportActivity.createPendingIntent(this, violation));
  }

  private void readLoop() {
    log("start readLoop");

    BufferedReader reader = null;

    try {
      //clear log
      Runtime.getRuntime().exec("logcat -c");

      //read only StrictMode error
      proc = Runtime.getRuntime().exec(LOGCAT_COMMAND);
      reader = new BufferedReader(new InputStreamReader(proc.getInputStream()), 1024);

      while (true) {
        String line = reader.readLine();
        if (line != null && line.length() != 0) {
          Log.d(TAG, line);

          StrictModeLog log = parseLine(line);
          if (log != null) {
            storeLog(log);
            startReportTimer();
          }
        } else {
          error("error limit exceeded");
          break;
        }
      }
    } catch (IOException e) {
      error(e.getMessage());
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          //ignore
        }
      }
    }

    Log.d(TAG, "readLoop end");
    stopSelf();
  }

  private StrictModeLog parseLine(String line) {
    String[] split = line.split(PARSE_REGEXP);
    if (split.length < 2) {
      return null;
    }
    if (split[1].equals("null")) {
      return null;
    }
    return new StrictModeLog(split[0], split[1], System.currentTimeMillis());
  }

  private void storeLog(StrictModeLog log) {
    synchronized (this) {
      logs.add(log);
    }
  }

  private void startReportTimer() {
    synchronized (this) {
      if (timer != null) {
        return;
      }
      timer = new Timer(true);
    }

    timer.schedule(new TimerTask() {
      @Override public void run() {
        synchronized (LogWatchService.this) {

          int count = logs.size();
          boolean prevIsAt = false;
          long lastReadTime = 0;
          List<StrictModeLog> targets = new ArrayList<>();

          for (int i = 0; i < count; i++) {
            StrictModeLog log = logs.get(i);

            boolean isAt = log.isAt();
            if (!isAt && prevIsAt && targets.size() > 0) {
              StrictModeViolation report = createViolation(targets);
              if (report != null) {
                notifyViolation(report);
              }
              targets.clear();
            }
            prevIsAt = isAt;
            targets.add(log);
            lastReadTime = log.time;
          }

          if (targets.size() > 0 && System.currentTimeMillis() - lastReadTime >= LOG_DELY) {
            StrictModeViolation report = createViolation(targets);
            if (report != null) {
              notifyViolation(report);
            }
            targets.clear();
          }
          timer = null;

          if (targets.size() > 0) {
            logs = targets;
            startReportTimer();
          } else {
            logs.clear();
          }
        }
      }
    }, NOTIFICATION_DELY);
  }

  private ViolationType getViolationType(List<StrictModeLog> logs) {
    for (StrictModeLog log : logs) {
      for (ViolationType type : values) {
        if (type.detector.detect(log)) {
          return type;
        }
      }
    }
    return null;
  }

  private StrictModeViolation createViolation(List<StrictModeLog> logs) {
    ArrayList<String> stacktreace = new ArrayList<>(logs.size());
    String title = "";
    String logKey = "";
    long time = 0;
    for (StrictModeLog log : logs) {
      if (TextUtils.isEmpty(title)) {
        title = log.message;
        logKey = log.tag;
        time = log.time;
      } else {
        stacktreace.add(log.message);
      }
    }

    ViolationType violationType = getViolationType(logs);
    if (violationType == null && logKey.contains(EXCEPTION_KEY)) {
      return null;
    }

    StrictModeViolation violation = new StrictModeViolation(violationType, title, logKey, stacktreace, time);

    try {
      violationStore.append(violation);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return violation;
  }

  private void log(String message) {
    if (debugMode) {
      Log.d(TAG, message);
    }
  }

  private void error(String message) {
    if (debugMode) {
      Log.e(TAG, message);
    }
  }
}
