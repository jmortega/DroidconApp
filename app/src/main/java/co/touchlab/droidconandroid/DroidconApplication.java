package co.touchlab.droidconandroid;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import co.touchlab.droidconandroid.data.AppPrefs;
import co.touchlab.droidconandroid.superbus.RefreshScheduleDataKot;
import co.touchlab.droidconandroid.superbus.SeedScheduleDataTask;
import co.touchlab.droidconandroid.tasks.Queues;
import io.fabric.sdk.android.Fabric;

/**
 * Created by kgalligan on 6/28/14.
 */
public class DroidconApplication extends Application
{
    public static final String FIRST_SEED = "FIRST_SEED";

    @Override
    public void onCreate()
    {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        if(AppPrefs.getInstance(this).once(FIRST_SEED))
            Queues.localQueue(this).execute(new SeedScheduleDataTask());

        if(AppPrefs.getInstance(this).isLoggedIn())
            RefreshScheduleDataKot.Companion.callMe(this);
    }
}
