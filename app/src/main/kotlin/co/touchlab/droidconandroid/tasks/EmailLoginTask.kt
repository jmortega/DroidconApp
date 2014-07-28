package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.android.threading.tasks.TaskQueue
import com.google.android.gms.auth.GoogleAuthUtil
import co.touchlab.droidconandroid.network.DataHelper
import android.text.TextUtils
import co.touchlab.android.superbus.appsupport.CommandBusHelper
import co.touchlab.droidconandroid.superbus.UploadAvatarCommand
import de.greenrobot.event.EventBus
import co.touchlab.droidconandroid.network.FindUserRequest
import co.touchlab.droidconandroid.network.EmailLoginRequest
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.superbus.RefreshScheduleDataKot

/**
 * Created by kgalligan on 7/20/14.
 */
open class EmailLoginTask(val email: String, val name: String?, val password: String?) : TaskQueue.Task
{
    override fun run(context: Context?)
    {
        val restAdapter = DataHelper.makeRequestAdapter(context)
        val emailLoginRequest = restAdapter!!.create(javaClass<EmailLoginRequest>())!!
        val loginResult = emailLoginRequest.emailLogin(email, password, name)

        if(loginResult?.uuid != null)
        {
            val appPrefs = AppPrefs.getInstance(context)
            appPrefs.setUserUuid(loginResult?.uuid)
            appPrefs.setUserId(loginResult?.userId)
            CommandBusHelper.submitCommandSync(context, RefreshScheduleDataKot())
        }

        EventBus.getDefault()!!.post(this);
    }
    override fun handleError(e: Exception?): Boolean
    {
        return false
    }
}