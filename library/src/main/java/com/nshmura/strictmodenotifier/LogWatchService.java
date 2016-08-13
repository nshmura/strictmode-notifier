package com.nshmura.strictmodenotifier;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LogWatchService extends IntentService {

  private static final String THREAD_NAME = LogWatchService.class.getSimpleName();
  private static final String TAG = THREAD_NAME;

  private static final String LOGCAT_COMMAND = "logcat -v time -s StrictMode:* System.err:*";
  private static final java.lang.String PARSE_REGEXP = "(StrictMode|System.err)(\\([ 0-9]+\\))?:";
  private static final CharSequence EXCEPTION_KEY = "System.err";

  private static final long NOTIFICATION_DELAY = 2000; //ms
  private static final long LOG_DELAY = 1000; //ms
  private static final long EXIT_SPAN = 1000 * 60; //ms

  private NotifierConfig notifierConfig = NotifierConfig.getInstance();
  private Process proc;
  private final ViolationStore violationStore;
  private List<StrictModeLog> logs = new ArrayList<>();
  private Timer timer = null;
  private ViolationType[] violationTypes = ViolationType.values();

  public LogWatchService() {
    this(TAG);
  }

  public LogWatchService(String name) {
    super(name);
    violationStore = new ViolationStore(this);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
    return START_STICKY;
  }

  @Override protected void onHandleIntent(Intent intent) {
    startReadLoop();
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
   *
   * @param violation StrictModeViolation
   */
  protected void notifyViolation(StrictModeViolation violation) {
    //Custom Actions
    List<CustomAction> customActions = notifierConfig.getCustomActions();
    for (CustomAction customAction : customActions) {
      customAction.onViolation(violation);
    }

    //Default Action
    String notificationTitle;
    if (violation.violationType != null) {
      notificationTitle = ViolationTypeInfo.convert(violation.violationType).violationName();
    } else {
      notificationTitle = getString(R.string.strictmode_notifier_title, getPackageName());
    }
    StrictModeNotifierInternals.showNotification(this, notificationTitle,
        getString(R.string.strictmode_notifier_more_detail), notifierConfig.isHeadupEnabled(),
        StrictModeReportActivity.createPendingIntent(this, violation));
  }

  private void startReadLoop() {
    while (true) {
      long startTime = System.currentTimeMillis();

      log("start readLoop");

      readLoop();

      log("end readLoop");

      if (System.currentTimeMillis() - startTime <= EXIT_SPAN) {
        log("exit readLoop");
        break;
      }
    }
  }

  private void readLoop() {
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
          log(line);

          StrictModeLog log = parseLine(line);
          if (log != null) {
            storeLog(log);
            startReportTimer();
          }
        } else {
          error("error readLoop");
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
              if (report != null && StringModeConfig.from(LogWatchService.this).isEnabled()) {
                storeViolation(report);
                notifyViolation(report);
              }
              targets.clear();
            }
            prevIsAt = isAt;
            targets.add(log);
            lastReadTime = log.time;
          }

          if (targets.size() > 0 && System.currentTimeMillis() - lastReadTime >= LOG_DELAY) {
            StrictModeViolation report = createViolation(targets);
            if (report != null && StringModeConfig.from(LogWatchService.this).isEnabled()) {
              storeViolation(report);
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
    }, NOTIFICATION_DELAY);
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
      }
      stacktreace.add(log.message);
    }

    ViolationType violationType = getViolationType(logs);
    if (violationType == ViolationType.UNKNOWN && logKey.contains(EXCEPTION_KEY)) {
      return null;
    }

    StrictModeViolation violation =
        new StrictModeViolation(violationType, title, logKey, stacktreace, time);

    //Ignore Action
    if (notifierConfig.getIgnoreAction() != null) {
      if (notifierConfig.getIgnoreAction().ignore(violation)) {
        return null;
      }
    }

    return violation;
  }

  private void storeViolation(StrictModeViolation violation) {
    try {
      violationStore.append(violation);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private ViolationType getViolationType(List<StrictModeLog> logs) {
    for (StrictModeLog log : logs) {
      for (ViolationType type : violationTypes) {
        ViolationTypeInfo info = ViolationTypeInfo.convert(type);
        if (info != null && info.detector.detect(log)) {
          return type;
        }
      }
    }
    return ViolationType.UNKNOWN;
  }

  private void log(String message) {
    if (notifierConfig.isDebugMode()) {
      Log.d(TAG, message);
    }
  }

  private void error(String message) {
    if (notifierConfig.isDebugMode()) {
      Log.e(TAG, message);
    }
  }
}