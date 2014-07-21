package co.touchlab.droidconandroid.network;

import co.touchlab.android.superbus.errorcontrol.PermanentException;
import co.touchlab.android.superbus.errorcontrol.TransientException;
import co.touchlab.droidconandroid.network.dao.Convention;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by kgalligan on 7/20/14.
 */
public interface RefreshScheduleDataRequest
{
    @GET("/dataTest/scheduleData/{conventionId}")
    Convention getScheduleData(@Path("conventionId")Integer conventionId)throws TransientException, PermanentException;
}
