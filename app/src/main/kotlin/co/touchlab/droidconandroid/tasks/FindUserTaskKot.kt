package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.droidconandroid.R
import co.touchlab.droidconandroid.network.dao.UserInfoResponse
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.network.FindUserRequest
import co.touchlab.android.superbus.errorcontrol.PermanentException
import co.touchlab.droidconandroid.network.SingleUserInfoRequest
import co.touchlab.droidconandroid.data.DatabaseHelper
import co.touchlab.droidconandroid.data.UserAccount
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.droidconandroid.data.UserAuthHelper

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

    class object
    {
        fun saveUserResponse(context: Context, user: UserAccount?, response: UserInfoResponse): UserAccount?
        {
            val newDbUser = UserAccount()
            UserAuthHelper.userAccountToDb(response.user, newDbUser)

            if(user == null || !user.equals(newDbUser))
            {
                val databaseHelper = DatabaseHelper.getInstance(context)
                databaseHelper.getUserAccountDao().createOrUpdate(newDbUser)
                return newDbUser
            }

            return null
        }

    }

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
            EventBusExt.getDefault()!!.post(this)
        }

        try
        {
            val response = loadRequest()
            if(response != null)
            {
                val updatedUser = saveUserResponse(context, user, response)

                if (updatedUser == null)
                {
                    cancelPost()
                }
                else
                {
                    this.user = updatedUser
                }
            }
        }
        catch(e: PermanentException)
        {
            errorStringCode = R.string.error_user_not_found
        }
    }

}