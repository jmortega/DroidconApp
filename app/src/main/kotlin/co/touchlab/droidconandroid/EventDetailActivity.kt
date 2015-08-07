package co.touchlab.droidconandroid

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

/**
 * Created by kgalligan on 7/27/14.
 */
class EventDetailActivity : AppCompatActivity()
{
    companion object
    {
        val EVENT_ID = "EVENT_ID"
        val TRACK_ID = "TRACK_ID"
        fun callMe(a: Activity, id: Long, track: Int)
        {
            val i = Intent(a, javaClass<EventDetailActivity>())
            i.putExtra(EVENT_ID, id)
            i.putExtra(TRACK_ID, track)
            a.startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if (item.getItemId() == android.R.id.home)
            finish()

        return super.onOptionsItemSelected(item)
    }
}