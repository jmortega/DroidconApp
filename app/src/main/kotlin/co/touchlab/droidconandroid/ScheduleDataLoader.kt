package co.touchlab.droidconandroid

import android.content.Context
import android.text.format.DateUtils
import co.touchlab.android.threading.loaders.AbstractEventBusLoader
import co.touchlab.droidconandroid.data.DatabaseHelper
import co.touchlab.droidconandroid.data.Event
import co.touchlab.droidconandroid.superbus.RefreshScheduleDataKot
import co.touchlab.droidconandroid.tasks.AddRsvpTaskKot
import co.touchlab.droidconandroid.tasks.RemoveRsvpTaskKot
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

/**
 * Created by kgalligan on 8/1/14.
 */
class ScheduleDataLoader(c: Context, val all: Boolean, val day: Long) : AbstractEventBusLoader<List<Event>>(c)
{
    override fun findContent(): List<Event>?
    {
        val dao = DatabaseHelper.getInstance(getContext()).getEventDao()
        val baseQuery = dao.queryBuilder().where().between("startDateLong", day, day + DateUtils.DAY_IN_MILLIS)
        val events = if(all)
        {
            baseQuery.query()!!
        }
        else
        {
            baseQuery.and().isNotNull("rsvpUuid")!!.query()!!
        }

        Collections.sort(events, object : Comparator<Event>
        {
            override fun compare(lhs: Event, rhs: Event): Int
            {
//                if (lhs.venue.id != rhs.venue.id)
//                {
//                    return lhs.venue.name!!.compareTo(rhs.venue.name!!)
//                }

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