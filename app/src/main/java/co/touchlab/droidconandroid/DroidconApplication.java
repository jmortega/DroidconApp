package co.touchlab.droidconandroid;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.crashlytics.android.Crashlytics;

import co.touchlab.droidconandroid.data.AppPrefs;
import co.touchlab.droidconandroid.data.DatabaseHelper;
import co.touchlab.droidconandroid.superbus.RefreshScheduleDataKot;
import co.touchlab.droidconandroid.tasks.persisted.PersistedTaskQueueFactory;

/**
 * Created by kgalligan on 6/28/14.
 */
public class DroidconApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Crashlytics.start(this);
        if(AppPrefs.getInstance(this).isLoggedIn())
            PersistedTaskQueueFactory.getInstance(this).execute(new RefreshScheduleDataKot());
    }
}
