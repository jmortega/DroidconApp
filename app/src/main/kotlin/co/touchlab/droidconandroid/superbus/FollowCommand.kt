package co.touchlab.droidconandroid.superbus

import co.touchlab.android.superbus.CheckedCommand
import co.touchlab.android.superbus.errorcontrol.PermanentException
import co.touchlab.android.superbus.Command
import android.content.Context
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.network.FollowRequest

/**
 * Created by kgalligan on 7/20/14.
 */
open class FollowCommand(var otherId : Long? = null) : CheckedCommand()
{
    override fun logSummary(): String
    {
        return "FollowCommand - " + otherId
    }

    override fun same(command: Command): Boolean
    {
        return false
    }

    override fun callCommand(context: Context)
    {
        val restAdapter = DataHelper.makeRequestAdapter(context)
        val followRequest = restAdapter!!.create(javaClass<FollowRequest>())!!

        val userUuid = AppPrefs.getInstance(context).getUserUuid()
        if(userUuid != null)
        {
            followRequest.follow(otherId!!)
        }
    }

    override fun handlePermanentError(context: Context, exception: PermanentException): Boolean
    {
        return false
    }
}