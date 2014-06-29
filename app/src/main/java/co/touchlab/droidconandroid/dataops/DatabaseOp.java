package co.touchlab.droidconandroid.dataops;

import android.content.Context;
import co.touchlab.droidconandroid.data.DatabaseHelper;

/**
 * Created by kgalligan on 6/28/14.
 */
public abstract class DatabaseOp implements DataProcessor.RunnableEx
{
    private Context context;

    protected DatabaseOp(Context context)
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
