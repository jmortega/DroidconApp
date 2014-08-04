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
            UserAuthHelper.processLoginResonse(context!!, loginResult!!)
            CommandBusHelper.submitCommandSync(context, RefreshScheduleDataKot())
        }

        EventBusExt.getDefault()!!.post(this);
    }
    override fun handleError(e: Exception?): Boolean
    {
        return false
    }
}