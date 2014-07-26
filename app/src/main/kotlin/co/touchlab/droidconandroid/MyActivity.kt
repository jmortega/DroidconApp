package co.touchlab.droidconandroid

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.content.Intent
import co.touchlab.droidconandroid.data.AppPrefs
import android.view.View

public class MyActivity : Activity()
{
    public class object
    {
        public fun startMe(c : Context)
        {
            val i = Intent(c, javaClass<MyActivity>())
            c.startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        if (!AppPrefs.getInstance(this).isLoggedIn())
        {
            DebugEnterUuidActivity.callMe(this@MyActivity)
            finish()
        }

        setContentView(R.layout.activity_my)

        findView(R.id.showSchedule).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                DebugScheduleDisplayActivity.callMe(this@MyActivity)
            }
        })
        findView(R.id.goFindUser).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                FindUser.callMe(this@MyActivity)
            }
        })

    }
}