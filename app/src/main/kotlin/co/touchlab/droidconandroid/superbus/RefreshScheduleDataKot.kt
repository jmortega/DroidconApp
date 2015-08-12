package co.touchlab.droidconandroid.superbus

import android.content.Context
import android.database.SQLException
import android.util.Log
import co.touchlab.android.superbus.CheckedCommand
import co.touchlab.android.superbus.Command
import co.touchlab.android.superbus.errorcontrol.PermanentException
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.droidconandroid.BuildConfig
import co.touchlab.droidconandroid.data.*
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.network.RefreshScheduleDataRequest
import co.touchlab.droidconandroid.utils.TimeUtils
import java.text.ParseException
import java.util.concurrent.Callable

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

        val convention = request.getScheduleData(BuildConfig.CONVENTION_ID) ?: throw PermanentException("No convention results")

        val databaseHelper = DatabaseHelper.getInstance(context)
        databaseHelper.performTransactionOrThrowRuntime (object : Callable<Void>
        {
//            throws(javaClass<Exception>())
            override fun call(): Void?
            {
                val eventDao = databaseHelper.getEventDao()
                val venueDao = databaseHelper.getVenueDao()
                val blockDao = databaseHelper.getBlockDao()
                val userAccountDao = databaseHelper.getUserAccountDao()
                val eventSpeakerDao = databaseHelper.getEventSpeakerDao()

                AppPrefs.getInstance(context).setConventionStartDate(convention.startDate)
                AppPrefs.getInstance(context).setConventionEndDate(convention.endDate)

                val venues = convention.venues
                val blocks = convention.blocks
                try
                {
                    for (venue in venues)
                    {
                        venueDao.createOrUpdate(venue)
                        for (event in venue.events.iterator())
                        {
                            val dbEvent = eventDao.queryForId(event.id)
                            event.venue = venue
                            event.startDateLong = TimeUtils.DATE_FORMAT.parse(event.startDate)!!.getTime()
                            event.endDateLong = TimeUtils.DATE_FORMAT.parse(event.endDate)!!.getTime()

                            if (dbEvent != null)
                                event.rsvpUuid = dbEvent.rsvpUuid

                            eventDao.createOrUpdate(event)

                            val iterator = event.speakers?.iterator()
                            if (iterator != null)
                            {
                                var speakerCount = 0

                                for (ua in iterator)
                                {
                                    var userAccount = userAccountDao.queryForId(ua.id)

                                    if (userAccount == null)
                                    {
                                        userAccount = UserAccount()
                                    }

                                    UserAuthHelper.userAccountToDb(ua, userAccount)

                                    userAccountDao.createOrUpdate(userAccount)

                                    val resultList = eventSpeakerDao.queryBuilder()!!
                                            .where()!!
                                            .eq("event_id", event.id)!!
                                            .and()!!
                                            .eq("userAccount_id", userAccount.id)!!
                                            .query()!!

                                    var eventSpeaker = if (resultList.size() == 0) EventSpeaker() else resultList[0]

                                    eventSpeaker.event = event
                                    eventSpeaker.userAccount = userAccount
                                    eventSpeaker.displayOrder = speakerCount++

                                    eventSpeakerDao.createOrUpdate(eventSpeaker)
                                }
                            }
                        }
                    }

                    for (block in blocks)
                    {
                        block.startDateLong = TimeUtils.DATE_FORMAT.parse(block.startDate)!!.getTime()
                        block.endDateLong = TimeUtils.DATE_FORMAT.parse(block.endDate)!!.getTime()

                        blockDao.createOrUpdate(block)
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

                return null
            }
        })

        EventBusExt.getDefault()!!.post(this)
    }

    override fun handlePermanentError(context: Context, exception: PermanentException): Boolean
    {
        Log.w("asdf", "Whoops", exception);
        return true;
    }
}