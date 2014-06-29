package co.touchlab.droidconandroid.superbus;

import android.content.Context;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.errorcontrol.PermanentException;
import co.touchlab.android.superbus.errorcontrol.TransientException;
import co.touchlab.droidconandroid.network.DataHelper;

/**
 * Created by kgalligan on 6/28/14.
 */
public class RefreshScheduleData extends Command
{
    @Override
    public String logSummary()
    {
        return RefreshScheduleData.class.getSimpleName();
    }

    @Override
    public boolean same(Command command)
    {
        return command instanceof RefreshScheduleData;
    }

    @Override
    public void callCommand(Context context) throws TransientException, PermanentException
    {
        DataHelper.scheduleData(context);
    }

    @Override
    public void onPermanentError(Context context, PermanentException exception)
    {
        throw new RuntimeException(exception);
    }
}
