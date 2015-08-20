package co.touchlab.droidconandroid.superbus

import android.content.Context
import android.util.Log
import co.touchlab.android.threading.tasks.helper.RetrofitPersistedTask
import co.touchlab.android.threading.tasks.persisted.PersistedTask
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.network.RemoveRsvpRequest
import com.crashlytics.android.Crashlytics

/**
 * Created by kgalligan on 7/20/14.
 */
open class RemoveRsvpCommandKot(var eventId: Long? = null) : RetrofitPersistedTask() {
    override fun logSummary(): String {
        return "RemoveRsvp - " + eventId
    }

    override fun same(command: PersistedTask): Boolean {
        return false
    }

    override fun runNetwork(context: Context?) {
        val restAdapter = DataHelper.makeRequestAdapter(context)
        val removeRsvpRequest = restAdapter!!.create(javaClass<RemoveRsvpRequest>())


        val userUuid = AppPrefs.getInstance(context).getUserUuid()
        if (eventId != null && userUuid != null) {
            removeRsvpRequest!!.removeRsvp(eventId!!)
        } else {
            throw IllegalArgumentException("Some value is null: " + eventId + "/" + userUuid)
        }
    }

    override fun handleError(context: Context?, e: Throwable?): Boolean {
        Log.e("RSVP", "Error removing RSVP eventID: " + eventId , e);
        Crashlytics.logException(e);
        return true;
    }
}