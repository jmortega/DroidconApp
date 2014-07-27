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

/**
 * Created by kgalligan on 7/20/14.
 */
open class FindUserTaskKot(val code : String) : LiveNetworkBsyncTaskKot()
{
    public var userInfoResponse: UserInfoResponse? = null

    override fun doInBackground(context: Context?)
    {
        val restAdapter = DataHelper.makeRequestAdapter(context)
        val findUserRequest = restAdapter!!.create(javaClass<FindUserRequest>())!!

        try
        {
            this.userInfoResponse = findUserRequest.getUserInfo(code)
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