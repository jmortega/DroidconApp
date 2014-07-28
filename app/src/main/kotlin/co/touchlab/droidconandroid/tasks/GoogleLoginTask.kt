package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.android.threading.tasks.TaskQueue
import com.google.android.gms.auth.GoogleAuthUtil
import co.touchlab.droidconandroid.network.DataHelper
import android.text.TextUtils
import co.touchlab.android.superbus.appsupport.CommandBusHelper
import co.touchlab.droidconandroid.superbus.UploadAvatarCommand
import de.greenrobot.event.EventBus

/**
 * Created by kgalligan on 7/20/14.
 */
open class GoogleLoginTask(val email:String, val name: String?, val imageURL: String?) : TaskQueue.Task
{
    class object
    {
        val SCOPE: String = "audience:server:client_id:654878069390-0rs83f4a457ggmlln2jnmedv1b808bkv.apps.googleusercontent.com"
        val GOOGLE_LOGIN_COMPLETE: String = "GOOGLE_LOGIN_COMPLETE";
        val GOOGLE_LOGIN_UUID: String = "GOOGLE_LOGIN_UUID";
    }

    override fun run(context: Context?)
    {
        val token = GoogleAuthUtil.getToken(context, email, SCOPE)
        DataHelper.loginGoogle(context, token, name)
        if (!TextUtils.isEmpty(imageURL))
            CommandBusHelper.submitCommandSync(context, UploadAvatarCommand(imageURL))

        EventBus.getDefault()!!.post(this);
    }
    override fun handleError(e: Exception?): Boolean
    {
        return false
    }
}