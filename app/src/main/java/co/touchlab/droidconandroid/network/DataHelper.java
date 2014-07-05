package co.touchlab.droidconandroid.network;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import co.touchlab.android.superbus.errorcontrol.PermanentException;
import co.touchlab.android.superbus.errorcontrol.TransientException;
import co.touchlab.android.superbus.http.BusHttpClient;
import co.touchlab.droidconandroid.BuildConfig;
import co.touchlab.droidconandroid.R;
import co.touchlab.droidconandroid.data.AppPrefs;
import co.touchlab.droidconandroid.data.DatabaseHelper;
import co.touchlab.droidconandroid.data.Event;
import co.touchlab.droidconandroid.data.Venue;
import co.touchlab.droidconandroid.tasks.GoogleLoginOpTask;
import com.j256.ormlite.dao.Dao;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by kgalligan on 6/28/14.
 */
public class DataHelper
{
    public static final String CT_APPLICATION_JSON = "application/json";

    public static void scheduleData(final Context context) throws PermanentException, TransientException
    {
        runRemoteCall(context, new RunOp()
        {
            @Override
            public String path()
            {
                return "dataTest/scheduleData/" + BuildConfig.CONVENTION_ID;
            }

            @Override
            public void fillParams(ParameterMap parameterMap)
            {

            }

            @Override
            public HttpResponse buildAndExecuteResponse(BusHttpClient httpClient, ParameterMap params)
            {
                return httpClient.get(path(), params);
            }

            @Override
            void jsonReply(JSONObject json) throws JSONException
            {
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
                Dao<Event, Long> eventDao = databaseHelper.getEventDao();
                Dao<Venue, Long> venueDao = databaseHelper.getVenueDao();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mma");

                try
                {
                    JSONArray venuesArray = json.getJSONArray("venues");
                    for(int i=0; i<venuesArray.length(); i++)
                    {
                        JSONObject venueNode = venuesArray.getJSONObject(i);
                        long venueId = venueNode.getLong("id");
                        Venue venue = venueDao.queryForId(venueId);
                        if(venue == null)
                        {
                            venue = new Venue();
                            venue.id = venueId;
                        }

                        venue.name = venueNode.getString("name");
                        venue.description = venueNode.getString("description");
                        venue.mapImageUrl = venueNode.getString("mapImageUrl");
                        venueDao.createOrUpdate(venue);

                        JSONArray eventsArray = venueNode.getJSONArray("events");

                        for(int j=0; j<eventsArray.length(); j++)
                        {
                            JSONObject eventNode = eventsArray.getJSONObject(j);
                            long eventId = eventNode.getLong("id");
                            Event event = eventDao.queryForId(eventId);
                            if(event == null)
                            {
                                event = new Event();
                                event.id = eventId;
                            }

                            event.name = eventNode.getString("name");
                            event.description = eventNode.getString("description");
                            event.publicEvent = eventNode.getBoolean("publicEvent");
                            event.rsvpLimit = eventNode.getInt("rsvpLimit");
                            event.rsvpCount = eventNode.getInt("rsvpCount");
                            event.startDate = dateFormat.parse(eventNode.getString("startDate")).getTime();
                            event.endDate = dateFormat.parse(eventNode.getString("endDate")).getTime();
                            event.venue = venue;

                            eventDao.createOrUpdate(event);
                        }
                    }
                }
                catch (SQLException e)
                {
                    throw new RuntimeException(e);
                }
                catch (ParseException e)
                {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    public static void addRsvp(final Context context, final Long eventId, final String rsvpUuid) throws TransientException, PermanentException
    {
        runRemoteCall(context, new RunOp()
        {
            @Override
            String path()
            {
                return "dataTest/rsvpEvent/"+ eventId;
            }

            @Override
            HttpResponse buildAndExecuteResponse(BusHttpClient httpClient, ParameterMap params)
            {
                params.put("uuid", AppPrefs.getInstance(context).getUserUuid());
                params.put("rsvpUuid", rsvpUuid);
                return httpClient.post(path(), params);
            }
        });
    }

    public static void loginGoogle(final Context context, final String token, final String name) throws TransientException, PermanentException
        {
            runRemoteCall(context, new RunOp()
            {
                @Override
                String path()
                {
                    return "deviceAuth/loginUser";
                }

                @Override
                HttpResponse buildAndExecuteResponse(BusHttpClient httpClient, ParameterMap params)
                {
                    params.put("googleToken", token);
                    params.put("name", name);
                    return httpClient.post(path(), params);
                }

                @Override
                void jsonReply(JSONObject json) throws JSONException
                {
                    String uuid = json.getString("uuid");
                    AppPrefs.getInstance(context).setUserUuid(uuid);
                    Intent intent = new Intent(GoogleLoginOpTask.GOOGLE_LOGIN_COMPLETE);
                    intent.putExtra(GoogleLoginOpTask.GOOGLE_LOGIN_UUID, uuid);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });
        }

    private static void runRemoteCall(Context context, RunOp op) throws PermanentException, TransientException
    {
        Exception failedJson = null;

        String callType = op.getDebugName();

        BusHttpClient httpClient = op.getClient(context);

        httpClient.setConnectionTimeout(10000);
        ParameterMap parameterMap = httpClient.newParams();
        op.fillParams(parameterMap);

        httpClient.addHeader("Accept", CT_APPLICATION_JSON);
        HttpResponse httpResponse = op.buildAndExecuteResponse(httpClient, parameterMap);

        try
        {
            if (httpResponse == null)
                throw new TransientException("No service/server not running");

            httpClient.checkAndThrowError();

            byte[] body = httpResponse.getBody();

            if (BuildConfig.DEBUG && body != null && body.length > 0)
                writeResponseDebug(callType, body);

            String jsonData = new String(body);
            op.jsonStringReply(jsonData);

            return;
        }
        catch (IOException e)
        {
            failedJson = e;
        }
        catch (JSONException e)
        {
            failedJson = e;
        }
        catch (ClassCastException e)
        {
            failedJson = e;
        }

        throw new PermanentException(failedJson);
    }

    static abstract class RunOp
    {
        abstract String path();

        void fillParams(ParameterMap params)
        {
        }

        BusHttpClient getClient(Context context)
        {
            return new BusHttpClient(context.getString(R.string.base_url));
        }

        abstract HttpResponse buildAndExecuteResponse(BusHttpClient httpClient, ParameterMap params);

        void jsonStringReply(String jsonData) throws JSONException
        {
            final JSONObject json = (JSONObject) new JSONTokener(jsonData).nextValue();
            jsonReply(jsonData, json);
            jsonReply(json);
        }

        void jsonReply(String jsonData, JSONObject json) throws JSONException
        {
        }

        void jsonReply(JSONObject json) throws JSONException
        {
        }

        String getDebugName()
        {
            return path().replace('/', '_');
        }
    }


    private static File writeResponseDebug(String callType, byte[] body) throws IOException
    {
        File debugDir = new File(Environment.getExternalStorageDirectory(), "minibar_debug");
        debugDir.mkdirs();
        File debugFile = new File(debugDir, callType + "_" + System.currentTimeMillis());

        FileOutputStream outStream = new FileOutputStream(debugFile);

        outStream.write(body);
        outStream.close();

        return debugFile;
    }
}
