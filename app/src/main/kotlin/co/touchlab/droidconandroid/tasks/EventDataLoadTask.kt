package co.touchlab.droidconandroid.tasks

import android.content.Context
import de.greenrobot.event.EventBus
import co.touchlab.droidconandroid.data.Event
import java.util.Comparator
import java.util.Collections
import java.util.ArrayList

/**
 * Created by kgalligan on 7/20/14.
 */
open class EventDataLoadTask(c: Context) : DatabaseTask(c)
{
    var events: List<Event> = ArrayList()

    override fun run(context: Context?)
    {
        val dao = getDatabase().getEventDao()
        val events = dao.queryForAll()!!

        Collections.sort(events, object : Comparator<Event>
        {
            override fun compare(lhs: Event, rhs: Event): Int
            {
                if (lhs.venue.id != rhs.venue.id)
                {
                    return lhs.venue.name!!.compareTo(rhs.venue.name!!)
                }

                if(lhs.startDateLong == rhs.startDateLong)
                    return 0

                return lhs.startDateLong!!.compareTo(rhs.startDateLong!!)
            }
        })

        this.events = ArrayList(events)

        EventBus.getDefault()?.post(this)
    }

    override fun handleError(e: Exception?): Boolean
    {
        return false
    }
}