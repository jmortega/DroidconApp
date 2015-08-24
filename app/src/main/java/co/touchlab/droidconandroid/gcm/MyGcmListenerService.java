package co.touchlab.droidconandroid.gcm;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.gcm.GcmListenerService;

import org.apache.commons.lang3.StringUtils;

import co.touchlab.droidconandroid.EventDetailActivity;
import co.touchlab.droidconandroid.MyActivity;
import co.touchlab.droidconandroid.R;
import co.touchlab.droidconandroid.data.DatabaseHelper;
import co.touchlab.droidconandroid.data.Event;
import co.touchlab.droidconandroid.superbus.RefreshScheduleDataKot;

/**
 * Created by kgalligan on 8/22/15.
 */
public class MyGcmListenerService extends GcmListenerService
{
    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        try
        {
            String gcmType = data.getString("type");
            if(StringUtils.equalsIgnoreCase(gcmType, "version"))
            {
                PackageManager manager = getPackageManager();
                String name = getPackageName();
                PackageInfo pInfo = manager.getPackageInfo(name, 0);

                int versionCode = pInfo.versionCode;
                int checkCode = Integer.parseInt(data.getString("versionCode"));
                if(versionCode < checkCode)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + name));

                    if (intent.resolveActivity(manager) == null) {
                       intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + name));
                    }

                    sendIntentNotification("Droidcon NYC 2015", "Please update your app", intent);
                }
            }
            else if(StringUtils.equalsIgnoreCase(gcmType, "message"))
            {
                String message = data.getString("message");
                Log.d(TAG, "From: " + from);
                Log.d(TAG, "Message: " + message);

                sendNotification(data.getString("title"), message);
            }
            else if(StringUtils.equalsIgnoreCase(gcmType, "updateSchedule"))
            {
                RefreshScheduleDataKot.Companion.callMe(this);
            }
            else if(StringUtils.equalsIgnoreCase(gcmType, "event"))
            {
                String message = data.getString("message");
                long eventId = Long.parseLong(data.getString("eventId"));
                Event event = DatabaseHelper.getInstance(this).getEventDao().queryForId(eventId);
                sendEventNotification(data.getString("title"), message, eventId, event.category);
            }
        }
        catch(Exception e)
        {
            Crashlytics.logException(e);
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MyActivity.class);
        sendIntentNotification(title, message, intent);
    }

    private void sendEventNotification(String title, String message, long eventId, String category)
    {
        Intent intent = EventDetailActivity.Companion.createIntent(this, category, eventId);
        sendIntentNotification(title, message, intent);
    }

    private void sendIntentNotification(String title, String message, Intent intent)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                                                                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(new NotificationCompat.BigTextStyle()
                                  .bigText(message))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
