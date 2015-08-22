package co.touchlab.droidconandroid.superbus

import android.content.Context
import android.database.SQLException
import android.util.Log
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.Task
import co.touchlab.android.threading.tasks.helper.RetrofitPersistedTask
import co.touchlab.android.threading.tasks.persisted.PersistedTask
import co.touchlab.droidconandroid.BuildConfig
import co.touchlab.droidconandroid.data.*
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.network.RefreshScheduleDataRequest
import co.touchlab.droidconandroid.network.RsvpRequest
import co.touchlab.droidconandroid.network.dao.Convention
import co.touchlab.droidconandroid.utils.TimeUtils
import com.crashlytics.android.Crashlytics
import com.google.gson.Gson
import com.j256.ormlite.dao.Dao
import org.apache.commons.lang3.StringUtils
import java.io.InputStreamReader
import java.text.ParseException
import java.util.*
import java.util.concurrent.Callable

fun saveConventionData(context: Context?, convention: Convention) {
    if (convention == null)
        throw IllegalStateException("No convention results")

    val databaseHelper = DatabaseHelper.getInstance(context)
    databaseHelper.performTransactionOrThrowRuntime (object : Callable<Void> {
        //            throws(javaClass<Exception>())
        override fun call(): Void? {
            val eventDao = databaseHelper.getEventDao()
            val venueDao = databaseHelper.getVenueDao()
            val blockDao = databaseHelper.getBlockDao()
            val userAccountDao = databaseHelper.getUserAccountDao()
            val eventSpeakerDao = databaseHelper.getEventSpeakerDao()

            AppPrefs.getInstance(context).setConventionStartDate(convention.startDate)
            AppPrefs.getInstance(context).setConventionEndDate(convention.endDate)

            val venues = convention.venues
            val blocks = convention.blocks
            try {
                for (venue in venues) {
                    venueDao.createOrUpdate(venue)
                    for (event in venue.events.iterator()) {
                        val dbEvent = eventDao.queryForId(event.id)
                        event.venue = venue
                        if (StringUtils.isEmpty(event.startDate) || StringUtils.isEmpty(event.endDate))
                            continue
                        event.startDateLong = TimeUtils.DATE_FORMAT.parse(event.startDate)!!.getTime()
                        event.endDateLong = TimeUtils.DATE_FORMAT.parse(event.endDate)!!.getTime()

                        if (dbEvent != null)
                            event.rsvpUuid = dbEvent.rsvpUuid

                        eventDao.createOrUpdate(event)

                        val iterator = event.speakers?.iterator()
                        if (iterator != null) {
                            var speakerCount = 0

                            for (ua in iterator) {
                                var userAccount = userAccountDao.queryForId(ua.id)

                                if (userAccount == null) {
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

                for (block in blocks) {
                    block.startDateLong = TimeUtils.DATE_FORMAT.parse(block.startDate)!!.getTime()
                    block.endDateLong = TimeUtils.DATE_FORMAT.parse(block.endDate)!!.getTime()

                    blockDao.createOrUpdate(block)
                }

            } catch (e: SQLException) {
                throw RuntimeException(e)
            } catch (e: ParseException) {
                throw RuntimeException(e)
            }

            return null
        }
    })
}

open class SeedScheduleDataTask : Task() {
    override fun handleError(context: Context?, e: Throwable?): Boolean {
        Crashlytics.logException(e)
        return true
    }

    override fun run(context: Context?) {
        saveConventionData(context, Gson().fromJson(InputStreamReader(context!!.getAssets().open("dataseed.json")), javaClass<Convention>()))
    }

}

/**
 * Created by kgalligan on 7/20/14.
 */
open class RefreshScheduleDataKot : RetrofitPersistedTask() {
    override fun logSummary(): String {
        return this.javaClass.getSimpleName()
    }

    override fun same(command: PersistedTask): Boolean {
        return command is RefreshScheduleDataKot
    }

    override fun runNetwork(context: Context?) {
        val restAdapter = DataHelper.makeRequestAdapter(context)!!
        val request = restAdapter.create(javaClass<RefreshScheduleDataRequest>())!!


        val convention = request.getScheduleData(BuildConfig.CONVENTION_ID)
        saveConventionData(context, convention)

        if(!AppPrefs.getInstance(context).isMyRsvpsLoaded())
        {
            try {
                val rsvpRequest = restAdapter.create(javaClass<RsvpRequest>())!!
                val myRsvpResponse = rsvpRequest.getMyRsvps(BuildConfig.CONVENTION_ID.toLong())
                val databaseHelper = DatabaseHelper.getInstance(context)
                databaseHelper.performTransactionOrThrowRuntime (object : Callable<Void> {
                    //            throws(javaClass<Exception>())
                    override fun call(): Void? {
                        val eventDao = databaseHelper.getEventDao()
                        for (eventId in myRsvpResponse.starred_sessions) {
                            val event = eventDao.queryForId(eventId.toLong())
                            if(event != null) {
                                event.rsvpUuid = UUID.randomUUID().toString()
                                eventDao.update(event)
                            }
                        }
                        return null;
                    }
                })
            } catch(e: Exception) {
                //Yeah, well, its not your banking app
                Crashlytics.logException(e)
            } finally {
                AppPrefs.getInstance(context).setMyRsvpsLoaded(true)
            }

        }

        EventBusExt.getDefault()!!.post(this)
    }

    override fun handleError(context: Context?, e: Throwable?): Boolean {
        Log.e("Schedule", "Error fetching schedule data", e);
        Crashlytics.logException(e);
        return true;
    }
}