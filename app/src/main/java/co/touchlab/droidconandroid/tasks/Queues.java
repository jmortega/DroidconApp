package co.touchlab.droidconandroid.tasks;
import android.content.Context;

import co.touchlab.android.threading.tasks.TaskQueue;

/**
 * Created by kgalligan on 8/17/15.
 */
public class Queues
{
    public static TaskQueue localQueue(Context context)
    {
        return TaskQueue.loadQueueDefault(context);
    }

    public static TaskQueue networkQueue(Context context)
    {
        return TaskQueue.loadQueue(context, "network");
    }
}
