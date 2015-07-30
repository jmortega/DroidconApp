package co.touchlab.droidconandroid.tasks

import android.content.Context
import android.util.Log
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.android.superbus.appsupport.CommandBusHelper
import co.touchlab.android.superbus.CheckedCommand
import co.touchlab.android.superbus.errorcontrol.PermanentException
import co.touchlab.android.superbus.Command
import co.touchlab.droidconandroid.data.DatabaseHelper
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.network.UpdateUserProfile

/**
 * Created by kgalligan on 8/3/14.
 */
class UpdateUserProfileTask(c: Context, val name: String?,
                            val profile: String?,
                            val company: String?,
                            val twitter: String?,
                            val linkedIn: String?,
                            val website: String?,
                            val phoneticName: String?,
                            val nickname: String?,
                            val phone: String?,
                            val email: String?,
                            val gPlus: String?) : DatabaseTaskKot(c)
{
    override fun handleError(context: Context?, e: Throwable?): Boolean {
        return false
    }

    override fun run(context: Context?)
    {
        val appPrefs = AppPrefs.getInstance(context)
        if (appPrefs.isLoggedIn())
        {
            databaseHelper.inTransaction {
                ->
                val dao = databaseHelper.getUserAccountDao()
                val userAccount = dao.queryForId(appPrefs.getUserId())!!
                userAccount.name = name
                userAccount.profile = profile
                userAccount.company = company
                userAccount.twitter = twitter
                userAccount.linkedIn = linkedIn
                userAccount.website = website
                userAccount.phoneticName = phoneticName
                userAccount.nickname = nickname
                userAccount.phone = phone
                userAccount.email = email
                userAccount.gPlus = gPlus
                dao.createOrUpdate(userAccount)
                CommandBusHelper.submitCommandSync(context, UpdateUserProfileCommand())
            }
        }
    }

}

class UpdateUserProfileCommand() : CheckedCommand()
{
    override fun handlePermanentError(context: Context, exception: PermanentException): Boolean
    {
        Log.w("asdf", "Whoops", exception);
        return true;
    }
    override fun logSummary(): String?
    {
        return "";
    }
    override fun same(command: Command): Boolean
    {
        return command is UpdateUserProfileCommand
    }
    override fun callCommand(context: Context)
    {
        val appPrefs = AppPrefs.getInstance(context)
        if (appPrefs.isLoggedIn())
        {
            val userAccount = DatabaseHelper.getInstance(context).getUserAccountDao().queryForId(appPrefs.getUserId())
            val restAdapter = DataHelper.makeRequestAdapter(context)
            val updateUserProfile = restAdapter!!.create(javaClass<UpdateUserProfile>())

            if (userAccount != null)
            {
                updateUserProfile!!.update(
                        userAccount.name,
                        userAccount.profile,
                        userAccount.company,
                        userAccount.twitter,
                        userAccount.linkedIn,
                        userAccount.website,
                        userAccount.phoneticName,
                        userAccount.nickname,
                        userAccount.phone,
                        userAccount.email,
                        userAccount.gPlus
                )
            }
            else
            {
                throw PermanentException("User update failed")
            }
        }

    }

}