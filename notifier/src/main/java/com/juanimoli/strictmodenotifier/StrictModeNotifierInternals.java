package com.juanimoli.strictmodenotifier;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.O;

final class StrictModeNotifierInternals {

    private static final Executor fileIoExecutor = newSingleThreadExecutor("File-IO");
    private static final String CHANNEL_ID = "strict_mode_notifier_channel";
    private static final String CHANNEL_NAME = "Strict Mode Notifier Notification";
    private static final int NOTIFICATION_ID = 1;

    public static void enableReportActivity(Context context) {
        StrictModeNotifierInternals.setEnabled(context, StrictModeReportActivity.class, true);
    }

    public static void startLogWatchService(Context context,
                                            Class<? extends LogWatchService> serviceClass) {
        Intent intent = new Intent(context, serviceClass);
        context.startService(intent);
    }

    public static void setEnabled(Context context, final Class<?> componentClass,
                                  final boolean enabled) {
        final Context appContext = context.getApplicationContext();
        executeOnFileIoThread(() -> setEnabledBlocking(appContext, componentClass, enabled));
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

    public static void showNotification(Context context, CharSequence contentTitle,
                                        CharSequence contentText, boolean headupEnabled, PendingIntent pendingIntent) {

        if (SDK_INT < JELLY_BEAN) {
            showNotificationFor11(context, contentTitle, contentText, pendingIntent);
        } else if (SDK_INT < O) {
            showNotificationFor16(context, contentTitle, contentText, headupEnabled, pendingIntent);
        } else {
            showNotificationFor26(context, contentTitle, contentText, headupEnabled, pendingIntent);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void showNotificationFor11(Context context, CharSequence contentTitle, CharSequence contentText, PendingIntent pendingIntent) {
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.strictmode_notifier_ic_notification)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        getNotificationManager(context).notify(NOTIFICATION_ID, builder.getNotification());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void showNotificationFor16(Context context, CharSequence contentTitle, CharSequence contentText,
                                              boolean headupEnabled, PendingIntent pendingIntent) {
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.strictmode_notifier_ic_notification)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (SDK_INT >= LOLLIPOP && headupEnabled) {
            builder.setFullScreenIntent(pendingIntent, true);
        }

        getNotificationManager(context).notify(NOTIFICATION_ID, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void showNotificationFor26(Context context, CharSequence contentTitle, CharSequence contentText,
                                              boolean headupEnabled, PendingIntent pendingIntent) {

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        getNotificationManager(context).createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(contentTitle)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.strictmode_notifier_ic_notification)
                .setContentText(contentText)
                .setChannelId(CHANNEL_ID);

        if (headupEnabled) {
            builder = builder.setFullScreenIntent(pendingIntent, true);
        }

        getNotificationManager(context).notify(NOTIFICATION_ID, builder.build());
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
