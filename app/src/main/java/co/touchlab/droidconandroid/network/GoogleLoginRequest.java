package co.touchlab.droidconandroid.network;

import co.touchlab.droidconandroid.network.dao.LoginResult;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by kgalligan on 7/20/14.
 */
public interface GoogleLoginRequest
{
    @FormUrlEncoded
    @POST("/deviceAuth/loginUser")
    LoginResult login(@Field("googleToken") String googleToken, @Field("name") String name) throws Exception;
}
