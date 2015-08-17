package co.touchlab.droidconandroid.network;

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
    Response unfollow(@Field("otherId") Long otherId) throws Exception;
}
