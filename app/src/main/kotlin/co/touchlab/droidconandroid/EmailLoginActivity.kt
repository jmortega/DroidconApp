package co.touchlab.droidconandroid

import android.app.Activity
import android.os.Bundle
import android.widget.EditText
import co.touchlab.android.threading.tasks.TaskQueue
import co.touchlab.droidconandroid.tasks.EmailLoginTask
import co.touchlab.droidconandroid.tasks.AddRsvpTaskKot
import android.content.Context
import android.content.Intent
import co.touchlab.droidconandroid.tasks.LocalUserDisplayNameTask
import android.text.TextUtils
import de.greenrobot.event.EventBus

/**
 * Created by kgalligan on 7/27/14.
 */
class EmailLoginActivity : Activity()
{
    var name: EditText? = null
    var email: EditText? = null
    var password: EditText? = null

    public class object
    {
        public fun startMe(c: Context)
        {
            val i = Intent(c, javaClass<EmailLoginActivity>())
            c.startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<Activity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_login)
        name = findView(R.id.name) as EditText
        email = findView(R.id.emailTest) as EditText
        password = findView(R.id.password) as EditText

        findView(R.id.goButton).setOnClickListener { v ->
            TaskQueue.execute(this, EmailLoginTask(email!!.getText().toString(), name!!.getText().toString(), password!!.getText().toString()))
        }

        EventBus.getDefault()!!.register(this)

        TaskQueue.execute(this, LocalUserDisplayNameTask())
    }

    override fun onDestroy()
    {
        super<Activity>.onDestroy()
        EventBus.getDefault()!!.unregister(this)
    }

    public fun onEventMainThread(task: LocalUserDisplayNameTask)
    {
        if(task.displayName != null && TextUtils.isEmpty(name!!.getText().toString()))
            name!!.setText(task.displayName)
    }

    public fun onEventMainThread(task: EmailLoginTask)
    {
        finish()
        MyActivity.startMe(this)
    }
}