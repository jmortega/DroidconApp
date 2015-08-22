package co.touchlab.droidconandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity

/**
 * Created by kgalligan on 8/1/14.
 */
class ScheduleDataActivity : FragmentActivity()
{
    companion object
    {
        val USER_ID = "USER_ID"

        fun callMe(a: Activity)
        {
            val i = Intent(a, javaClass <ScheduleDataActivity> ())
            a.startActivity(i)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super < FragmentActivity >.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_data)
    }
}

