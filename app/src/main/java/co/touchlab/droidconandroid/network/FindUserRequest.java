package co.touchlab.droidconandroid.network;

import co.touchlab.droidconandroid.network.dao.UserInfoResponse;
import co.touchlab.droidconandroid.network.dao.UserSearchResponse;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by kgalligan on 7/26/14.
 */
public interface FindUserRequest
{
    @GET("/dataTest/findUserByCode/{userCode}")
    UserInfoResponse getUserInfo(@Path("userCode") String userCode);

    @GET("/dataTest/findUserBySearch/{search}")
    UserSearchResponse searchUsers(@Path("search") String search);
}
