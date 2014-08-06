package co.touchlab.droidconandroid.network;

import co.touchlab.android.superbus.errorcontrol.PermanentException;
import co.touchlab.android.superbus.errorcontrol.TransientException;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by kgalligan on 7/20/14.
 */
public interface RemoveRsvpRequest
{
    @POST("/dataTest/unRsvpEvent/{eventId}")
    BasicIdResult removeRsvp(@Path("eventId") Long eventId) throws TransientException, PermanentException;
}
