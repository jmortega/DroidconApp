package co.touchlab.droidconandroid.tasks;

import android.content.Context;
import co.touchlab.droidconandroid.data.Event;
import com.j256.ormlite.dao.Dao;
import de.greenrobot.event.EventBus;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by kgalligan on 6/28/14.
 */
public class SimpleEventDataLoadTask extends DatabaseTask
{
    public List<Event> events;

    public SimpleEventDataLoadTask(Context context)
    {
        super(context);
    }

    @Override
    public void run() throws Exception
    {
        Dao<Event, Long> dao = getDatabase().getDao(Event.class);
        List<Event> events = dao.queryForAll();
        Collections.sort(events, new Comparator<Event>()
        {
            @Override
            public int compare(Event lhs, Event rhs)
            {
                if(lhs.venue.id != rhs.venue.id)
                    return lhs.venue.name.compareTo(rhs.venue.name);

                return lhs.startDate.compareTo(rhs.startDate);
            }
        });

        this.events = events;

        EventBus.getDefault().post(this);
    }

    @Override
    public boolean handleError(Exception e)
    {
        return false;
    }

}
