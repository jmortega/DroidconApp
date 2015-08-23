package co.touchlab.droidconandroid

import android.content.Context
import android.text.format.DateUtils
import co.touchlab.android.threading.loaders.AbstractEventBusLoader
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.data.DatabaseHelper
import co.touchlab.droidconandroid.data.ScheduleBlock
import co.touchlab.droidconandroid.superbus.RefreshScheduleDataKot
import co.touchlab.droidconandroid.tasks.AddRsvpTaskKot
import co.touchlab.droidconandroid.tasks.RemoveRsvpTaskKot
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

/**
 * Created by kgalligan on 8/1/14.
 */
class ScheduleDataLoader(val c: Context, val all: Boolean, val day: Long) : AbstractEventBusLoader<List<ScheduleBlock>>(c)
{
    override fun findContent(): List<ScheduleBlock>?
    {
        val databaseHelper = DatabaseHelper.getInstance(getContext())
        val eventDao = databaseHelper.getEventDao()
        val blockDao = databaseHelper.getBlockDao()
        val baseQuery = eventDao.createWhere().between("startDateLong", day, day + DateUtils.DAY_IN_MILLIS)
        val events = if(all)
        {
            baseQuery.query()!!
        }
        else
        {
            baseQuery.and().isNotNull("rsvpUuid")!!.query()!!
        }

        val blocks = blockDao.createWhere().between("startDateLong", day, day + DateUtils.DAY_IN_MILLIS).query()

        val eventsAndBlocks = ArrayList<ScheduleBlock>()

        for (event in events) {
            eventDao.fillForeignCollection(event, "speakerList")
        }
        
        eventsAndBlocks.addAll(events)
        eventsAndBlocks.addAll(blocks)

        Collections.sort(eventsAndBlocks, object : Comparator<ScheduleBlock>
        {
            override fun compare(lhs: ScheduleBlock, rhs: ScheduleBlock): Int
            {
                if (lhs.getStartLong() == rhs.getStartLong())
                    return 0

                return lhs.getStartLong()!!.compareTo(rhs.getStartLong()!!)
            }
        })

        return eventsAndBlocks
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