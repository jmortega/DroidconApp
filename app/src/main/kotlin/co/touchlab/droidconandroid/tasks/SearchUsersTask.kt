package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.Task
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.network.FindUserRequest
import co.touchlab.droidconandroid.network.dao.UserSearchResponse
import com.crashlytics.android.Crashlytics

/**
 * Created by kgalligan on 8/23/15.
 */
class SearchUsersTask(val search: String, var userSearchResponse: UserSearchResponse? = null): Task()
{
    override fun run(context: Context?) {
        val restAdapter = DataHelper.makeRequestAdapter(context)
        val findUserRequest = restAdapter!!.create(javaClass<FindUserRequest>())!!
        userSearchResponse = findUserRequest.searchUsers(search)
    }

    override fun handleError(context: Context?, e: Throwable?): Boolean {
        Crashlytics.logException(e)
        return true
    }

    override fun onComplete(context: Context?) {
        EventBusExt.getDefault().post(this)
    }
}