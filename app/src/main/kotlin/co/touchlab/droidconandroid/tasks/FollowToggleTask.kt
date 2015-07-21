package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.android.threading.tasks.TaskQueue
import co.touchlab.android.superbus.appsupport.CommandBusHelper
import co.touchlab.droidconandroid.superbus.FollowCommand
import co.touchlab.droidconandroid.superbus.UnfollowCommand
import co.touchlab.android.threading.eventbus.EventBusExt

/**
 * Created by kgalligan on 7/27/14.
 */
class FollowToggleTask(c: Context, val otherId: Long) : DatabaseTaskKot(c)
{
    override fun handleError(context: Context?, e: Throwable?): Boolean {
        return false
    }

    companion object
    {
        public fun createTask(c: Context, otherId: Long)
        {
            TaskQueue.loadQueueDefault(c).execute(FollowToggleTask(c, otherId))
        }
    }

    override fun run(context: Context?)
    {
        val dao = databaseHelper.getUserAccountDao()
        val userAccount = dao.queryForId(otherId)
        if (userAccount != null)
        {
            if (userAccount.following)
            {
                CommandBusHelper.submitCommandSync(context, UnfollowCommand(otherId))
                userAccount.following = false
            }
            else
            {
                CommandBusHelper.submitCommandSync(context, FollowCommand(otherId))
                userAccount.following = true
            }
            dao.createOrUpdate(userAccount)
            EventBusExt.getDefault()!!.post(this);
        }

    }
}