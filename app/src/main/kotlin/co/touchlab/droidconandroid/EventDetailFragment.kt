package co.touchlab.droidconandroid

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.TaskQueue
import co.touchlab.droidconandroid.data.Event
import co.touchlab.droidconandroid.data.Track
import co.touchlab.droidconandroid.data.UserAccount
import co.touchlab.droidconandroid.tasks.AddRsvpTaskKot
import co.touchlab.droidconandroid.tasks.EventDetailLoadTask
import co.touchlab.droidconandroid.tasks.RemoveRsvpTaskKot
import co.touchlab.droidconandroid.tasks.TrackDrawableTask
import com.wnafee.vector.compat.ResourcesCompat
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

/**
 * Created by kgalligan on 7/27/14.
 */
var buisnessDrawable: Drawable? = null
var designDrawable: Drawable? = null
var devDrawable: Drawable? = null
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
    private fun findTrackIdArg(): String?
    {
        var trackId = getArguments()?.getString(TRACK_ID)
        if (trackId == null)
        {
            trackId = getActivity()!!.getIntent()!!.getStringExtra(TRACK_ID)
        }

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
        backdrop!!.setBackgroundColor(trackColor)

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

        updateTrackColor(event.category)
        updateToolbar(event)
        updateFAB(event)

        updateContent(event, eventDetailTask.speakers)
   }

    public fun onEventMainThread(@suppress("UNUSED_PARAMETER") task: AddRsvpTaskKot)
    {
        startDetailRefresh()
    }

    public fun onEventMainThread(@suppress("UNUSED_PARAMETER") task: RemoveRsvpTaskKot)
    {
        startDetailRefresh()
    }

    public fun onEventMainThread(task: TrackDrawableTask)
    {
        when (task.drawableRes)
        {
            R.drawable.illo_development ->
            {
                devDrawable = task.drawable
            }

            R.drawable.illo_design ->
            {
                designDrawable = task.drawable
            }

            R.drawable.illo_business ->
            {
                buisnessDrawable = task.drawable
            }
        }

        if (task.drawable != null)
            updateBackdropDrawable(task.drawable!!)
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

        //Track
        var backdropDrawable: Drawable? = null
        if (!TextUtils.isEmpty(event.category))
        {
            var track = Track.findByServerName(event.category)
            when (track)
            {
                Track.DEVELOPMENT ->
                {
                    if (devDrawable == null)
                        TaskQueue.loadQueueDefault(getActivity()).execute(TrackDrawableTask(getActivity().getApplicationContext(), R.drawable.illo_development))
                    else
                        backdropDrawable = devDrawable
                }

                Track.DESIGN ->
                {
                    if (designDrawable == null)
                        TaskQueue.loadQueueDefault(getActivity()).execute(TrackDrawableTask(getActivity().getApplicationContext(), R.drawable.illo_design))
                    else
                        backdropDrawable = designDrawable
                }
                Track.BUSINESS ->
                {
                    if (buisnessDrawable == null)
                        TaskQueue.loadQueueDefault(getActivity()).execute(TrackDrawableTask(getActivity().getApplicationContext(), R.drawable.illo_business))
                    else
                        backdropDrawable = buisnessDrawable
                }
            }
        }

        if (backdropDrawable != null)
            updateBackdropDrawable(backdropDrawable)

        //Toolbar Colors
        collapsingToolbar!!.setContentScrimColor(trackColor)
        collapsingToolbar!!.setStatusBarScrimColor(trackColor)
    }

    /**
     * Adds all the content to the recyclerView
     */
    private fun updateContent(event: Event, speakers: ArrayList<UserAccount>?)
    {
        var adapter = EventDetailAdapter(getActivity(), trackColor)

        adapter.addSpace(getResources().getDimensionPixelSize(R.dimen.height_small))

        //Construct the time and venue string and add it to the adapter
        val startDateVal = Date(event.startDateLong!!)
        val endDateVal = Date(event.endDateLong!!)
        val timeFormat = SimpleDateFormat("hh:mm a")
        val venueFormatString = getResources().getString(R.string.event_venue_time);

        adapter.addHeader(venueFormatString.format(event.venue.name, timeFormat.format(startDateVal), timeFormat.format(endDateVal)), R.drawable.ic_map)

        //Description text
        if (!TextUtils.isEmpty(event.description))
            adapter.addBody(event.description)

        //Track
        if (!TextUtils.isEmpty(event.category))
        {
            var track = Track.findByServerName(event.category)
            var trackName = getResources().getString(track.getDisplayNameRes())
            val trackFormatString = getResources().getString(R.string.event_track);
            adapter.addHeader(trackFormatString.format(trackName), R.drawable.ic_track)
        }

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
    private fun updateTrackColor(category: String?)
    {
        //Default to design
        var track = null as Track?

        if (!TextUtils.isEmpty(category))
            track = Track.findByServerName(category)

        if(track == null)
            track = Track.findByServerName("Design")

        trackColor = getResources().getColor(track!!.getTextColorRes())
        fabColorList = getResources().getColorStateList(track.getCheckBoxSelectorRes())
    }

    private fun updateBackdropDrawable(backdropDrawable: Drawable)
    {
        backdrop!!.setImageDrawable(backdropDrawable)
    }
}