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
import co.touchlab.droidconandroid.network.SingleUserInfoRequest

/**
 * Created by kgalligan on 7/20/14.
 */
class FindUserTaskKot(val code: String) : AbstractFindUserTask()
{
    override fun doInBackground(context: Context?)
    {
        handleData(context!!, code, {(): UserInfoResponse? ->
            val restAdapter = DataHelper.makeRequestAdapter(context)
            val findUserRequest = restAdapter!!.create(javaClass<FindUserRequest>())!!
            findUserRequest.getUserInfo(code)
        })
    }
}

class FindUserByIdTask(val id: Long) : AbstractFindUserTask()
{
    override fun doInBackground(context: Context?)
    {
        handleData(context!!, makeIdFileName(id)!!, {(): UserInfoResponse? ->
            val restAdapter = DataHelper.makeRequestAdapter(context)
            val findUserRequest = restAdapter!!.create(javaClass<SingleUserInfoRequest>())!!
            findUserRequest.getUserInfo(id)
        })
    }
}

trait UserInfoUpdate
{
    fun showResult(findUserTask: AbstractFindUserTask)
}

abstract class AbstractFindUserTask() : LiveNetworkBsyncTaskKot()
{
    public var userInfoResponse: UserInfoResponse? = null

    override fun onPostExecute(host: Activity?)
    {
        val userInfoUpdate = host as UserInfoUpdate
        userInfoUpdate.showResult(this)
    }

    override fun handleError(e: Exception?): Boolean
    {
        return false
    }

    fun handleData(context: Context, fileName: String, loadRequest: () -> UserInfoResponse?)
    {
        val cacheUserData = CacheHelper.findFile(context, fileName)

        if (!TextUtils.isEmpty(cacheUserData))
        {
            val gson = Gson()
            userInfoResponse = gson.fromJson(cacheUserData, javaClass<UserInfoResponse>())

            EventBus.getDefault()!!.post(this)
        }

        try
        {
            this.userInfoResponse = loadRequest()
            saveInCache(context)
        }
        catch(e: PermanentException)
        {
            errorStringCode = R.string.error_user_not_found
        }
    }

    fun saveInCache(context: Context)
    {
        val userJson = Gson().toJson(userInfoResponse)

        val userCode = userInfoResponse?.user?.userCode
        if (userCode != null)
            CacheHelper.saveFile(context, userCode, userJson!!)

        val userFileName = makeIdFileName(userInfoResponse?.user?.id)
        if (userFileName != null)
            CacheHelper.saveFile(context, userFileName, userJson!!)
    }

    fun makeIdFileName(id: Long?): String?
    {
        return if (id != null) "user_" + id else null
    }
}