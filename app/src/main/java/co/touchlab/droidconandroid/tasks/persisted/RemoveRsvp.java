package co.touchlab.droidconandroid.tasks.persisted;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import co.touchlab.android.threading.tasks.helper.RetrofitPersistedTask;
import co.touchlab.droidconandroid.data.AppPrefs;
import co.touchlab.droidconandroid.network.DataHelper;
import co.touchlab.droidconandroid.network.RsvpRequest;

/**
 * Created by kgalligan on 8/21/15.
 */
public class RemoveRsvp extends RetrofitPersistedTask
{
    private Long eventId;

    public RemoveRsvp(){}

    public RemoveRsvp(Long eventId)
    {
        this.eventId = eventId;
    }

    @Override
    protected void runNetwork(Context context)
    {
        RsvpRequest rsvpRequest = DataHelper.makeRequestAdapter(context).create(RsvpRequest.class);
        String userUuid = AppPrefs.getInstance(context).getUserUuid();

        if (eventId != null && userUuid != null) {
            rsvpRequest.removeRsvp(eventId, "notneeded");
        } else {
            throw new IllegalArgumentException("Some value is null: " + eventId + "/" + userUuid);
        }
    }

    @Override
    protected boolean handleError(Context context, Throwable e)
    {
        Log.e("RSVP", "Error removing RSVP eventID: " + eventId , e);
        Crashlytics.logException(e);
        return true;
    }
}
