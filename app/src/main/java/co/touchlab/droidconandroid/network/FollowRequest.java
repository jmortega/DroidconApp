package co.touchlab.droidconandroid.network;

import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by kgalligan on 7/20/14.
 */
public interface FollowRequest
{
    @FormUrlEncoded
    @POST("/dataTest/follow")
    Response follow(@Field("otherId") Long otherId) throws Exception;
}
