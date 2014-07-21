package co.touchlab.droidconandroid.tasks;

import android.content.Context;
import co.touchlab.android.threading.tasks.TaskQueue;
import co.touchlab.droidconandroid.data.DatabaseHelper;
import org.jetbrains.annotations.NotNull;


/**
 * Created by kgalligan on 6/28/14.
 */
public abstract class DatabaseTask implements TaskQueue.Task
{
    private final DatabaseHelper databaseHelper;

    protected DatabaseTask(Context context)
    {
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    @NotNull
    protected DatabaseHelper getDatabase()
    {
        return databaseHelper;
    }
}
