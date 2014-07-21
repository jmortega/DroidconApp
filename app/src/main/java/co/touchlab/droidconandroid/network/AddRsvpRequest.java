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
public interface AddRsvpRequest
{
    @FormUrlEncoded
    @POST("/dataTest/rsvpEvent/{eventId}")
    BasicIdResult addRsvp(@Path("eventId") Long eventId, @Field("uuid") String uuid, @Field("rsvpUuid") String rsvpUuid) throws TransientException, PermanentException;
}
