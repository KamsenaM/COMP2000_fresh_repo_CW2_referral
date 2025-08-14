package com.example.librarymanager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    public static final String CHANNEL_ID = "library_channel";

    private static final String PREFS_NAME = "notification_prefs";
    private static final String KEY_BOOK_REQUEST_UPDATES = "book_request_updates";
    private static final String KEY_LIBRARY_ANNOUNCEMENTS = "library_announcements";


    public static void createChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Library Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = ctx.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void sendNotification(Context ctx, String title, String message, String type) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean allowBookUpdates = prefs.getBoolean(KEY_BOOK_REQUEST_UPDATES, true);
        boolean allowAnnouncements = prefs.getBoolean(KEY_LIBRARY_ANNOUNCEMENTS, true);

        if (type.equalsIgnoreCase("book_request") && !allowBookUpdates) return;
        if (type.equalsIgnoreCase("announcement") && !allowAnnouncements) return;

        if (Build.VERSION.SDK_INT >= 33 &&
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat.from(ctx)
                .notify((int) System.currentTimeMillis(), builder.build());
    }
}
