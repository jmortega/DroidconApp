package co.touchlab.droidconandroid.tasks

import android.content.Context
import android.app.Activity
import co.touchlab.droidconandroid.R
import com.turbomanage.httpclient.BasicHttpClient
import org.json.JSONObject
import co.touchlab.droidconandroid.FindUserKot
import co.touchlab.droidconandroid.network.dao.UserInfoResponse
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.network.AddRsvpRequest
import co.touchlab.droidconandroid.network.FindUserRequest
import co.touchlab.android.superbus.errorcontrol.PermanentException
import co.touchlab.droidconandroid.utils.CacheHelper
import com.google.gson.Gson
import co.touchlab.android.threading.tasks.TaskQueue
import de.greenrobot.event.EventBus
import android.text.TextUtils

/**
 * Created by kgalligan on 7/20/14.
 */
open class FindUserTaskKot(val code : String) : LiveNetworkBsyncTaskKot()
{
    public var userInfoResponse: UserInfoResponse? = null

    override fun doInBackground(context: Context?)
    {
        val cacheUserData = CacheHelper.findFile(context!!, code)

        if(!TextUtils.isEmpty(cacheUserData))
        {
            val gson = Gson()
            userInfoResponse = gson.fromJson(cacheUserData, javaClass<UserInfoResponse>())

            EventBus.getDefault()!!.post(this)
        }

        val restAdapter = DataHelper.makeRequestAdapter(context)
        val findUserRequest = restAdapter!!.create(javaClass<FindUserRequest>())!!

        try
        {
            this.userInfoResponse = findUserRequest.getUserInfo(code)
            Thread.sleep(5000)
            CacheHelper.saveFile(context, code, Gson().toJson(userInfoResponse)!!)
        }
        catch(e: PermanentException)
        {
            errorStringCode = R.string.error_user_not_found
        }
    }

    override fun onPostExecute(host: Activity?)
    {
        val findUser = host as FindUserKot
        findUser.showResult(this)
    }

    override fun handleError(e: Exception?): Boolean
    {
        return false
    }
}