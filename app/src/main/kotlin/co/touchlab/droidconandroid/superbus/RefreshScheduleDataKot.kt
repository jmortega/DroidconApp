package co.touchlab.droidconandroid.superbus

import android.content.Context
import android.database.SQLException
import android.util.Log
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.helper.RetrofitPersistedTask
import co.touchlab.android.threading.tasks.persisted.PersistedTask
import co.touchlab.droidconandroid.BuildConfig
import co.touchlab.droidconandroid.data.*
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.network.RefreshScheduleDataRequest
import co.touchlab.droidconandroid.utils.TimeUtils
import com.crashlytics.android.Crashlytics
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.concurrent.Callable

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
        if (convention == null)
            throw IllegalStateException("No convention results")

        val databaseHelper = DatabaseHelper.getInstance(context)
        databaseHelper.performTransactionOrThrowRuntime (object : Callable<Void> {
            //            throws(javaClass<Exception>())
            override fun call(): Void? {
                val eventDao = databaseHelper.getEventDao()
                val venueDao = databaseHelper.getVenueDao()
                val userAccountDao = databaseHelper.getUserAccountDao()
                val eventSpeakerDao = databaseHelper.getEventSpeakerDao()

                val venues = convention.venues

                AppPrefs.getInstance(context).setConventionStartDate(convention.startDate)
                AppPrefs.getInstance(context).setConventionEndDate(convention.endDate)

                try
                {
                    for (venue in venues)
                    {
                        venueDao.createOrUpdate(venue)
                        for (event in venue.events.iterator()) {
                            val dbEvent = eventDao.queryForId(event.id)
                            event.venue = venue
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

                                    UserAuthHelper.userAccountToDb(ua, userAccount!!)

                                    userAccountDao.createOrUpdate(userAccount)

                                    val resultList = eventSpeakerDao.queryBuilder()!!
                                            .where()!!
                                            .eq("event_id", event.id)!!
                                            .and()!!
                                            .eq("userAccount_id", userAccount!!.id)!!
                                            .query()!!

                                    var eventSpeaker = if (resultList.size == 0) EventSpeaker() else resultList[0]

                                    eventSpeaker.event = event
                                    eventSpeaker.userAccount = userAccount
                                    eventSpeaker.displayOrder = speakerCount++

                                    eventSpeakerDao.createOrUpdate(eventSpeaker)
                                }
                            }
                        }
                    }
                } catch (e: SQLException) {
                    throw RuntimeException(e)
                } catch (e: ParseException) {
                    throw RuntimeException(e)
                }

                return null
            }
        })

        EventBusExt.getDefault()!!.post(this)
    }

    override fun handleError(context: Context?, e: Throwable?): Boolean {
        Log.w("asdf", "Whoops", e);
        Crashlytics.logException(e);
        return true;
    }
}