package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.android.threading.tasks.TaskQueue
import co.touchlab.droidconandroid.superbus.FollowCommand
import co.touchlab.droidconandroid.superbus.UnfollowCommand
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.droidconandroid.tasks.persisted.PersistedTaskQueueFactory

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
                PersistedTaskQueueFactory.getInstance(context).execute(UnfollowCommand(otherId))
                userAccount.following = false
            }
            else
            {
                PersistedTaskQueueFactory.getInstance(context).execute(FollowCommand(otherId))
                userAccount.following = true
            }
            dao.createOrUpdate(userAccount)
            EventBusExt.getDefault()!!.post(this);
        }

    }
}