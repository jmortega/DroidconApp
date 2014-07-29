package co.touchlab.droidconandroid.tasks

import co.touchlab.android.threading.tasks.TaskQueue
import android.content.Context
import co.touchlab.droidconandroid.utils.UserProfileInfo
import co.touchlab.android.threading.eventbus.EventBusExt

/**
 * Created by kgalligan on 7/27/14.
 */
class LocalUserDisplayNameTask(var displayName: String? = null) : TaskQueue.Task
{
    override fun run(context: Context?)
    {
        displayName = UserProfileInfo.findUserName(context)
        EventBusExt.getDefault()!!.post(this);
    }

    override fun handleError(e: Exception?): Boolean
    {
        throw UnsupportedOperationException()
    }
}