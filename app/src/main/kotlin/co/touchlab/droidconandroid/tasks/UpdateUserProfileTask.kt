package co.touchlab.droidconandroid.tasks

import android.content.Context
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
                            val website: String?) : DatabaseTaskKot(c)
{
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
                dao.createOrUpdate(userAccount)
                CommandBusHelper.submitCommandSync(context, UpdateUserProfileCommand())
            }
        }
    }
    override fun handleError(e: Exception?): Boolean
    {
        return false
    }
}

class UpdateUserProfileCommand() : CheckedCommand()
{
    override fun handlePermanentError(context: Context, exception: PermanentException): Boolean
    {
        throw UnsupportedOperationException()
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
                        userAccount.website
                )
            }
            else
            {
                throw PermanentException("User update failed")
            }
        }

    }

}