package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.Task
import co.touchlab.droidconandroid.data.DatabaseHelper
import co.touchlab.droidconandroid.tasks.persisted.PersistedTaskQueueFactory
import co.touchlab.droidconandroid.tasks.persisted.RemoveRsvp
import java.util.concurrent.Callable

/**
 * Created by kgalligan on 7/20/14.
 */
class RemoveRsvpTaskKot(val eventId : Long) : Task()
{
    override fun handleError(context: Context?, e: Throwable?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun run(context: Context?)
    {
        DatabaseHelper.getInstance(context).performTransactionOrThrowRuntime(object : Callable<Void>
        {
//            throws(javaClass<Exception>())
            override fun call(): Void?
            {
                val dao = DatabaseHelper.getInstance(context).getEventDao()
                val event = dao.queryForId(eventId)
                if (event != null)
                {
                    event.rsvpUuid = null
                    dao.update(event)
                    PersistedTaskQueueFactory.getInstance(context).execute(RemoveRsvp(eventId))
                }

                return null
            }
        })
    }

    override fun onComplete(context: Context?) {
        EventBusExt.getDefault()!!.post(this);
    }
}