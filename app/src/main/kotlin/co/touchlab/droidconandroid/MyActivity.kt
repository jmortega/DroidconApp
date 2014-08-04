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
            EnterUuidActivity.startMe(this@MyActivity)
            finish()
        }

        setContentView(R.layout.activity_my)

        findView(R.id.showSchedule).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                ScheduleDataActivity.callMe(this@MyActivity)
            }
        })

        findView(R.id.goFindUser).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                FindUserKot.startMe(this@MyActivity)
            }
        })

        findView(R.id.goMyProfile).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                MyProfileActivity.callMe(this@MyActivity)
            }
        })

        findView(R.id.updateProfile).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                EditUserProfile.callMe(this@MyActivity)
            }
        })

    }
}