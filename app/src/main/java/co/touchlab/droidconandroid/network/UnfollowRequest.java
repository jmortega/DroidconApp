package co.touchlab.droidconandroid.network;

import co.touchlab.android.superbus.errorcontrol.PermanentException;
import co.touchlab.android.superbus.errorcontrol.TransientException;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by kgalligan on 7/20/14.
 */
public interface UnfollowRequest
{
    @FormUrlEncoded
    @POST("/dataTest/unfollow")
    Response unfollow(@Field("uuid") String uuid, @Field("otherId") Long otherId) throws TransientException, PermanentException;
}
