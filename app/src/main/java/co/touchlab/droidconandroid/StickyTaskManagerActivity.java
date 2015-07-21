package co.touchlab.droidconandroid;

import android.app.Activity;
import android.os.Bundle;
import co.touchlab.android.threading.tasks.sticky.StickyTaskManager;

/**
 * Created by kgalligan on 8/3/14.
 */
public class StickyTaskManagerActivity extends Activity
{
    protected StickyTaskManager stickyTaskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        stickyTaskManager = new StickyTaskManager(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        stickyTaskManager.onSaveInstanceState(outState);
    }
}
