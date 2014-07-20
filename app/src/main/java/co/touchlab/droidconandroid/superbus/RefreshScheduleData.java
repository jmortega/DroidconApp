package co.touchlab.droidconandroid.superbus;

import android.content.Context;
import co.touchlab.android.superbus.CheckedCommand;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.errorcontrol.PermanentException;
import co.touchlab.android.superbus.errorcontrol.TransientException;
import co.touchlab.droidconandroid.BuildConfig;
import co.touchlab.droidconandroid.data.AppPrefs;
import co.touchlab.droidconandroid.data.DatabaseHelper;
import co.touchlab.droidconandroid.data.Event;
import co.touchlab.droidconandroid.network.BasicIdResult;
import co.touchlab.droidconandroid.network.DataHelper;
import co.touchlab.droidconandroid.network.dao.Convention;
import co.touchlab.droidconandroid.network.dao.Venue;
import com.j256.ormlite.dao.Dao;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by kgalligan on 6/28/14.
 */
public class RefreshScheduleData extends CheckedCommand
{
    interface RefreshScheduleDataRequest
    {
        @GET("/dataTest/scheduleData/{conventionId}")
        Convention getScheduleData(@Path("conventionId")Integer conventionId)throws TransientException, PermanentException;
    }

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
    public void callCommand(final Context context) throws TransientException, PermanentException
    {
        RestAdapter restAdapter = DataHelper.makeRequestAdapter(context);
        RefreshScheduleDataRequest request = restAdapter.create(RefreshScheduleDataRequest.class);
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        Dao<Event, Long> eventDao = databaseHelper.getEventDao();
        Dao<co.touchlab.droidconandroid.data.Venue, Long> venueDao = databaseHelper.getVenueDao();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mma");

        Convention convention = request.getScheduleData(BuildConfig.CONVENTION_ID);
        List<Venue> venues = convention.venues;

        try
        {
            for (Venue venue : venues)
            {
                venueDao.createOrUpdate(venue);
                for (co.touchlab.droidconandroid.network.dao.Event event : venue.events)
                {
                    event.venue = venue;
                    event.startDateLong = dateFormat.parse(event.startDate).getTime();
                    event.endDateLong = dateFormat.parse(event.endDate).getTime();
                    eventDao.createOrUpdate(event);
                }
            }
        }
        catch (SQLException e)
        {
            throw new PermanentException(e);
        }
        catch (ParseException e)
        {
            throw new PermanentException(e);
        }
    }

    @Override
    public boolean handlePermanentError(Context context, PermanentException exception)
    {
        return false;
    }
}
