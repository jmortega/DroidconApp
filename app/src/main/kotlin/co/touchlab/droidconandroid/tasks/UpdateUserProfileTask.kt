package co.touchlab.droidconandroid.tasks

import android.content.Context
import android.util.Log
import co.touchlab.android.threading.tasks.helper.RetrofitPersistedTask
import co.touchlab.android.threading.tasks.persisted.PersistedTask
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.data.DatabaseHelper
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.network.UpdateUserProfile
import co.touchlab.droidconandroid.tasks.persisted.PersistedTaskQueueFactory
import com.crashlytics.android.Crashlytics

/**
 * Created by kgalligan on 8/3/14.
 */
class UpdateUserProfileTask(c: Context, val name: String?,
                            val profile: String?,
                            val company: String?,
                            val twitter: String?,
                            val linkedIn: String?,
                            val website: String?,
                            val facebook: String?,
                            val phone: String?,
                            val email: String?,
                            val gPlus: String?,
                            val shareEmail: Boolean) : DatabaseTaskKot(c)
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
                userAccount.facebook = facebook
                userAccount.phone = phone
                userAccount.email = email
                userAccount.gPlus = gPlus
                userAccount.emailPublic = shareEmail
                dao.createOrUpdate(userAccount)

                AppPrefs.getInstance(context).setName(name)
                AppPrefs.getInstance(context).setEmail(email)
                PersistedTaskQueueFactory.getInstance(context).execute(UpdateUserProfileCommand())
            }
        }
    }

}

class UpdateUserProfileCommand() : RetrofitPersistedTask()
{
    override fun handleError(context: Context?, e: Throwable?): Boolean {
        Log.w("asdf", "Whoops", e);
        Crashlytics.logException(e)
        return true;
    }

    override fun logSummary(): String?
    {
        return "";
    }

    override fun same(command: PersistedTask): Boolean
    {
        return command is UpdateUserProfileCommand
    }
    override fun runNetwork(context: Context?) {
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
                        null,
                        null,
                        userAccount.phone,
                        userAccount.email,
                        userAccount.gPlus,
                        userAccount.facebook,
                        userAccount.emailPublic
                )
            }
            else
            {
                throw RuntimeException("User update failed")
            }
        }

    }

}