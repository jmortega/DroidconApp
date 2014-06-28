package co.touchlab.droidconandroid.network;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import co.touchlab.android.superbus.errorcontrol.PermanentException;
import co.touchlab.android.superbus.errorcontrol.TransientException;
import co.touchlab.android.superbus.http.BusHttpClient;
import co.touchlab.droidconandroid.BuildConfig;
import co.touchlab.droidconandroid.R;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import de.greenrobot.event.EventBus;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by kgalligan on 6/28/14.
 */
public class DataHelper
{
    public static final String CT_APPLICATION_JSON = "application/json";

    public static void scheduleData(Context context) throws PermanentException, TransientException
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
            void jsonStringReply(String jsonData) throws JSONException
            {
                Log.w("json", jsonData);
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
