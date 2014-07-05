package co.touchlab.droidconandroid.tasks;

import android.content.Context;
import co.touchlab.android.threading.tasks.TaskQueue;
import co.touchlab.droidconandroid.data.DatabaseHelper;

/**
 * Created by kgalligan on 6/28/14.
 */
public abstract class DatabaseTask implements TaskQueue.Task
{
    private Context context;

    protected DatabaseTask(Context context)
    {
        this.context = context;
    }

    protected DatabaseHelper getDatabase()
    {
        return DatabaseHelper.getInstance(context);
    }

    public Context getContext()
    {
        return context;
    }
}
