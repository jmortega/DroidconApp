package co.touchlab.droidconandroid;

import android.app.Activity;
import android.os.Bundle;
import co.touchlab.android.threading.tasks.BsyncTaskManager;

/**
 * Created by kgalligan on 8/3/14.
 */
public class BsyncActivity extends Activity
{
    protected BsyncTaskManager<Activity> bsyncTaskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bsyncTaskManager = new BsyncTaskManager<Activity>(savedInstanceState);
        bsyncTaskManager.register(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        bsyncTaskManager.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        bsyncTaskManager.unregister();
    }
}
