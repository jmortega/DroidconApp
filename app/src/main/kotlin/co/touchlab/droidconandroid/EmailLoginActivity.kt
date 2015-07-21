package co.touchlab.droidconandroid

import android.app.Activity
import android.os.Bundle
import android.widget.EditText
import co.touchlab.android.threading.tasks.TaskQueue
import co.touchlab.droidconandroid.tasks.EmailLoginTask
import android.content.Context
import android.content.Intent
import co.touchlab.droidconandroid.tasks.LocalUserDisplayNameTask
import android.text.TextUtils
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.droidconandroid.tasks.AbstractLoginTask

/**
 * Created by kgalligan on 7/27/14.
 */
class EmailLoginActivity : Activity()
{
    var name: EditText? = null
    var email: EditText? = null
    var password: EditText? = null

    public companion  object
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
            TaskQueue.loadQueueDefault(this).execute(EmailLoginTask(email!!.getText().toString(), name!!.getText().toString(), password!!.getText().toString()))
        }

        EventBusExt.getDefault()!!.register(this)

        TaskQueue.loadQueueDefault(this).execute(LocalUserDisplayNameTask())
    }

    override fun onDestroy()
    {
        super<Activity>.onDestroy()
        EventBusExt.getDefault()!!.unregister(this)
    }

    public fun onEventMainThread(task: LocalUserDisplayNameTask)
    {
        if(task.displayName != null && TextUtils.isEmpty(name!!.getText().toString()))
            name!!.setText(task.displayName)
    }

    public fun onEventMainThread(task: AbstractLoginTask)
    {
        finish()
        MyActivity.startMe(this)
        if(task.firstLogin)
            EditUserProfile.callMe(this)
    }
}