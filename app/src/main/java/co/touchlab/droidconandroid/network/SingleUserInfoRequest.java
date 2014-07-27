package co.touchlab.droidconandroid.network;

import co.touchlab.android.superbus.errorcontrol.PermanentException;
import co.touchlab.android.superbus.errorcontrol.TransientException;
import co.touchlab.droidconandroid.network.dao.UserInfoResponse;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by kgalligan on 7/26/14.
 */
public interface SingleUserInfoRequest
{
    @GET("/dataTest/findUserById/{userId}")
    UserInfoResponse getUserInfo(@Path("userId")Integer userId)throws TransientException, PermanentException;
}
