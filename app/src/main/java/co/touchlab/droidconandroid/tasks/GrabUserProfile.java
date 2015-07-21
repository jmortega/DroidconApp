package co.touchlab.droidconandroid.tasks;

import android.app.Activity;
import android.content.Context;

import co.touchlab.android.threading.eventbus.EventBusExt;
import co.touchlab.android.threading.tasks.Task;
import co.touchlab.droidconandroid.data.DatabaseHelper;
import co.touchlab.droidconandroid.data.UserAccount;

/**
 * Created by kgalligan on 8/3/14.
 */
public class GrabUserProfile extends Task
{
    private Long userId;
    public UserAccount userAccount;

    public GrabUserProfile(Long userId)
    {
        this.userId = userId;
    }

    @Override
    protected void onComplete(Context context)
    {
        EventBusExt.getDefault().post(this);
    }

    @Override
    protected void run(Context context) throws Throwable
    {
        DatabaseHelper instance = DatabaseHelper.getInstance(context);
        userAccount = instance.getUserAccountDao().queryForId(userId);
    }

    @Override
    protected boolean handleError(Context context, Throwable e)
    {
        return false;
    }
}
