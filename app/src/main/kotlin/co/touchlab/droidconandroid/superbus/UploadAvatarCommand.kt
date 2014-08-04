package co.touchlab.droidconandroid.superbus

import co.touchlab.android.superbus.CheckedCommand
import co.touchlab.android.superbus.Command
import android.content.Context
import co.touchlab.android.superbus.http.BusHttpClient
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.R
import co.touchlab.android.superbus.errorcontrol.PermanentException
import org.apache.commons.io.IOUtils
import java.io.FileInputStream
import co.touchlab.android.threading.eventbus.EventBusExt
import com.google.gson.Gson
import co.touchlab.droidconandroid.network.dao.UserInfoResponse
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask

/**
 * Created by kgalligan on 7/20/14.
 */
open class UploadAvatarCommand(val imageURL : String? = null) : CheckedCommand()
{
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
        var body : ByteArray

        if(imageURL!!.startsWith("http"))
        {
            val client = BusHttpClient("")
            val response = client.get(imageURL, null)
            client.checkAndThrowError()
            body = response!!.getBody()!!
        }
        else
        {
            val inp = FileInputStream(imageURL)
            body = IOUtils.toByteArray(inp)!!
            inp.close()
        }

        val uuid = AppPrefs.getInstance(context).getUserUuid()
        val postClient = BusHttpClient(context.getString(R.string.base_url))
        postClient.addHeader("uuid", uuid);
        val uploadResponse = postClient.post("dataTest/uploadAvatar", "image/jpeg", body)
        postClient.checkAndThrowError()

        val userResponseString = uploadResponse?.getBodyAsString()
        if(userResponseString == null)
            throw RuntimeException("No user response")
        val gson = Gson()
        val userInfoResponse = gson.fromJson(userResponseString, javaClass<UserInfoResponse>())
        AbstractFindUserTask.saveUserResponse(context, null, userInfoResponse!!)

        EventBusExt.getDefault()!!.post(this)
    }

    override fun handlePermanentError(context: Context, exception: PermanentException): Boolean
    {
        return false
    }
}