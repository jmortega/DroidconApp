package co.touchlab.droidconandroid

import android.content.Context
import co.touchlab.android.threading.loaders.AbstractEventBusLoader
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask
import co.touchlab.droidconandroid.data.UserAccount
import co.touchlab.droidconandroid.data.DatabaseHelper
import co.touchlab.droidconandroid.tasks.FindUserByIdTask

/**
 * Created by kgalligan on 7/29/14.
 */
class UserDetailLoader(c: Context, val userId: Long) : AbstractEventBusLoader<UserAccount>(c)
{
    override fun findContent(): UserAccount?
    {
        return DatabaseHelper.getInstance(getContext()).getUserAccountDao().queryForId(userId)
    }

    override fun handleError(e: Exception?): Boolean
    {
        return false
    }

    public fun onEvent(f: FindUserByIdTask)
    {

    }
}