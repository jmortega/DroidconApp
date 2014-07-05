package co.touchlab.droidconandroid.tasks;

import android.content.Context;
import co.touchlab.android.superbus.appsupport.CommandBusHelper;
import co.touchlab.droidconandroid.data.DatabaseHelper;
import co.touchlab.droidconandroid.data.Event;
import co.touchlab.droidconandroid.superbus.AddRsvpCommand;
import com.j256.ormlite.dao.Dao;

import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Created by kgalligan on 6/28/14.
 */
public class AddRsvpTask extends DatabaseTask
{
    private Long eventId;

    public AddRsvpTask(Context context, Long eventId)
    {
        super(context);
        this.eventId = eventId;
    }

    @Override
    public void run() throws Exception
    {
        getDatabase().performTransactionOrThrowRuntime(new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                DatabaseHelper database = getDatabase();
                Dao<Event, Long> dao = database.getDao(Event.class);
                Event event = dao.queryForId(eventId);
                if(event.rsvpUuid == null)
                {
                    event.rsvpUuid = UUID.randomUUID().toString();
                    dao.update(event);
                    CommandBusHelper.submitCommandSync(getContext(), new AddRsvpCommand(eventId, event.rsvpUuid));
                }

                return null;
            }
        });
    }

    @Override
    public boolean handleError(Exception e)
    {
        return false;
    }
}
