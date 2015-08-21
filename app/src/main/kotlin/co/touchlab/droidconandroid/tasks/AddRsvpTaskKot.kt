package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.droidconandroid.data.Event
import java.util.UUID
import java.util.concurrent.Callable
import co.touchlab.android.threading.tasks.TaskQueue
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.droidconandroid.tasks.persisted.AddRsvp
import co.touchlab.droidconandroid.tasks.persisted.PersistedTaskQueueFactory
import com.crashlytics.android.Crashlytics

/**
 * Created by kgalligan on 7/20/14.
 */
open class AddRsvpTaskKot(c : Context, val eventId : Long) : DatabaseTaskKot(c)
{
    override fun handleError(context: Context?, e: Throwable?): Boolean {
        //This is all local.  Should work.
        return false
    }

    override fun run(context: Context?)
    {
        databaseHelper.performTransactionOrThrowRuntime(object : Callable<Void>
        {
//            throws(javaClass<Exception>())
            override fun call(): Void?
            {
                val dao = databaseHelper.getEventDao()
                val event = dao.queryForId(eventId)
                if (event != null && event.rsvpUuid == null)
                {
                    val uuid = UUID.randomUUID().toString()
                    event.rsvpUuid = uuid
                    dao.update(event)
                    val addRsvpCommandKot = AddRsvp(eventId, uuid)
                    PersistedTaskQueueFactory.getInstance(context).execute(addRsvpCommandKot)
                }

                return null
            }
        })

        EventBusExt.getDefault()!!.post(this);
    }

}