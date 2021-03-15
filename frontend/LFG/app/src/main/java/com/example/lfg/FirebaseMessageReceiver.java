package com.example.lfg;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class FirebaseMessageReceiver
        extends FirebaseMessagingService {

    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("FirebaseMessageReceiver", remoteMessage.toString());
        Log.i("FirebaseMessageReceiver", Objects.requireNonNull(remoteMessage.getNotification()).toString());
        // Second case when notification payload is received.
        if (remoteMessage.getNotification() != null) {
            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    // Method to display the notifications
    public void showNotification(String title, String message) {
        // Assign channel ID
        String CLOUD_CHANNEL_ID = "cloud_channel";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CLOUD_CHANNEL_ID)
                .setSmallIcon(R.drawable.other)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);

        // Create an object of NotificationManager class to notify the user of events that happen in the background.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel cloud_messaging = new NotificationChannel(CLOUD_CHANNEL_ID, "cloud messaging", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(cloud_messaging);
        }

        notificationManager.notify(0, builder.build());
    }
}