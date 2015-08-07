package co.touchlab.droidconandroid

import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.text.TextUtils
import com.squareup.picasso.Picasso
import android.text.Html
import co.touchlab.droidconandroid.utils.TextHelper
import co.touchlab.android.threading.tasks.TaskQueue
import co.touchlab.droidconandroid.tasks.EventDetailLoadTask
import org.apache.commons.lang3.StringUtils
import java.text.SimpleDateFormat
import co.touchlab.droidconandroid.tasks.AddRsvpTaskKot
import co.touchlab.droidconandroid.tasks.RemoveRsvpTaskKot
import co.touchlab.droidconandroid.data.UserAccount
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.FragmentActivity
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.*
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.droidconandroid.data.Event
import com.wnafee.vector.compat.ResourcesCompat
import java.util.*

/**
 * Created by kgalligan on 7/27/14.
 */
class EventDetailFragment() : Fragment()
{
    private var name: TextView? = null
    private var backdrop: ImageView? = null
    private var fab: FloatingActionButton? = null
    private var collapsingToolbar: CollapsingToolbarLayout? = null
    private var recycler: RecyclerView? = null

    private var trackColor: Int = 0
    private var fabColorList: ColorStateList? = null

    companion object
    {
        val EVENT_ID = "EVENT_ID"
        val TRACK_ID = "TRACK_ID"

        fun createFragment(id: Long, track: Int): EventDetailFragment
        {
            val bundle = Bundle()
            bundle.putLong(EVENT_ID, id)
            bundle.putInt(TRACK_ID, track);

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

        if (eventId == -1L)
            throw IllegalArgumentException("Must set event id");

        return eventId
    }

    /**
     * Gets the track ID argument. This is to make sure we dont flash the incorrect colors
     * on things like the FAB and toolbar while waiting to load the event details
     */
    private fun findTrackIdArg(): Int
    {
        var trackId = getArguments()?.getInt(TRACK_ID, -1)
        if (trackId == null || trackId == -1)
        {
            trackId = getActivity()!!.getIntent()!!.getIntExtra(TRACK_ID, -1)
        }

        if (trackId == -1)
            throw IllegalArgumentException("Must set track id");

        return trackId
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater!!.inflate(R.layout.fragment_event_detail, null)!!

        var toolbar = view.findViewById(R.id.toolbar) as Toolbar
        var activity = getActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true)
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true)

        //collect the views
        name = view.findViewById(R.id.name) as TextView
        backdrop = view.findViewById(R.id.backdrop) as ImageView
        fab = view.findViewById(R.id.register) as FloatingActionButton
        collapsingToolbar = view.findViewById(R.id.collapsingToolbar) as CollapsingToolbarLayout
        recycler = view.findViewById(R.id.recycler) as RecyclerView

        recycler!!.setLayoutManager(LinearLayoutManager(getActivity()))

        updateTrackColor(findTrackIdArg())

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

        updateTrackColor(0)
        updateToolbar(event)
        updateFAB(event)

        updateContent(event, eventDetailTask.speakers)
   }

    public fun onEventMainThread([suppress("UNUSED_PARAMETER")] task: AddRsvpTaskKot)
    {
        startDetailRefresh()
    }

    public fun onEventMainThread([suppress("UNUSED_PARAMETER")] task: RemoveRsvpTaskKot)
    {
        startDetailRefresh()
    }

    /**
     * Sets up the floating action bar according to the event details. This includes setting the color
     * and adjusting the icon according to rsvp status
     */
    private fun updateFAB(event: Event)
    {
        //Follow Fab
        fab!!.setBackgroundTintList(fabColorList)
        fab!!.setRippleColor(trackColor)

        if (event.isRsvped())
        {
            fab!!.setImageDrawable(ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_check))
        }
        else
        {
            fab!!.setImageDrawable(ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_plus))
        }

        fab!!.setOnClickListener { v ->
            if (event.isRsvped()) {
                TaskQueue.loadQueueDefault(getActivity()).execute(RemoveRsvpTaskKot(getActivity()!!, event.id))
            } else {
                TaskQueue.loadQueueDefault(getActivity()).execute(AddRsvpTaskKot(getActivity()!!, event.id))
            }
            //Chage this if we have a tablet situation
            getActivity()!!.finish()
        }

        var p = fab!!.getLayoutParams() as CoordinatorLayout.LayoutParams
        p.setAnchorId(R.id.appbar)
        fab!!.setLayoutParams(p)
        fab!!.setVisibility(View.VISIBLE)
    }

    /**
     * Updates the title and colors of the toolbar
     */
    private fun updateToolbar(event: Event)
    {
        name!!.setText(event.name)
        backdrop!!.setImageDrawable(ResourcesCompat.getDrawable(getActivity(), R.drawable.welcome_1))

        //Toolbar Colors
        collapsingToolbar!!.setContentScrimColor(trackColor)
        collapsingToolbar!!.setStatusBarScrimColor(trackColor)
    }

    /**
     * Adds all the content to the recyclerView
     */
    private fun updateContent(event: Event, speakers: ArrayList<UserAccount>?)
    {
        var adapter = EventDetailAdapter(getActivity(), getResources().getColor(R.color.track_accent_pink))

        //Construct the time and venue string and add it to the adapter
        val startDateVal = Date(event.startDateLong!!)
        val endDateVal = Date(event.endDateLong!!)
        val timeFormat = SimpleDateFormat("hh:mm a")
        val formatString = getResources().getString(R.string.event_venue_time);
        formatString.format(event.venue.name, timeFormat.format(startDateVal), timeFormat.format(endDateVal))

        adapter.addSpace(getResources().getDimensionPixelSize(R.dimen.height_small))
        adapter.addHeader(formatString.format(event.venue.name, timeFormat.format(startDateVal), timeFormat.format(endDateVal)), R.drawable.ic_map)

        //Description text
        adapter.addBody(event.description)

        //Track
        adapter.addHeader("on the TEMP track", R.drawable.ic_action_train)

        adapter.addDivider()

        for(item: UserAccount in speakers as ArrayList)
        {
            adapter.addSpeaker(item)
        }

        recycler!!.setAdapter(adapter)
    }

    /**
     * Ensures that all view which are colored according to the track are updated
     */
    private fun updateTrackColor(trackId: Int)
    {
        when (trackId)
        {
            0 ->
            {
                trackColor = getResources().getColor(R.color.droidcon_pink)
                fabColorList = getResources().getColorStateList(R.color.track_accent_pink)
            }
            else ->
            {
                trackColor = getResources().getColor(R.color.droidcon_blue)
                fabColorList = getResources().getColorStateList(R.color.track_accent_pink)
            }
        }
    }
}