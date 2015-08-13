package co.touchlab.droidconandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by kgalligan on 7/27/14.
 */
class UserDetailActivity : AppCompatActivity(), UserDetailFragment.Companion.FinishListener
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
        super<AppCompatActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
    }

    override fun onFragmentFinished() {
        finish()
    }
}