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
import co.touchlab.droidconandroid.data.ScheduleBlock
import co.touchlab.droidconandroid.data.Track
import co.touchlab.droidconandroid.ui.EventAdapter
import co.touchlab.droidconandroid.ui.EventClickListener
import co.touchlab.droidconandroid.utils.Toaster

class ScheduleDataFragment() : Fragment()
{
    var eventList: RecyclerView? = null
    var adapter: EventAdapter? = null
    private var allEvents = true
    private var day: Long? = null
    private var position: Int? = null

    companion object
    {
        val ALL_EVENTS = "ALL_EVENTS"
        val DAY = "DAY"
        val POSITION = "POSITION"

        fun newInstance(all: Boolean, day: Long, position: Int): ScheduleDataFragment
        {
            val scheduleDataFragment = ScheduleDataFragment()
            val args = Bundle()
            args.putBoolean(ALL_EVENTS, all)
            args.putLong(DAY, day)
            args.putInt(POSITION, position)
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
        day = getArguments()!!.getLong(DAY)
        position = getArguments()!!.getInt(POSITION)

        eventList = getView().findViewById(R.id.eventList) as RecyclerView
        eventList!!.setLayoutManager(LinearLayoutManager(getActivity()))

        //http://stackoverflow.com/a/28884330
        getParentFragment().getLoaderManager()!!.initLoader(position!!, null, this.ScheduleDataLoaderCallbacks())

    }

    inner class ScheduleDataLoaderCallbacks() : LoaderManager.LoaderCallbacks<List<ScheduleBlock>>
    {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<ScheduleBlock>>?
        {
            return ScheduleDataLoader(getActivity()!!, allEvents, day!!)
        }

        override fun onLoadFinished(loader: Loader<List<ScheduleBlock>>?, data: List<ScheduleBlock>?)
        {
            if (data == null)
            {
                Toaster.showMessage(getActivity(), "NoData")
            }
            else
            {
                adapter = EventAdapter(data, allEvents, (getActivity() as FilterInterface).getCurrentFilters(), object : EventClickListener{
                    override fun onEventClick(event: Event) {
                        EventDetailActivity.callMe(getActivity()!!, event.id, event.category)
                    }
                })
                eventList!!.setAdapter(adapter!!)
            }

        }
        override fun onLoaderReset(loader: Loader<List<ScheduleBlock>>?)
        {

        }
    }

    fun filter(track: Track) {
        if(adapter != null) {
            adapter!!.update(track)
        }
    }

}