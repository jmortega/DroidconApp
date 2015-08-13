package co.touchlab.droidconandroid.network;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import co.touchlab.droidconandroid.BuildConfig;
import co.touchlab.droidconandroid.R;
import co.touchlab.droidconandroid.data.AppPrefs;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;

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
        return makeRequestAdapterBuilder(context, null);
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
                .setEndpoint(BuildConfig.BASE_URL);

        if (errorHandler != null)
            builder.setErrorHandler(errorHandler);

        return builder;
    }
}
