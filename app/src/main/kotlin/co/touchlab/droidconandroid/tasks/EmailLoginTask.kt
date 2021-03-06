package co.touchlab.droidconandroid.tasks

import android.content.Context
import android.text.TextUtils
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.Task
import co.touchlab.droidconandroid.data.UserAuthHelper
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.droidconandroid.network.GoogleLoginRequest
import co.touchlab.droidconandroid.superbus.RefreshScheduleDataKot
import co.touchlab.droidconandroid.superbus.UploadAvatarCommand
import co.touchlab.droidconandroid.superbus.UploadCoverCommand
import co.touchlab.droidconandroid.tasks.persisted.PersistedTaskQueueFactory
import com.crashlytics.android.Crashlytics
import com.google.android.gms.auth.GoogleAuthUtil
import org.apache.commons.lang3.StringUtils

/**
 * Created by kgalligan on 7/20/14.
 */
class GoogleLoginTask(val email: String, val name: String?, val imageURL: String?, val coverURL: String?) : Task()
{
    var failed: Boolean = false
    var firstLogin: Boolean = false

    override fun handleError(context: Context?, e: Throwable?): Boolean {
        Crashlytics.logException(e)
        failed = true
        EventBusExt.getDefault()!!.post(this);
        return true
    }

    companion object
    {
        val SCOPE: String = "audience:server:client_id:654878069390-ft2vt5sp4v0pcfk4poausabjnah0aeod.apps.googleusercontent.com"
        val GOOGLE_LOGIN_COMPLETE: String = "GOOGLE_LOGIN_COMPLETE";
        val GOOGLE_LOGIN_UUID: String = "GOOGLE_LOGIN_UUID";
    }

    override fun run(context: Context?)
    {
        val token = GoogleAuthUtil.getToken(context, email, SCOPE)
        val restAdapter = DataHelper.makeRequestAdapter(context)
        val loginRequest = restAdapter!!.create(javaClass<GoogleLoginRequest>())!!
        val loginResult = loginRequest.login(token, name)

        val userAccount = UserAuthHelper.processLoginResonse(context!!, loginResult!!)
        firstLogin = StringUtils.isEmpty(userAccount.profile) && StringUtils.isEmpty(userAccount.company)

        RefreshScheduleDataKot.callMe(context)

        if (!TextUtils.isEmpty(imageURL))
            PersistedTaskQueueFactory.getInstance(context).execute(UploadAvatarCommand(imageURL!!))

        if (!TextUtils.isEmpty(coverURL))
            PersistedTaskQueueFactory.getInstance(context).execute(UploadCoverCommand(coverURL!!))
    }

    override fun onComplete(context: Context?) {
        EventBusExt.getDefault()!!.post(this);
    }
}