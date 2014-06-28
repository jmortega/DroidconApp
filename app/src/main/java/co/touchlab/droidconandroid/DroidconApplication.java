package co.touchlab.droidconandroid;

import android.app.Application;
import co.touchlab.droidconandroid.data.DatabaseHelper;

/**
 * Created by kgalligan on 6/28/14.
 */
public class DroidconApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        DatabaseHelper.getInstance(this);
    }
}
