package app.shouoff.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import app.shouoff.NotificationPostDetails;
import app.shouoff.R;
import app.shouoff.UserProfile;
import app.shouoff.activity.HomeActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private static final String TAG = "FCM";
    private Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        Log.e(TAG, "From: " + remoteMessage.getData());
        Log.e(TAG, "Notification Message Body: " + remoteMessage.getData().get("data"));

        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();
            Log.e(key + " : ", value);
        }

        if (remoteMessage.getData().get("notification_type").equalsIgnoreCase("Registration"))
        {
            /*Move home page*/
            intent = new Intent(this, HomeActivity.class);
        }
        else if (remoteMessage.getData().get("notification_type").equalsIgnoreCase("like")
                ||remoteMessage.getData().get("notification_type").equalsIgnoreCase("winner")
                ||remoteMessage.getData().get("notification_type").equalsIgnoreCase("Qualify Posts")
                ||remoteMessage.getData().get("notification_type").equalsIgnoreCase("comment_tag")
                ||remoteMessage.getData().get("notification_type").equalsIgnoreCase("post_rejected")
                ||remoteMessage.getData().get("notification_type").equalsIgnoreCase("post_accepted")
                ||remoteMessage.getData().get("notification_type").equalsIgnoreCase("post_tagging")
                ||remoteMessage.getData().get("notification_type").equalsIgnoreCase("comment")
                ||remoteMessage.getData().get("notification_type").equalsIgnoreCase("share"))
        {
            /*Move on Notification Post Details*/
            intent = new Intent(this, NotificationPostDetails.class)
                    .putExtra("notification_type",remoteMessage.getData().get("notification_type"))
                    .putExtra("sender_id",remoteMessage.getData().get("sender_id"))
                    .putExtra("post_id",remoteMessage.getData().get("post_id"));
        }
        else if (remoteMessage.getData().get("notification_type").equalsIgnoreCase("admire"))
        {
            /*Move on User profile*/
            intent = new Intent(this, UserProfile.class)
                    .putExtra("notification","notification")
                    .putExtra("notification_type",remoteMessage.getData().get("notification_type"))
                    .putExtra("profile_id",remoteMessage.getData().get("sender_id"))
                    .putExtra("post_id",remoteMessage.getData().get("post_id"));
        }
        else if ( remoteMessage.getData().get("notification_type").equalsIgnoreCase("invite"))
        {
            /*Move on User profile*/
            intent = new Intent(this, UserProfile.class)
                    .putExtra("invite","invite")
                    .putExtra("notification","notification")
                    .putExtra("title",remoteMessage.getData().get("title"))
                    .putExtra("notification_type",remoteMessage.getData().get("notification_type"))
                    .putExtra("profile_id",remoteMessage.getData().get("sender_id"))
                    .putExtra("post_id",remoteMessage.getData().get("post_id"));
        }

        sendNotification(remoteMessage.getData().get("sender_name")+" "+
                remoteMessage.getData().get("title"),getApplicationContext());
    }


    private void sendNotification(String messageBody, Context context)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder mBuilder = new NotificationCompat
                .Builder(this);
        NotificationChannel mChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            mChannel = new NotificationChannel
                    ("showuoff", messageBody, NotificationManager.IMPORTANCE_HIGH);
        }

        Notification notification = mBuilder
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messageBody))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(getNotificationIcon(mBuilder,context))
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(messageBody)
                .setChannelId("showuoff")
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager
                .notify(1, notification);
    }

    private static int getNotificationIcon(NotificationCompat.Builder notificationBuilder, Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            int color = ContextCompat.getColor(context, R.color.app_color);
            notificationBuilder.setColor(color);
            return R.mipmap.new_app_round;
        } else {
            return R.mipmap.new_app_round;
        }
    }
}