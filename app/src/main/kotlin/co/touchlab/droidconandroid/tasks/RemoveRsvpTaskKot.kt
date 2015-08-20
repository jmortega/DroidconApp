package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.droidconandroid.data.Event
import java.util.concurrent.Callable
import co.touchlab.android.threading.tasks.TaskQueue
import co.touchlab.droidconandroid.superbus.RemoveRsvpCommandKot
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.droidconandroid.tasks.persisted.PersistedTaskQueueFactory

/**
 * Created by kgalligan on 7/20/14.
 */
class RemoveRsvpTaskKot(c : Context, val eventId : Long) : DatabaseTaskKot(c)
{
    override fun handleError(context: Context?, e: Throwable?): Boolean {
        throw UnsupportedOperationException()
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
                if (event != null)
                {
                    event.rsvpUuid = null
                    dao.update(event)
                    PersistedTaskQueueFactory.getInstance(context).execute(RemoveRsvpCommandKot(eventId))
                }

                return null
            }
        })

        EventBusExt.getDefault()!!.post(this);
    }
}