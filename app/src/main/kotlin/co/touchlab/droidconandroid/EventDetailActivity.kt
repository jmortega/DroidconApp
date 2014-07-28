package co.touchlab.droidconandroid

import android.os.Bundle
import android.app.Activity
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import co.touchlab.android.threading.tasks.BsyncTaskManager
import co.touchlab.droidconandroid.tasks.FindUserTaskKot
import co.touchlab.droidconandroid.utils.Toaster
import android.text.TextUtils
import com.squareup.picasso.Picasso
import android.text.Html
import co.touchlab.droidconandroid.utils.TextHelper
import android.text.method.LinkMovementMethod
import co.touchlab.droidconandroid.tasks.UserInfoUpdate
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask
import android.content.Intent
import co.touchlab.droidconandroid.tasks.FindUserByIdTask
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