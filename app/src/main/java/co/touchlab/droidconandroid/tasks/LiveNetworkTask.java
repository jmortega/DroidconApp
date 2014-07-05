package co.touchlab.droidconandroid.tasks;

import co.touchlab.android.threading.tasks.TaskQueue;

/**
 * Created by kgalligan on 7/5/14.
 */
public abstract class LiveNetworkTask implements TaskQueue.Task
{
    public Integer errorStringCode;

    public boolean isError()
    {
        return errorStringCode != null;
    }


}
