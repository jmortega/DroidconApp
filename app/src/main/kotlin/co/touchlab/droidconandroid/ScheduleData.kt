package co.touchlab.droidconandroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.LoaderManager.LoaderCallbacks
import android.support.v4.content.Loader
import android.view
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import co.touchlab.droidconandroid.data.Event
import co.touchlab.droidconandroid.utils.Toaster
import org.apache.commons.lang3.StringUtils
import java.text.SimpleDateFormat
import java.util.*

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

