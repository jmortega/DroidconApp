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
public interface EmailLoginRequest
{
    @FormUrlEncoded
    @POST("/deviceAuth/loginWithEmail")
    LoginResult emailLogin(@Field("email") String email, @Field("password") String password, @Field("name") String name) throws TransientException, PermanentException;
}
