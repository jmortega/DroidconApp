package co.touchlab.droidconandroid

import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.text.TextUtils
import com.squareup.picasso.Picasso
import android.text.Html
import co.touchlab.droidconandroid.utils.TextHelper
import android.widget.Button
import co.touchlab.android.threading.tasks.TaskQueue
import co.touchlab.droidconandroid.tasks.EventDetailLoadTask
import org.apache.commons.lang3.StringUtils
import java.text.SimpleDateFormat
import java.util.Date
import co.touchlab.droidconandroid.tasks.AddRsvpTaskKot
import co.touchlab.droidconandroid.tasks.RemoveRsvpTaskKot
import co.touchlab.droidconandroid.data.UserAccount
import android.widget.ArrayAdapter
import android.content.Context
import android.widget.ListView
import co.touchlab.android.threading.eventbus.EventBusExt

/**
 * Created by kgalligan on 7/27/14.
 */
class EventDetailFragment() : Fragment()
{
    private var name: TextView? = null
    private var description: TextView? = null
    private var venue: TextView? = null
    private var date: TextView? = null
    private var startTime: TextView? = null
    private var endTime: TextView? = null
    private var speakerList: ListView? = null
    private var rsvpButton: Button? = null

    companion object
    {
        val HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES: String = "https://s3.amazonaws.com/droidconimages/"
        val EVENT_ID = "EVENT_ID"

        fun createFragment(id: Long): EventDetailFragment
        {
            val bundle = Bundle()
            bundle.putLong(EVENT_ID, id);

            val f = EventDetailFragment()
            f.setArguments(bundle);

            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<Fragment>.onCreate(savedInstanceState)
        EventBusExt.getDefault()!!.register(this)
    }

    override fun onDestroy()
    {
        super<Fragment>.onDestroy()
        EventBusExt.getDefault()!!.unregister(this)
    }

    private fun findEventIdArg(): Long
    {
        var eventId = getArguments()?.getLong(EVENT_ID, -1)
        if (eventId == null || eventId == -1L)
        {
            eventId = getActivity()!!.getIntent()!!.getLongExtra(EVENT_ID, -1)
        }

        if (eventId == null || eventId == -1L)
            throw IllegalArgumentException("Must set event id");

        return eventId!!
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater!!.inflate(R.layout.fragment_event_detail, null)!!

        name = view.findView(R.id.name) as TextView
        description = view.findView(R.id.description) as TextView
        venue = view.findView(R.id.venue) as TextView
        date = view.findView(R.id.date) as TextView
        startTime = view.findView(R.id.startTime) as TextView
        endTime = view.findView(R.id.endTime) as TextView
        speakerList = view.findView(R.id.speakerList) as ListView
        rsvpButton = view.findView(R.id.rsvpButton) as Button

        startDetailRefresh()

        return view
    }

    private fun startDetailRefresh()
    {
        TaskQueue.loadQueueDefault(getActivity()).execute(EventDetailLoadTask(getActivity()!!, findEventIdArg()))
    }

    public fun onEventMainThread(eventDetailTask: EventDetailLoadTask)
    {
        if (!eventDetailTask.eventId.equals(findEventIdArg()))
            return

        val event = eventDetailTask.event!!

        name!!.setText(event.name)

        val descriptionString = Html.fromHtml(TextHelper.findTagLinks(StringUtils.trimToEmpty(event.description)!!))
        description!!.setText(descriptionString)
        venue!!.setText(event.venue.name)
        val startDateVal = Date(event.startDateLong!!)
        val endDateVal = Date(event.endDateLong!!)
        date!!.setText(SimpleDateFormat("MM/dd").format(startDateVal))
        val timeFormat = SimpleDateFormat("hh:mm a")
        startTime!!.setText(timeFormat.format(startDateVal))
        endTime!!.setText(timeFormat.format(endDateVal))

        if (event.isRsvped())
        {
            rsvpButton!!.setText(R.string.unregister)
        }
        else
        {
            rsvpButton!!.setText(R.string.register)
        }

        rsvpButton!!.setOnClickListener { v ->
            if (event.isRsvped())
            {
                TaskQueue.loadQueueDefault(getActivity()).execute( RemoveRsvpTaskKot(getActivity()!!, event.id))
            }
            else
            {
                TaskQueue.loadQueueDefault(getActivity()).execute(AddRsvpTaskKot(getActivity()!!, event.id))
            }
            //Chage this if we have a tablet situation
            getActivity()!!.finish()
        }

        speakerList!!.setAdapter(EventSpeakersAdapter(getActivity()!!, eventDetailTask.speakers!!))


        speakerList!!.setOnItemClickListener { adapterView, view, position, id ->
            val userAccount = speakerList!!.getAdapter()!!.getItem(position) as UserAccount
            UserDetailActivity.callMe(getActivity()!!, userAccount.id!!)
        }
    }

    public fun onEventMainThread(task: AddRsvpTaskKot)
    {
        startDetailRefresh()
    }

    public fun onEventMainThread(task: RemoveRsvpTaskKot)
    {
        startDetailRefresh()
    }

    inner class EventSpeakersAdapter(c: Context, speakers: List<UserAccount>) : ArrayAdapter<UserAccount>(c, android.R.layout.simple_list_item_1, speakers)
    {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View?
        {
            var view = if (convertView == null)LayoutInflater.from(getActivity()!!).inflate(R.layout.list_user_summary, null) else convertView
            val avatarView = view!!.findView(R.id.userAvatar) as ImageView
            val userName = view!!.findView(R.id.userName) as TextView

            val userAccount = getItem(position)!!
            if (!TextUtils.isEmpty(userAccount.avatarKey))
            {
                Picasso.with(getActivity())!!.load(HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES + userAccount.avatarKey)!!.into(avatarView)
                avatarView.setVisibility(View.VISIBLE)
            }
            else
            {
                avatarView.setVisibility(View.GONE)
            }

            userName.setText(userAccount.name)

            return view
        }
    }
}