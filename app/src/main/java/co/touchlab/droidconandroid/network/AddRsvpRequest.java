package co.touchlab.droidconandroid.network;

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
    BasicIdResult addRsvp(@Path("eventId") Long eventId, @Field("rsvpUuid") String rsvpUuid) throws Exception;
}
