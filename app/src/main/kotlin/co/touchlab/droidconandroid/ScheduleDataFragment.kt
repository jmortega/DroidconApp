package co.touchlab.droidconandroid

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.touchlab.droidconandroid.data.Event
import co.touchlab.droidconandroid.ui.EventAdapter
import co.touchlab.droidconandroid.ui.EventClickListener
import co.touchlab.droidconandroid.utils.Toaster

class ScheduleDataFragment() : Fragment()
{
    var eventList: RecyclerView? = null
    var adapter: EventAdapter? = null
    private var allEvents = true

    companion object
    {
        val ALL_EVENTS = "ALL_EVENTS"
        val DAY = "DAY"

        fun newInstance(all: Boolean, day: Long): ScheduleDataFragment
        {
            val scheduleDataFragment = ScheduleDataFragment()
            val args = Bundle()
            args.putBoolean(ALL_EVENTS, all)
            args.putLong(DAY, day)
            scheduleDataFragment.setArguments(args)
            return scheduleDataFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater!!.inflate(R.layout.fragment_schedule_data, null)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        allEvents = getArguments()!!.getBoolean(ALL_EVENTS)

        eventList = getView().findViewById(R.id.eventList) as RecyclerView
        eventList!!.setLayoutManager(LinearLayoutManager(getActivity()))

        getLoaderManager()!!.initLoader(0, null, this.ScheduleDataLoaderCallbacks())

    }

    inner class ScheduleDataLoaderCallbacks() : LoaderManager.LoaderCallbacks<List<Event>>
    {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Event>>?
        {
            return ScheduleDataLoader(getActivity()!!, allEvents, getArguments()!!.getLong(DAY))
        }

        override fun onLoadFinished(loader: Loader<List<Event>>?, data: List<Event>?)
        {
            if (data == null)
            {
                Toaster.showMessage(getActivity(), "NoData")
            }
            else
            {
                adapter = EventAdapter(data, allEvents, object : EventClickListener{
                    override fun onEventClick(event: Event) {
                        EventDetailActivity.callMe(getActivity()!!, event.id)
                    }
                })
                eventList!!.setAdapter(adapter!!)
            }

        }
        override fun onLoaderReset(loader: Loader<List<Event>>?)
        {

        }
    }

}