package co.touchlab.droidconandroid;

import android.database.sqlite.SQLiteDatabase;

import com.crashlytics.android.Crashlytics;

import co.touchlab.android.superbus.SuperbusConfig;
import co.touchlab.android.superbus.appsupport.AbstractCommandPersistedApplication;
import co.touchlab.android.superbus.appsupport.CommandBusHelper;
import co.touchlab.android.superbus.errorcontrol.ConfigException;
import co.touchlab.android.superbus.network.ConnectionChangeBusEventListener;
import co.touchlab.droidconandroid.data.AppPrefs;
import co.touchlab.droidconandroid.data.DatabaseHelper;
import co.touchlab.droidconandroid.superbus.RefreshScheduleDataKot;

/**
 * Created by kgalligan on 6/28/14.
 */
public class DroidconApplication extends AbstractCommandPersistedApplication
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Crashlytics.start(this);
        DatabaseHelper.getInstance(this);
        if(AppPrefs.getInstance(this).isLoggedIn())
            CommandBusHelper.submitCommandAsync(this, new RefreshScheduleDataKot());
    }

    @Override
    protected void buildConfig(SuperbusConfig.Builder builder) throws ConfigException
    {
        builder.addEventListener(new ConnectionChangeBusEventListener());
    }

    @Override
    protected SQLiteDatabase getWritableDatabase()
    {
        return DatabaseHelper.getInstance(this).getWritableDatabase();
    }

}
