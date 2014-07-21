package co.touchlab.droidconandroid.tasks

import android.content.Context
import android.app.Activity
import co.touchlab.droidconandroid.R
import com.turbomanage.httpclient.BasicHttpClient
import org.json.JSONObject
import co.touchlab.droidconandroid.FindUser

/**
 * Created by kgalligan on 7/20/14.
 */
open class FindUserTaskKot(val code : String) : LiveNetworkBsyncTaskKot()
{
    public var userData: UserData? = null

    override fun doInBackground(context: Context?)
    {
        val client = BasicHttpClient(context?.getString(R.string.base_url))
        val httpResponse = client.get("dataTest/findUserByCode/" + code, null)
        if (httpResponse?.getStatus() == 404)
        {
            errorStringCode = R.string.error_user_not_found
        }
        else
        {
            val json = JSONObject(httpResponse?.getBodyAsString())
            val userData = UserData()
            userData.id = json.getLong("id")
            userData.name = json.getString("name")
            userData.avatarKey = json.getString("avatarKey")
            userData.userCode = json.getString("userCode")
            this.userData = userData
        }
    }

    override fun onPostExecute(host: Activity?)
    {
        val findUser = host as FindUser
        findUser.showResult(this)
    }

    override fun handleError(e: Exception?): Boolean
    {
        return false
    }

    class UserData
    {
        public var id: Long = 0
        public var name: String = ""
        public var avatarKey: String = ""
        public var userCode: String = ""
    }
}