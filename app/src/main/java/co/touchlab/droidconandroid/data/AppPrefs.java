package co.touchlab.droidconandroid.data;

import android.content.Context;
import android.content.SharedPreferences;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by kgalligan on 6/28/14.
 */
public class AppPrefs
{
    public static final String USER_UUID = "USER_UUID";
    private static AppPrefs instance;

    private SharedPreferences prefs;

    public static synchronized AppPrefs getInstance(Context context)
    {
        if(instance == null)
        {
            instance = new AppPrefs();
            instance.prefs = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        }

        return instance;
    }

    public boolean isLoggedIn()
    {
        return StringUtils.isNoneEmpty(getUserUuid());
    }

    public String getUserUuid()
    {
        return prefs.getString(USER_UUID, null);
    }

    public void setUserUuid(String uuid)
    {
        prefs.edit().putString(USER_UUID, uuid).apply();
    }
}
