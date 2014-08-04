package co.touchlab.droidconandroid.tasks;

import android.app.Activity;
import android.content.Context;
import co.touchlab.android.threading.tasks.BsyncTask;
import co.touchlab.droidconandroid.data.DatabaseHelper;
import co.touchlab.droidconandroid.data.UserAccount;

/**
 * Created by kgalligan on 8/3/14.
 */
public class GrabUserProfile extends BsyncTask<Activity>
{
    private Long userId;
    private UserAccount userAccount;

    public interface UserProfileUpdate
    {
        void profile(UserAccount ua);
    }

    public GrabUserProfile(Long userId)
    {
        this.userId = userId;
    }

    @Override
    protected void doInBackground(Context context) throws Exception
    {
        DatabaseHelper instance = DatabaseHelper.getInstance(context);
        userAccount = instance.getUserAccountDao().queryForId(userId);
    }

    @Override
    protected void onPostExecute(Activity host)
    {
        ((UserProfileUpdate)host).profile(userAccount);
    }

    @Override
    public boolean handleError(Exception e)
    {
        return false;
    }
}
