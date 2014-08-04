package co.touchlab.droidconandroid.superbus

import co.touchlab.android.superbus.CheckedCommand
import co.touchlab.android.superbus.Command
import android.content.Context
import co.touchlab.android.superbus.http.BusHttpClient
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.R
import co.touchlab.android.superbus.errorcontrol.PermanentException

/**
 * Created by kgalligan on 7/20/14.
 */
open class UploadAvatarCommand(var imageURL : String? = null) : CheckedCommand()
{
//    var imageURL: String? = null

    override fun logSummary(): String
    {
        return "imageURL: " + imageURL
    }

    override fun same(command: Command): Boolean
    {
        return false
    }

    override fun callCommand(context: Context)
    {
        val client = BusHttpClient("")
        val response = client.get(imageURL, null)
        val body = response!!.getBody()

        client.checkAndThrowError()

        val uuid = AppPrefs.getInstance(context).getUserUuid()
        val postClient = BusHttpClient(context.getString(R.string.base_url))
        postClient.addHeader("uuid", uuid);
        postClient.post("dataTest/uploadAvatar", "image/jpeg", body)
        postClient.checkAndThrowError()
    }

    override fun handlePermanentError(context: Context, exception: PermanentException): Boolean
    {
        return false
    }
}