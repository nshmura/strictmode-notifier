package com.nshmura.strictmodenotifier.internal;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import com.nshmura.strictmodenotifier.R;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;

public final class StrictModeNotifierInternals {

  private static final Executor fileIoExecutor = newSingleThreadExecutor("File-IO");

  public static void setEnabled(Context context, final Class<?> componentClass,
      final boolean enabled) {
    final Context appContext = context.getApplicationContext();
    executeOnFileIoThread(new Runnable() {
      @Override public void run() {
        setEnabledBlocking(appContext, componentClass, enabled);
      }
    });
  }

  public static void setEnabledBlocking(Context appContext, Class<?> componentClass,
      boolean enabled) {
    ComponentName component = new ComponentName(appContext, componentClass);
    PackageManager packageManager = appContext.getPackageManager();
    int newState = enabled ? COMPONENT_ENABLED_STATE_ENABLED : COMPONENT_ENABLED_STATE_DISABLED;
    // Blocks on IPC.
    packageManager.setComponentEnabledSetting(component, newState, DONT_KILL_APP);
  }

  public static void executeOnFileIoThread(Runnable runnable) {
    fileIoExecutor.execute(runnable);
  }

  public static Executor newSingleThreadExecutor(String threadName) {
    return Executors.newSingleThreadExecutor(new StrictModeNotifierSingleThreadFactory(threadName));
  }

  @TargetApi(HONEYCOMB)
  public static void showNotification(Context context, CharSequence contentTitle,
      CharSequence contentText, PendingIntent pendingIntent) {
    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    Notification notification;
    if (SDK_INT < HONEYCOMB) {
      notification = new Notification();
      notification.icon = R.drawable.strictmode_notifier_ic_notification;
      notification.when = System.currentTimeMillis();
      notification.flags |= Notification.FLAG_AUTO_CANCEL;
      try {
        Method method =
            Notification.class.getMethod("setLatestEventInfo", Context.class, CharSequence.class,
                CharSequence.class, PendingIntent.class);
        method.invoke(notification, context, contentTitle, contentText, pendingIntent);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else {
      Notification.Builder builder = new Notification.Builder(context) //
          .setSmallIcon(R.drawable.strictmode_notifier_ic_notification)
          .setWhen(System.currentTimeMillis())
          .setContentTitle(contentTitle)
          .setContentText(contentText)
          .setAutoCancel(true)
          .setContentIntent(pendingIntent);
      if (SDK_INT < JELLY_BEAN) {
        notification = builder.getNotification();
      } else {
        notification = builder.build();
      }
    }
    notificationManager.notify(0xDEAFBEEF, notification);
  }
}
