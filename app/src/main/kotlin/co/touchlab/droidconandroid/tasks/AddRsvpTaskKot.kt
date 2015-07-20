package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.droidconandroid.data.Event
import java.util.UUID
import co.touchlab.android.superbus.appsupport.CommandBusHelper
import java.util.concurrent.Callable
import co.touchlab.droidconandroid.superbus.AddRsvpCommandKot
import co.touchlab.android.threading.tasks.TaskQueue
import co.touchlab.android.threading.eventbus.EventBusExt

/**
 * Created by kgalligan on 7/20/14.
 */
open class AddRsvpTaskKot(c : Context, val eventId : Long) : DatabaseTaskKot(c)
{
    companion object
    {
        public fun createTask(c : Context, event: Event)
        {
            TaskQueue.execute(c, AddRsvpTaskKot(c, event.id))
        }
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
                    CommandBusHelper.submitCommandSync(context, AddRsvpCommandKot(eventId, uuid))
                }

                return null
            }
        })

        EventBusExt.getDefault()!!.post(this);
    }
    override fun handleError(e: Exception?): Boolean
    {
        throw UnsupportedOperationException()
    }
}