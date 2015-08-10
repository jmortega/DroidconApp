package co.touchlab.droidconandroid.ui

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.touchlab.droidconandroid.R
import co.touchlab.droidconandroid.data.Event
import co.touchlab.droidconandroid.data.Track
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

/**
 *
 * Created by izzyoji :) on 8/6/15.
 */
class EventAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    val VIEW_TYPE_EVENT: Int = 0

    private var dataSet: List<Event>
    private var filteredData: ArrayList<Event>
    private val eventClickListener: EventClickListener
    private val timeFormat = SimpleDateFormat("h:mma")
    private val allEvents: Boolean
    private var currentTracks: ArrayList<String> = ArrayList()


//    constructor(events: List<Event>, all: Boolean, initialFilters: List<String>,  eventClickListener: EventClickListener) : super() {
    constructor(events: List<Event>, all: Boolean, eventClickListener: EventClickListener) : super() {
        dataSet = events;
        filteredData = ArrayList(events);
        allEvents = all
        this.eventClickListener = eventClickListener
//        this.currentTracks = ArrayList(initialFilters);
        update(null)
    }

    override fun getItemCount(): Int {
        return filteredData.size()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        val v: View
        if (viewType == VIEW_TYPE_EVENT) {
            v = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.item_event, parent, false)
            return EventViewHolder(v)
        }
        throw UnsupportedOperationException()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val context = holder!!.itemView.getContext()
        val resources = context.getResources()
        if(getItemViewType(position) == VIEW_TYPE_EVENT){
            val eventHolder = holder as EventViewHolder
            val event = filteredData.get(position)
            if(position == 0)
            {
                Log.d("izzytest", "sigh " + event.endDateLong)
            }

            eventHolder.title.setText(event.name)
            var timeBlock = ""
            if(event.startDateLong != null && isFirstForTime(position))
            {
                val startDate = Date(event.startDateLong!!)
                timeBlock = timeFormat.format(startDate)
            }

            holder.time.setText(timeBlock.toLowerCase())

            holder.track.setBackgroundColor(resources.getColor(R.color.droidcon_blue))

            holder.card.setOnClickListener{
                eventClickListener.onEventClick(event)
            }

            if (allEvents && !TextUtils.isEmpty(event.rsvpUuid)) {
                holder.rsvp.setVisibility(View.VISIBLE)
            } else {
                holder.rsvp.setVisibility(View.GONE)
            }

            var locationTime = event.venue.name

            if(event.startDateLong != null && event.endDateLong != null)
            {
                val startDate = Date(event.startDateLong!!)
                val endDate = Date(event.endDateLong!!)

                var formattedStart = timeFormat.format(startDate).toLowerCase()
                val formattedEnd = timeFormat.format(endDate).toLowerCase()

                val startMarker = formattedStart.substring(Math.max(formattedStart.length() - 2, 0))
                val endMarker = formattedEnd.substring(Math.max(formattedEnd.length() - 2, 0))

                if(TextUtils.equals(startMarker, endMarker))
                {
                    formattedStart = formattedStart.substring(0, Math.max(formattedStart.length() - 2, 0))
                }

                locationTime += " " + formattedStart + " - " + formattedEnd
            }

            holder.locationTime.setText(locationTime)

            val track = Track.findByServerName(event.category)
            if(track != null) {
                holder.track.setBackgroundColor(resources.getColor(track.getTextColorRes()))
            }
            else
            {
                holder.track.setBackgroundColor(resources.getColor(android.R.color.transparent))
            }
        }
    }

    private fun isFirstForTime(position: Int): Boolean {
        if (position == 0) {
            return true
        } else {
            val prevEvent = filteredData.get(position - 1)
            val event = filteredData.get(position)
            val prevEventStart = prevEvent.startDateLong
            if(prevEventStart != null && (event.startDateLong != prevEventStart)) {
                return true
            }
        }
        return false
    }

    override fun getItemViewType(position: Int): Int {
            return VIEW_TYPE_EVENT
    }

    public class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public val title: TextView
        public val time: TextView
        public val locationTime: TextView
        public val track: View
        public val card: View
        public val rsvp: View

        init {
            title = itemView.findViewById(R.id.title) as TextView
            time = itemView.findViewById(R.id.time) as TextView
            locationTime = itemView.findViewById(R.id.location_time) as TextView
            track = itemView.findViewById(R.id.track)
            card = itemView.findViewById(R.id.card)
            rsvp = itemView.findViewById(R.id.rsvp)
        }
    }

    fun update(track: Track?) {
        if(track != null)
        {
            val trackServerName = track.getServerName()
            if(!currentTracks.contains(trackServerName))
            {
                currentTracks.add(trackServerName)
            }
            else
            {
                currentTracks.remove(trackServerName)
            }
        }

        filteredData.clear()
        if(currentTracks.isEmpty())
        {
            filteredData = ArrayList(dataSet)
        } else {
            for (event in dataSet) {
                val category = event.category
                if (!TextUtils.isEmpty(category) && currentTracks.contains(category)) {
                    filteredData.add(event)
                }
            }
        }

        notifyDataSetChanged()
    }

}

interface EventClickListener {

    fun onEventClick(event: Event)

}
