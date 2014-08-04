package co.touchlab.droidconandroid.superbus

import co.touchlab.android.superbus.CheckedCommand
import co.touchlab.android.superbus.errorcontrol.PermanentException
import co.touchlab.android.superbus.Command
import android.content.Context
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.data.AppPrefs
import android.util.Log
import co.touchlab.droidconandroid.network.AddRsvpRequest

/**
 * Created by kgalligan on 7/20/14.
 */
class AddRsvpCommandKot(var eventId : Long? = null, var rsvpUuid : String? = null) : CheckedCommand()
{
    override fun logSummary(): String
    {
        return "AddRsvp - " + eventId
    }

    override fun same(command: Command): Boolean
    {
        return false
    }

    override fun callCommand(context: Context)
    {
        val restAdapter = DataHelper.makeRequestAdapter(context)
        val addRsvpRequest = restAdapter!!.create(javaClass<AddRsvpRequest>())


        val userUuid = AppPrefs.getInstance(context).getUserUuid()
        if(eventId != null && userUuid != null && rsvpUuid != null)
        {
            val basicIdResult = addRsvpRequest!!.addRsvp(eventId!!, userUuid, rsvpUuid!!)
            Log.w("asdf", "Result id: " + basicIdResult!!.id)
        }
        else
        {
            throw PermanentException("Some value is null: "+ eventId +"/"+ userUuid +"/"+ rsvpUuid)
        }
    }

    override fun handlePermanentError(context: Context, exception: PermanentException): Boolean
    {
        return false
    }
}