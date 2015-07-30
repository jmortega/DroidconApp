package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.android.superbus.errorcontrol.PermanentException
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.Task
import co.touchlab.droidconandroid.R
import co.touchlab.droidconandroid.data.DatabaseHelper
import co.touchlab.droidconandroid.data.UserAccount
import co.touchlab.droidconandroid.data.UserAuthHelper
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.network.FindUserRequest
import co.touchlab.droidconandroid.network.SingleUserInfoRequest
import co.touchlab.droidconandroid.network.dao.UserInfoResponse
import retrofit.RetrofitError
import java.net.HttpURLConnection

/**
 * Created by kgalligan on 7/20/14.
 */
class FindUserTaskKot(val code: String) : AbstractFindUserTask()
{
    override fun run(context: Context?) {
        handleData(context!!, fun(): UserAccount? {
            val databaseHelper = DatabaseHelper.getInstance(context)
            return UserAccount.findByCode(databaseHelper, code)
        }, fun(): UserInfoResponse? {
            val restAdapter = DataHelper.makeRequestAdapter(context)
            val findUserRequest = restAdapter!!.create(javaClass<FindUserRequest>())!!
            return findUserRequest.getUserInfo(code)
        })
    }
}

data class FindUserResponse(val user: UserAccount)

class FindUserByIdTask(val id: Long) : AbstractFindUserTask()
{
    override fun run(context: Context?) {
        handleData(context!!, fun(): UserAccount? = DatabaseHelper.getInstance(context).getUserAccountDao().queryForId(id), fun(): UserInfoResponse? {
            val restAdapter = DataHelper.makeRequestAdapter(context)
            val findUserRequest = restAdapter!!.create(javaClass<SingleUserInfoRequest>())!!
            try {
                return findUserRequest.getUserInfo(id)
            } catch(e: RetrofitError) {
                if(e.getResponse().getStatus() == HttpURLConnection.HTTP_NOT_FOUND) {
                    errorStringCode = R.string.error_user_not_found
                    return null
                }
                throw RuntimeException(e)
            }
        })
    }
}

abstract class AbstractFindUserTask() : Task()
{
    public var user: UserAccount? = null
    public var errorStringCode: Int? = null

    companion object
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

    public fun isError():Boolean
    {
        return errorStringCode != null
    }

    override fun onComplete(context: Context?) {
        EventBusExt.getDefault()!!.post(this)
    }

    override fun handleError(context: Context?, e: Throwable?): Boolean {
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
//                    cancelPost()
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