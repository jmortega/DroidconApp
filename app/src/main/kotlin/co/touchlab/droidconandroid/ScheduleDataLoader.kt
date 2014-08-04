package co.touchlab.droidconandroid

import co.touchlab.android.threading.loaders.AbstractEventBusLoader
import co.touchlab.droidconandroid.data.Event
import android.content.Context
import java.util.Collections
import java.util.Comparator
import java.util.ArrayList
import co.touchlab.droidconandroid.data.DatabaseHelper
import co.touchlab.droidconandroid.tasks.RemoveRsvpTaskKot
import co.touchlab.droidconandroid.tasks.AddRsvpTaskKot
import co.touchlab.droidconandroid.superbus.RefreshScheduleDataKot

/**
 * Created by kgalligan on 8/1/14.
 */
class ScheduleDataLoader(c: Context, val all: Boolean) : AbstractEventBusLoader<List<Event>>(c)
{
    override fun findContent(): List<Event>?
    {
        val dao = DatabaseHelper.getInstance(getContext()).getEventDao()
        val events = if(all)
        {
            dao.queryForAll()!!
        }
        else
        {
            dao.queryBuilder()!!.where()!!.isNotNull("rsvpUuid")!!.query()!!
        }

        Collections.sort(events, object : Comparator<Event>
        {
            override fun compare(lhs: Event, rhs: Event): Int
            {
                if (lhs.venue.id != rhs.venue.id)
                {
                    return lhs.venue.name!!.compareTo(rhs.venue.name!!)
                }

                if (lhs.startDateLong == rhs.startDateLong)
                    return 0

                return lhs.startDateLong!!.compareTo(rhs.startDateLong!!)
            }
        })

        return ArrayList(events)
    }

    override fun handleError(e: Exception?): Boolean
    {
        return false
    }

    public fun onEvent(task: AddRsvpTaskKot)
    {
        onContentChanged()
    }

    public fun onEvent(task: RemoveRsvpTaskKot)
    {
        onContentChanged()
    }

    public fun onEvent(task: RefreshScheduleDataKot)
    {
        onContentChanged()
    }

}