package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.Task
import co.touchlab.droidconandroid.data.DatabaseHelper
import co.touchlab.droidconandroid.tasks.persisted.AddRsvp
import co.touchlab.droidconandroid.tasks.persisted.PersistedTaskQueueFactory
import java.util.UUID
import java.util.concurrent.Callable

/**
 * Created by kgalligan on 7/20/14.
 */
open class AddRsvpTaskKot(val eventId : Long) : Task()
{
    override fun handleError(context: Context?, e: Throwable?): Boolean {
        //This is all local.  Should work.
        return false
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
    }

    override fun onComplete(context: Context?) {
        EventBusExt.getDefault()!!.post(this);
    }
}