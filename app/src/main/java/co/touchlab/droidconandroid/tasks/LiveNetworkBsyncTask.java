package co.touchlab.droidconandroid.tasks;

import android.app.Activity;
import co.touchlab.android.threading.tasks.BsyncTask;

/**
 * Created by kgalligan on 7/6/14.
 */
public abstract class LiveNetworkBsyncTask extends BsyncTask<Activity>
{
    public Integer errorStringCode;

    public boolean isError()
    {
        return errorStringCode != null;
    }
}
