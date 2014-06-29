package co.touchlab.droidconandroid.superbus;

import android.content.Context;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.errorcontrol.PermanentException;
import co.touchlab.android.superbus.errorcontrol.TransientException;
import co.touchlab.droidconandroid.network.DataHelper;

/**
 * Created by kgalligan on 6/28/14.
 */
public class AddRsvpCommand extends Command
{
    private Long eventId;
    private String rsvpUuid;

    public AddRsvpCommand()
    {
    }

    public AddRsvpCommand(Long eventId, String rsvpUuid)
    {
        this.eventId = eventId;
        this.rsvpUuid = rsvpUuid;
    }

    @Override
    public String logSummary()
    {
        return "AddRsvp - "+ eventId;
    }

    @Override
    public boolean same(Command command)
    {
        return false;
    }

    @Override
    public void callCommand(Context context) throws TransientException, PermanentException
    {
        DataHelper.addRsvp(context, eventId, rsvpUuid);
    }
}
