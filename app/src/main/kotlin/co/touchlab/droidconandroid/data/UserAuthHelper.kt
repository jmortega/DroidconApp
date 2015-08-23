package co.touchlab.droidconandroid.data

import android.content.Context
import co.touchlab.droidconandroid.network.dao.LoginResult

/**
 * Created by kgalligan on 8/4/14.
 */
class UserAuthHelper
{
    companion object
    {
        fun processLoginResonse(c: Context, result: LoginResult): UserAccount
        {
            val newDbUser = UserAccount()
            userAccountToDb(result.user, newDbUser)
            DatabaseHelper.getInstance(c).getUserAccountDao().createOrUpdate(newDbUser)

            //Save db first, then these.
            val appPrefs = AppPrefs.getInstance(c)
            appPrefs.setUserUuid(result.uuid)
            appPrefs.setUserId(result.userId)

            saveDrawerAppPrefs(c, newDbUser)

            return newDbUser
        }

        fun userAccountToDb(ua: co.touchlab.droidconandroid.network.dao.UserAccount, dbUa: co.touchlab.droidconandroid.data.UserAccount)
        {
            dbUa.id = ua.id
            dbUa.name = ua.name
            dbUa.profile = ua.profile
            dbUa.avatarKey = ua.avatarKey
            dbUa.userCode = ua.userCode
            dbUa.company = ua.company
            dbUa.twitter = ua.twitter
            dbUa.linkedIn = ua.linkedIn
            dbUa.website = ua.website
            dbUa.following = ua.following
            dbUa.gPlus = ua.gPlus
            dbUa.phone = ua.phone
            dbUa.email = ua.email
            dbUa.coverKey = ua.coverKey
            dbUa.facebook = ua.facebook
            dbUa.emailPublic = ua.emailPublic
        }

         fun saveDrawerAppPrefs(context: Context, user: UserAccount) {
             val appPrefs = AppPrefs.getInstance(context)
             appPrefs.setAvatarKey(user.avatarKey)
             appPrefs.setCoverKey(user.coverKey)
             appPrefs.setName(user.name)
             appPrefs.setEmail(user.email)
        }
    }
}