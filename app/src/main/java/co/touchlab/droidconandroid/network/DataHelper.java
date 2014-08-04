package co.touchlab.droidconandroid.network;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
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
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import retrofit.ErrorHandler;
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
    public static RestAdapter makeRequestAdapter(Context context)
    {
        RestAdapter.Builder builder = makeRequestAdapterBuilder(context);
        return builder
                .build();
    }

    public static RestAdapter.Builder makeRequestAdapterBuilder(Context context)
    {
        return makeRequestAdapterBuilder(context, new RetrofitBusErrorHandler());
    }

    @NotNull
    public static RestAdapter.Builder makeRequestAdapterBuilder(Context context, ErrorHandler errorHandler)
    {
        AppPrefs appPrefs = AppPrefs.getInstance(context);
        final String userUuid = appPrefs.getUserUuid();

        RequestInterceptor requestInterceptor = new RequestInterceptor()
        {
            @Override
            public void intercept(RequestFacade request)
            {
                request.addHeader("Accept", "application/json");
                if (!TextUtils.isEmpty(userUuid))
                    request.addHeader("uuid", userUuid);
            }
        };
        Gson gson = new GsonBuilder().create();
        GsonConverter gsonConverter = new GsonConverter(gson);

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setRequestInterceptor(requestInterceptor)
                .setConverter(gsonConverter)
                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("DroidconApp"))
                .setEndpoint(context.getString(R.string.base_url));

        if (errorHandler != null)
            builder.setErrorHandler(errorHandler);

        return builder;
    }
}
