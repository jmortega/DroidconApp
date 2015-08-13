package co.touchlab.droidconandroid.superbus

import android.content.Context
import android.util.Log
import co.touchlab.android.threading.tasks.helper.RetrofitPersistedTask
import co.touchlab.android.threading.tasks.persisted.PersistedTask
import co.touchlab.droidconandroid.network.AddRsvpRequest
import co.touchlab.droidconandroid.network.DataHelper
import com.crashlytics.android.Crashlytics

/**
 * Created by kgalligan on 7/20/14.
 */
class AddRsvpCommandKot(var eventId: Long? = null, var rsvpUuid: String? = null) : RetrofitPersistedTask() {
    override fun runNetwork(context: Context?) {
        val restAdapter = DataHelper.makeRequestAdapter(context)
        val addRsvpRequest = restAdapter!!.create(javaClass<AddRsvpRequest>())

        if (eventId != null && rsvpUuid != null) {
            val basicIdResult = addRsvpRequest!!.addRsvp(eventId!!, rsvpUuid!!)
            Log.w("asdf", "Result id: " + basicIdResult!!.id)
        } else {
            throw IllegalArgumentException("Some value is null: " + eventId + "/" + rsvpUuid)
        }
    }

    override fun logSummary(): String {
        return "AddRsvp - " + eventId
    }

    override fun same(command: PersistedTask): Boolean {
        return false
    }

    override fun handleError(context: Context?, e: Throwable?): Boolean {
        Log.w("asdf", "Whoops", e);
        Crashlytics.logException(e);
        return true;
    }
}