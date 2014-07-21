package co.touchlab.droidconandroid.superbus

import co.touchlab.android.superbus.CheckedCommand
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.network.RefreshScheduleDataRequest
import android.content.Context
import co.touchlab.android.superbus.errorcontrol.PermanentException
import co.touchlab.android.superbus.errorcontrol.TransientException
import co.touchlab.android.superbus.Command
import co.touchlab.droidconandroid.data.DatabaseHelper
import java.text.SimpleDateFormat
import co.touchlab.droidconandroid.BuildConfig
import android.database.SQLException
import java.text.ParseException

/**
 * Created by kgalligan on 7/20/14.
 */
open class RefreshScheduleDataKot : CheckedCommand()
{
    override fun logSummary(): String
    {
        return this.javaClass.getSimpleName()
    }

    override fun same(command: Command): Boolean
    {
        return command is RefreshScheduleDataKot
    }

    override fun callCommand(context: Context)
    {
        val restAdapter = DataHelper.makeRequestAdapter(context)!!
        val request = restAdapter.create(javaClass<RefreshScheduleDataRequest>())!!
        val databaseHelper = DatabaseHelper.getInstance(context)
        val eventDao = databaseHelper.getEventDao()
        val venueDao = databaseHelper.getVenueDao()
        val dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mma")

        val convention = request.getScheduleData(BuildConfig.CONVENTION_ID)
        if(convention == null)
            throw PermanentException("No convention results")

        val venues = convention.venues

        try
        {
            for (venue in venues)
            {
                venueDao.createOrUpdate(venue)
                for (event in venue.events.iterator())
                {
                    val dbEvent = eventDao.queryForId(event.id)
                    event.venue = venue
                    event.startDateLong = dateFormat.parse(event.startDate)!!.getTime()
                    event.endDateLong = dateFormat.parse(event.endDate)!!.getTime()

                    if (dbEvent != null)
                        event.rsvpUuid = dbEvent.rsvpUuid

                    eventDao.createOrUpdate(event)
                }
            }
        }
        catch (e: SQLException)
        {
            throw PermanentException(e)
        }
        catch (e: ParseException)
        {
            throw PermanentException(e)
        }

    }

    override fun handlePermanentError(context: Context, exception: PermanentException): Boolean
    {
        return false
    }
}