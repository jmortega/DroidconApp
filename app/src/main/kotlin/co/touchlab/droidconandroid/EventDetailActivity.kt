package co.touchlab.droidconandroid

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity

/**
 * Created by kgalligan on 7/27/14.
 */
class EventDetailActivity : FragmentActivity()
{
    class object
    {
        val EVENT_ID = "EVENT_ID"
        fun callMe(a: Activity, id: Long)
        {
            val i = Intent(a, javaClass<EventDetailActivity>())
            i.putExtra(EVENT_ID, id)
            a.startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<FragmentActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
    }
}