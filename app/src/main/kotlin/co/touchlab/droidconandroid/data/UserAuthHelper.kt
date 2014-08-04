package co.touchlab.droidconandroid.data

import android.content.Context
import co.touchlab.droidconandroid.network.LoginResult
import co.touchlab.android.superbus.appsupport.CommandBusHelper
import co.touchlab.droidconandroid.superbus.RefreshScheduleDataKot

/**
 * Created by kgalligan on 8/4/14.
 */
class UserAuthHelper
{
    class object
    {
        fun processLoginResonse(c: Context, result: LoginResult)
        {
            if(result.uuid != null)
            {
                val appPrefs = AppPrefs.getInstance(c)
                appPrefs.setUserUuid(result.uuid)
                appPrefs.setUserId(result.userId)
                val newDbUser = UserAccount()
                userAccountToDb(result.user!!.user, newDbUser)
                DatabaseHelper.getInstance(c).getUserAccountDao().createOrUpdate(newDbUser)
                CommandBusHelper.submitCommandSync(c, RefreshScheduleDataKot())
            }
        }

        fun userAccountToDb(ua: co.touchlab.droidconandroid.network.dao.UserAccount, dbUa: co.touchlab.droidconandroid.data.UserAccount)
        {
            dbUa.id = ua.id
            dbUa.uuid = ua.uuid
            dbUa.name = ua.name
            dbUa.profile = ua.profile
            dbUa.avatarKey = ua.avatarKey
            dbUa.userCode = ua.userCode
            dbUa.company = ua.company
            dbUa.twitter = ua.twitter
            dbUa.linkedIn = ua.linkedIn
            dbUa.website = ua.website
            dbUa.following = ua.following
        }
    }
}