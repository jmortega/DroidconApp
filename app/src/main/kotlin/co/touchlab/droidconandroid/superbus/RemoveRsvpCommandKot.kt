package co.touchlab.droidconandroid.superbus

import co.touchlab.android.superbus.CheckedCommand
import co.touchlab.android.superbus.errorcontrol.PermanentException
import co.touchlab.android.superbus.Command
import android.content.Context
import android.util.Log
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.network.RemoveRsvpRequest

/**
 * Created by kgalligan on 7/20/14.
 */
open class RemoveRsvpCommandKot(var eventId : Long? = null) : CheckedCommand()
{
    override fun logSummary(): String
    {
        return "RemoveRsvp - " + eventId
    }

    override fun same(command: Command): Boolean
    {
        return false
    }

    override fun callCommand(context: Context)
    {
        val restAdapter = DataHelper.makeRequestAdapter(context)
        val removeRsvpRequest = restAdapter!!.create(javaClass<RemoveRsvpRequest>())


        val userUuid = AppPrefs.getInstance(context).getUserUuid()
        if(eventId != null && userUuid != null)
        {
            removeRsvpRequest!!.removeRsvp(eventId!!)
        }
        else
        {
            throw PermanentException("Some value is null: "+ eventId +"/"+ userUuid)
        }
    }

    override fun handlePermanentError(context: Context, exception: PermanentException): Boolean
    {
        Log.w("asdf", "Whoops", exception);
        return true;
    }
}