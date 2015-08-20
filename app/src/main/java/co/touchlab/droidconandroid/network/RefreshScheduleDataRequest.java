package co.touchlab.droidconandroid.network;

import co.touchlab.droidconandroid.network.dao.Convention;
import co.touchlab.droidconandroid.network.dao.MyRsvpResponse;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by kgalligan on 7/20/14.
 */
public interface RefreshScheduleDataRequest
{
    @GET("/dataTest/scheduleData/{conventionId}")
    Convention getScheduleData(@Path("conventionId")Integer conventionId) throws Exception;
}
