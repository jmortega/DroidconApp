package co.touchlab.droidconandroid.superbus;

import android.content.Context;
import android.util.Log;
import co.touchlab.android.superbus.CheckedCommand;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.errorcontrol.PermanentException;
import co.touchlab.android.superbus.errorcontrol.TransientException;
import co.touchlab.android.superbus.http.RetrofitBusErrorHandler;
import co.touchlab.droidconandroid.R;
import co.touchlab.droidconandroid.data.AppPrefs;
import co.touchlab.droidconandroid.network.BasicIdResult;
import co.touchlab.droidconandroid.network.DataHelper;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.*;

/**
 * Created by kgalligan on 6/28/14.
 */
public class AddRsvpCommand extends CheckedCommand
{
    private Long eventId;
    private String rsvpUuid;

    interface AddRsvpRequest
    {
        @FormUrlEncoded
        @POST("/dataTest/rsvpEvent/{eventId}")
        BasicIdResult addRsvp(@Path("eventId") Long eventId, @Field("uuid") String uuid, @Field("rsvpUuid") String rsvpUuid);
    }

    public AddRsvpCommand()
    {
    }

    @Override
    public boolean handlePermanentError(Context context, PermanentException exception)
    {
        return false;
    }

    public AddRsvpCommand(Long eventId, String rsvpUuid)
    {
        this.eventId = eventId;
        this.rsvpUuid = rsvpUuid;
    }

    @Override
    public String logSummary()
    {
        return "AddRsvp - " + eventId;
    }

    @Override
    public boolean same(Command command)
    {
        return false;
    }

    @Override
    public void callCommand(Context context) throws TransientException, PermanentException
    {
        RestAdapter restAdapter = DataHelper.makeRequestAdapter(context);
        AddRsvpRequest addRsvpRequest = restAdapter.create(AddRsvpRequest.class);

        BasicIdResult basicIdResult = addRsvpRequest.addRsvp(eventId, AppPrefs.getInstance(context).getUserUuid(), rsvpUuid);

        Log.w("asdf", "Result id: "+ basicIdResult.id);
    }


}
