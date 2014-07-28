package co.touchlab.droidconandroid.tasks

import android.content.Context
import de.greenrobot.event.EventBus
import co.touchlab.droidconandroid.data.Event
import java.util.Comparator
import java.util.Collections
import java.util.ArrayList
import co.touchlab.droidconandroid.data.UserAccount

/**
 * Created by kgalligan on 7/20/14.
 */
open class EventDetailLoadTask(c: Context, val eventId: Long) : DatabaseTaskKot(c)
{
    var event: Event? = null
    var speakers: MutableList<UserAccount>? = null

    override fun run(context: Context?)
    {
        val dao = databaseHelper.getEventDao()
        event = dao.queryForId(eventId)

        val eventSpeakerDao = databaseHelper.getEventSpeakerDao()
        val results = eventSpeakerDao.queryBuilder()!!.where()!!.eq("event_id", eventId)!!.query()!!

        speakers = ArrayList(results.size)

        for (eventSpeaker in results)
        {
            speakers!!.add(eventSpeaker.userAccount!!)
        }

        EventBus.getDefault()?.post(this)
    }

    override fun handleError(e: Exception?): Boolean
    {
        return false
    }
}