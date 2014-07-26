package co.touchlab.droidconandroid.superbus

import co.touchlab.android.superbus.CheckedCommand
import retrofit.http.FormUrlEncoded
import retrofit.http.POST
import co.touchlab.android.superbus.errorcontrol.TransientException
import co.touchlab.android.superbus.errorcontrol.PermanentException
import retrofit.http.Path
import retrofit.http.Field
import co.touchlab.droidconandroid.network.BasicIdResult
import co.touchlab.android.superbus.Command
import android.content.Context
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.data.AppPrefs
import android.util.Log
import co.touchlab.droidconandroid.network.AddRsvpRequest
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
            val basicIdResult = removeRsvpRequest!!.removeRsvp(eventId!!, userUuid)
            Log.w("asdf", "Result id: " + basicIdResult!!.id)
        }
        else
        {
            throw PermanentException("Some value is null: "+ eventId +"/"+ userUuid)
        }
    }

    override fun handlePermanentError(context: Context, exception: PermanentException): Boolean
    {
        return false
    }
}