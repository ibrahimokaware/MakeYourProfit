package com.rivierasoft.makeyourprofit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notification {
    public static final String CHANNEL_ID = "withdraw_notification_channel";

    public static void displayNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel( CHANNEL_ID, "دخول السحب",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel Description .......");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Get the layouts to use in the custom notification
        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.notification_small);
        RemoteViews notificationLayoutExpanded = new RemoteViews(context.getPackageName(), R.layout.notification_small);

        Intent intent = new Intent(context.getApplicationContext(), SettingsActivity.class).putExtra("s", 3);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.app_icon)
                .setContentTitle("تهانينا...")
                .setLargeIcon(drawableToBitmap(context.getResources().getDrawable(R.drawable.app_logo)))
                //.setContentText("تهانينا! لقد دخلت السحب بنجاح\nانقر للاطلاع على التفاصيل")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                //.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                //.setCustomContentView(notificationLayout)
                //.setCustomBigContentView(notificationLayoutExpanded);
                .setStyle(new NotificationCompat.BigTextStyle().bigText("لقد دخلت السحب بنجاح"))
                .addAction(R.drawable.ic_baseline_card_giftcard_24, "التفاصيل", pendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context.getApplicationContext());
        managerCompat.notify(1, builder.build());
    }

    private static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
