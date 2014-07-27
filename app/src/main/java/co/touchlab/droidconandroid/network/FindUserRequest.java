package co.touchlab.droidconandroid.network;

import co.touchlab.android.superbus.errorcontrol.PermanentException;
import co.touchlab.android.superbus.errorcontrol.TransientException;
import co.touchlab.droidconandroid.network.dao.UserInfoResponse;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by kgalligan on 7/26/14.
 */
public interface FindUserRequest
{
    @GET("/dataTest/findUserByCode/{userCode}")
    UserInfoResponse getUserInfo(@Path("userCode") String userCode)throws TransientException, PermanentException;
}
