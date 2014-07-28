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
import com.google.gson.Gson
import co.touchlab.android.threading.tasks.TaskQueue
import de.greenrobot.event.EventBus
import android.text.TextUtils
import co.touchlab.droidconandroid.network.SingleUserInfoRequest
import co.touchlab.droidconandroid.data.DatabaseHelper
import co.touchlab.droidconandroid.data.UserAccount
import co.touchlab.droidconandroid.network.dao.userAccountToDb

/**
 * Created by kgalligan on 7/20/14.
 */
class FindUserTaskKot(val code: String) : AbstractFindUserTask()
{
    override fun doInBackground(context: Context?)
    {

        handleData(context!!, { (): UserAccount? ->
            val databaseHelper = DatabaseHelper.getInstance(context)
            UserAccount.findByCode(databaseHelper, code)
        }, {(): UserInfoResponse? ->
            val restAdapter = DataHelper.makeRequestAdapter(context)
            val findUserRequest = restAdapter!!.create(javaClass<FindUserRequest>())!!
            findUserRequest.getUserInfo(code)
        })
    }
}

data class FindUserResponse(val user: UserAccount)

class FindUserByIdTask(val id: Long) : AbstractFindUserTask()
{
    override fun doInBackground(context: Context?)
    {
        handleData(context!!, {(): UserAccount? ->
            DatabaseHelper.getInstance(context).getUserAccountDao().queryForId(id)
        }, {(): UserInfoResponse? ->
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

abstract class AbstractFindUserTask() : LiveNetworkBsyncTaskKot<UserInfoUpdate>()
{
    public var user: UserAccount? = null

    override fun onPostExecute(host: UserInfoUpdate?)
    {
        val userInfoUpdate = host as UserInfoUpdate
        userInfoUpdate.showResult(this)
    }

    override fun handleError(e: Exception?): Boolean
    {
        return false
    }

    fun handleData(context: Context, loadFromDb: () -> UserAccount?, loadRequest: () -> UserInfoResponse?)
    {
        user = loadFromDb()

        if (user != null)
        {
            EventBus.getDefault()!!.post(this)
        }

        try
        {
            val response = loadRequest()
            val newDbUser = UserAccount()
            userAccountToDb(response!!.user, newDbUser)

            if(user != null && user.equals(newDbUser))
            {
                cancelPost()
            }
            else
            {
                this.user = newDbUser
                val databaseHelper = DatabaseHelper.getInstance(context)
                databaseHelper.getUserAccountDao().createOrUpdate(user)
            }
        }
        catch(e: PermanentException)
        {
            errorStringCode = R.string.error_user_not_found
        }
    }

}