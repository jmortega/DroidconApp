package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.Task
import co.touchlab.droidconandroid.utils.UserProfileInfo

/**
 * Created by kgalligan on 7/27/14.
 */
class LocalUserDisplayNameTask(var displayName: String? = null) : Task()
{
    override fun handleError(context: Context?, e: Throwable?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun run(context: Context?)
    {
        displayName = UserProfileInfo.findUserName(context)
        EventBusExt.getDefault()!!.post(this);
    }
}