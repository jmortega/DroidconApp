package co.touchlab.droidconandroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import co.touchlab.droidconandroid.data.AppPrefs

public class MyActivity : Activity()
{
    public companion object
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


        if (!AppPrefs.getInstance(this).getHasSeenWelcome())
        {
            startActivity(WelcomeActivity.getLaunchIntent(this@MyActivity))
            finish()
        }
        else if (!AppPrefs.getInstance(this).isLoggedIn())
        {
            startActivity(SignInActivity.getLaunchIntent(this@MyActivity))
            finish()
        }

        setContentView(R.layout.activity_my)

        findView(R.id.showSchedule).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                MainHomeActivity.callMe(this@MyActivity)
            }
        })

        findView(R.id.goFindUser).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                FindUserKot.startMe(this@MyActivity)
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