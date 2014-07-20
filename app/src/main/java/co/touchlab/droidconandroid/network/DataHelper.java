package co.touchlab.droidconandroid.network;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import co.touchlab.android.superbus.errorcontrol.PermanentException;
import co.touchlab.android.superbus.errorcontrol.TransientException;
import co.touchlab.android.superbus.http.BusHttpClient;
import co.touchlab.android.superbus.http.RetrofitBusErrorHandler;
import co.touchlab.droidconandroid.BuildConfig;
import co.touchlab.droidconandroid.R;
import co.touchlab.droidconandroid.data.AppPrefs;
import co.touchlab.droidconandroid.tasks.GoogleLoginTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by kgalligan on 6/28/14.
 */
public class DataHelper
{
    public static final String CT_APPLICATION_JSON = "application/json";

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
                    Intent intent = new Intent(GoogleLoginTask.GOOGLE_LOGIN_COMPLETE);
                    intent.putExtra(GoogleLoginTask.GOOGLE_LOGIN_UUID, uuid);
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

    public static RestAdapter makeRequestAdapter(Context context)
    {
        RestAdapter.Builder builder = makeRequestAdapterBuilder(context);
        return builder
                .build();
    }

    public static RestAdapter.Builder makeRequestAdapterBuilder(Context context)
    {
        RequestInterceptor requestInterceptor = new RequestInterceptor()
        {
            @Override
            public void intercept(RequestFacade request)
            {
                request.addHeader("Accept", "application/json");
            }
        };
        Gson gson = new GsonBuilder().create();
        GsonConverter gsonConverter = new GsonConverter(gson);

        return new RestAdapter.Builder()
                .setErrorHandler(new RetrofitBusErrorHandler())
                .setRequestInterceptor(requestInterceptor)
                .setConverter(gsonConverter)
                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("DroidconApp"))
                .setEndpoint(context.getString(R.string.base_url));
    }
}
