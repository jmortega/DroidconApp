package co.touchlab.droidconandroid

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity

/**
 * Created by kgalligan on 7/27/14.
 */
class UserDetailActivity : FragmentActivity()
{
    companion object
    {
        val USER_ID = "USER_ID"
        fun callMe(a: Activity, id: Long)
        {
            val i = Intent(a, javaClass<UserDetailActivity>())
            i.putExtra(USER_ID, id)
            a.startActivity(i)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<FragmentActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
    }
}