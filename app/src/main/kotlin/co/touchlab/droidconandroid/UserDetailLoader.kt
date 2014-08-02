package co.touchlab.droidconandroid

import android.content.Context
import co.touchlab.android.threading.loaders.AbstractEventBusLoader
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask
import co.touchlab.droidconandroid.data.UserAccount
import co.touchlab.droidconandroid.data.DatabaseHelper
import co.touchlab.droidconandroid.tasks.FindUserByIdTask
import co.touchlab.android.threading.loaders.networked.AbstractDoubleTapEventBusLoader
import co.touchlab.android.threading.loaders.networked.DoubleTapResult
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.network.SingleUserInfoRequest
import retrofit.ErrorHandler
import retrofit.RetrofitError
import co.touchlab.droidconandroid.network.NetworkErrorHandler
import co.touchlab.droidconandroid.network.dao.userAccountToDb
import co.touchlab.droidconandroid.network.NetworkErrorHandler.NetworkException
import co.touchlab.droidconandroid.network.NetworkErrorHandler.NotFoundException
import co.touchlab.android.threading.loaders.networked.AbstractDoubleTapLoader

/**
 * Created by kgalligan on 7/29/14.
 */
class UserDetailLoader(c: Context, val userId: Long) : AbstractDoubleTapLoader<UserAccount, Int>(c)
{
    override fun registerContentChangedObserver()
    {
    }

    override fun unregisterContentChangedObserver()
    {
    }

    override fun findLocalContent(): UserAccount?
    {
        return getUserFromDb()
    }

    private fun getUserFromDb(): UserAccount?
    {
        return DatabaseHelper.getInstance(getContext()).getUserAccountDao().queryForId(userId)
    }

    override fun findRemoteContent(): Int?
    {
        val restAdapter = DataHelper.makeRequestAdapterBuilder(getContext(), NetworkErrorHandler()).build()
        val findUserRequest = restAdapter!!.create(javaClass<SingleUserInfoRequest>())!!
        try
        {
            val userInfoResponse = findUserRequest.getUserInfo(userId)!!
            val userAccount = userInfoResponse.user

            //TODO: Check if different. Also, put user update in central place.
            //Clear out superbus to prevent overwrite.
            val dbUser = getUserFromDb()

            val newDbUser = UserAccount()
            userAccountToDb(userAccount, newDbUser)
            val databaseHelper = DatabaseHelper.getInstance(getContext())
            databaseHelper.getUserAccountDao().createOrUpdate(newDbUser)

            return null;
        }
        catch(e: NetworkException)
        {
            return R.string.network_error
        }
        catch(e: NotFoundException)
        {
            return null
        }
    }

    override fun handleError(e: Exception?): Boolean
    {
        return false
    }
}