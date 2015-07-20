package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.android.threading.tasks.TaskQueue
import co.touchlab.droidconandroid.network.DataHelper
import co.touchlab.android.superbus.appsupport.CommandBusHelper
import co.touchlab.droidconandroid.network.EmailLoginRequest
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.superbus.RefreshScheduleDataKot
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.droidconandroid.data.UserAuthHelper
import com.google.android.gms.auth.GoogleAuthUtil
import co.touchlab.droidconandroid.network.GoogleLoginRequest
import android.text.TextUtils
import co.touchlab.droidconandroid.superbus.UploadAvatarCommand
import org.apache.commons.lang3.StringUtils
import co.touchlab.droidconandroid.network.dao.LoginResult

/**
 * Created by kgalligan on 7/20/14.
 */
open class EmailLoginTask(val email: String, val name: String?, val password: String?) : AbstractLoginTask()
{
    override fun run(context: Context?)
    {
        val restAdapter = DataHelper.makeRequestAdapter(context)
        val emailLoginRequest = restAdapter!!.create(javaClass<EmailLoginRequest>())!!
        val loginResult = emailLoginRequest.emailLogin(email, password, name)

        handleLoginResult(context, loginResult)

        EventBusExt.getDefault()!!.post(this);
    }
    override fun handleError(e: Exception?): Boolean
    {
        return false
    }
}

class GoogleLoginTask(val email: String, val name: String?, val imageURL: String?) : AbstractLoginTask()
{
    companion object
    {
        val SCOPE: String = "audience:server:client_id:654878069390-0rs83f4a457ggmlln2jnmedv1b808bkv.apps.googleusercontent.com"
        val GOOGLE_LOGIN_COMPLETE: String = "GOOGLE_LOGIN_COMPLETE";
        val GOOGLE_LOGIN_UUID: String = "GOOGLE_LOGIN_UUID";
    }

    override fun run(context: Context?)
    {
        val token = GoogleAuthUtil.getToken(context, email, SCOPE)
        val restAdapter = DataHelper.makeRequestAdapter(context)
        val loginRequest = restAdapter!!.create(javaClass<GoogleLoginRequest>())!!
        val loginResult = loginRequest.login(token, name)

        handleLoginResult(context, loginResult)
        if (!TextUtils.isEmpty(imageURL))
            CommandBusHelper.submitCommandSync(context, UploadAvatarCommand(imageURL!!))

        EventBusExt.getDefault()!!.post(this);
    }

    override fun handleError(e: Exception?): Boolean
    {
        return false
    }
}

abstract class AbstractLoginTask : TaskQueue.Task()
{
    var firstLogin: Boolean = false

    fun handleLoginResult(context: Context?, loginResult: LoginResult?)
    {
        val userAccount = UserAuthHelper.processLoginResonse(context!!, loginResult!!)
        firstLogin = StringUtils.isEmpty(userAccount.profile) && StringUtils.isEmpty(userAccount.company)

        CommandBusHelper.submitCommandSync(context, RefreshScheduleDataKot())
    }
}