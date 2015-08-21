package co.touchlab.droidconandroid

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder

/**
 * Created by samuelhill on 8/20/15.
 */

class SetAlarmReceiver(): BroadcastReceiver()
{
    public val ACTION_KEYNOTE: String = "co.touchlab.droidconandroid.action.KEYNOTE"
    public val ACTION_PREGAME: String = "co.touchlab.droidconandroid.action.PREGAME"
    public val ACTION_BOOT: String = "android.intent.action.BOOT_COMPLETED"

    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_PREGAME.equals(intent.getAction()))
            addNotification(context, "DroidCon starts tomorrow! Remember to update your profile and RSVP to talks!")
        else if (ACTION_KEYNOTE.equals(intent.getAction()))
            addNotification(context, "Welcome to DroidCon. Be sure to check out our Code Labs")
        else if (ACTION_BOOT.equals(intent.getAction()))
            setAllAlarms(context)
    }

    public fun setAllAlarms(context: Context)
    {
//        var cal = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
//        cal.set(2015, Calendar.AUGUST, 26, 7, 0)
//        setAlarm(context, cal.getTimeInMillis(), ACTION_PREGAME)
//
//        cal.set(2015, Calendar.AUGUST, 27, 10, 0)
//        setAlarm(context, cal.getTimeInMillis(), ACTION_KEYNOTE)
    }

    public fun setAlarm(context: Context, time: Long, action: String)
    {
        var manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent(action, null, context, javaClass<SetAlarmReceiver>())
        var pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        if (time > System.currentTimeMillis()) {
            manager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        }
    }

    public fun addNotification(context: Context, content: String)
    {
        var intent =  Intent(context, javaClass<MyActivity>())
        var stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(javaClass<MyActivity>())
        stackBuilder.addNextIntent(intent)
        var pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        var builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("DroidCon")
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(content))


        var manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, builder.build())
    }

}
