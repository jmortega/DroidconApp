package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.droidconandroid.data.Event
import co.touchlab.android.threading.tasks.TaskQueue
import java.util.concurrent.Callable
import java.util.UUID
import co.touchlab.android.superbus.appsupport.CommandBusHelper
import co.touchlab.droidconandroid.superbus.AddRsvpCommandKot
import de.greenrobot.event.EventBus
import co.touchlab.droidconandroid.superbus.FollowCommand

/**
 * Created by kgalligan on 7/27/14.
 */
class FollowToggleTask(val otherId: Long) : TaskQueue.Task
{
    class object
    {
        public fun createTask(c: Context, otherId: Long)
        {
            TaskQueue.execute(c, FollowToggleTask(otherId))
        }
    }

    override fun run(context: Context?)
    {
        CommandBusHelper.submitCommandSync(context, FollowCommand(otherId))
        EventBus.getDefault()!!.post(this);
    }

    override fun handleError(e: Exception?): Boolean
    {
        return false
    }
}