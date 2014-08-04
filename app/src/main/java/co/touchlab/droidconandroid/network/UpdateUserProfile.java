package co.touchlab.droidconandroid.network;

import co.touchlab.android.superbus.errorcontrol.PermanentException;
import co.touchlab.android.superbus.errorcontrol.TransientException;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by kgalligan on 8/3/14.
 */
public interface UpdateUserProfile
{
    @FormUrlEncoded
    @POST("/dataTest/updateUserProfile")
    Response update(@Field("name") String name, @Field("profile") String profile, @Field("company") String company, @Field("twitter") String twitter, @Field("linkedIn") String linkedIn, @Field("website") String website) throws TransientException, PermanentException;
}
