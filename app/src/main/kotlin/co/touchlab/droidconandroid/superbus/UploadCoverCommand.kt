package co.touchlab.droidconandroid.superbus

import android.content.Context
import android.util.Log
import co.touchlab.android.superbus.CheckedCommand
import co.touchlab.android.superbus.Command
import co.touchlab.android.superbus.errorcontrol.PermanentException
import co.touchlab.android.superbus.http.BusHttpClient
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.Task
import co.touchlab.droidconandroid.R
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.data.DatabaseHelper
import co.touchlab.droidconandroid.network.dao.UserInfoResponse
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask
import com.google.gson.Gson
import org.apache.commons.io.IOUtils
import java.io.FileInputStream

/**
 *
 * Created by izzyoji :) on 8/4/15.
 */
open class UploadCoverCommand(val coverURL: String? = null) : CheckedCommand()
{
    override fun logSummary(): String
    {
        return "coverURL: " + coverURL
    }

    override fun same(command: Command): Boolean
    {
        return false
    }

    override fun callCommand(context: Context)
    {
        var body : ByteArray

        val client = BusHttpClient("")
        val response = client.get(coverURL, null)
        client.checkAndThrowError()
        body = response!!.getBody()!!

        val uuid = AppPrefs.getInstance(context).getUserUuid()
        val postClient = BusHttpClient(context.getString(R.string.base_url))
        postClient.addHeader("uuid", uuid);
        val uploadResponse = postClient.post("dataTest/uploadCover", "image/jpeg", body)
        postClient.checkAndThrowError()

        val userResponseString = uploadResponse?.getBodyAsString() ?: throw RuntimeException("No user response")
        val gson = Gson()
        val userInfoResponse = gson.fromJson(userResponseString, javaClass<UserInfoResponse>())
        AbstractFindUserTask.saveUserResponse(context, null, userInfoResponse!!)

        EventBusExt.getDefault()!!.post(this)
    }

    override fun handlePermanentError(context: Context, exception: PermanentException): Boolean
    {
        Log.w("asdf", "Whoops", exception);
        return true;
    }
}
