package co.touchlab.droidconandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import co.touchlab.android.threading.eventbus.EventBusExt;
import co.touchlab.android.threading.tasks.TaskQueue;
import co.touchlab.droidconandroid.data.Event;
import co.touchlab.droidconandroid.tasks.AddRsvpTaskKot;
import co.touchlab.droidconandroid.tasks.EventDataLoadTask;
import co.touchlab.droidconandroid.tasks.RemoveRsvpTaskKot;

import java.util.List;

public class DebugScheduleDisplayActivity extends Activity
{

    private ListView eventList;
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_schedule_display);
        eventList = (ListView) findViewById(R.id.eventList);
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Event event = adapter.getItem(position);
                Intent intent = new Intent(DebugScheduleDisplayActivity.this, EventDetailActivity.class);
                intent.putExtra("EVENT_ID", event.id);
                startActivity(intent);
            }
        });
        EventBusExt.getDefault().register(this);
        runDataReload();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBusExt.getDefault().unregister(this);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(EventDataLoadTask eventDataLoad)
    {
        adapter = new EventAdapter(this, eventDataLoad.getEvents());
        eventList.setAdapter(adapter);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(AddRsvpTaskKot task)
    {
        runDataReload();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(RemoveRsvpTaskKot task)
    {
        runDataReload();
    }

    private void runDataReload()
    {
        TaskQueue.execute(DebugScheduleDisplayActivity.this, new EventDataLoadTask(this));
    }

    private class EventAdapter extends ArrayAdapter<Event>
    {

        public EventAdapter(Context context, List<Event> objects)
        {
            super(context, android.R.layout.simple_list_item_1, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = LayoutInflater.from(DebugScheduleDisplayActivity.this).inflate(R.layout.default_schedule_list_row, null);
            }

            TextView eventName = (TextView) convertView.findViewById(R.id.eventName);
            TextView eventDescription = (TextView) convertView.findViewById(R.id.eventDescription);
            TextView rsvpStatus = (TextView) convertView.findViewById(R.id.rsvpStatus);

            Event event = getItem(position);

            eventName.setText(event.name);
            eventDescription.setText(event.description);
            rsvpStatus.setText(event.rsvpUuid == null ? "No" : "Yes");

            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.debug_schedule_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void callMe(Context c)
    {
        Intent i = new Intent(c, DebugScheduleDisplayActivity.class);
        c.startActivity(i);
    }
}
