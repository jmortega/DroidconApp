package co.touchlab.droidconandroid.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by kgalligan on 6/28/14.
 */
public class AppPrefs
{
    public static final String USER_UUID        = "USER_UUID";
    public static final String USER_ID          = "USER_ID";
    public static final String SEEN_WELCOME     = "seen_welcome";
    public static final String AVATAR_KEY       = "avatar_key";
    public static final String NAME             = "name";
    public static final String EMAIL            = "email";
    public static final String COVER_KEY        = "cover_key";
    public static final String CONVENTION_START = "convention_start";
    public static final String CONVENTION_END = "convention_end";
    public static final String REFRESH_TIME = "refresh_time";
    private static AppPrefs instance;

    private SharedPreferences prefs;

    @NotNull
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
        return getString(USER_UUID, null);
    }

    public void setUserUuid(String uuid)
    {
        setString(USER_UUID, uuid);
    }

    public Long getUserId()
    {
        long id = getLong(USER_ID, - 1l);
        return id == -1 ? null : id;
    }

    public void setUserId(Long id)
    {
        setLong(USER_ID, id);
    }

    public boolean getHasSeenWelcome()
    {
        return getBoolean(SEEN_WELCOME, false);
    }

    public void setHasSeenWelcome()
    {
        setBoolean(SEEN_WELCOME, true);
    }

    public void setAvatarKey(String key)
    {
        setString(AVATAR_KEY, key);
    }

    public String getAvatarKey()
    {
        return getString(AVATAR_KEY, null);
    }

    public void setName(String name)
    {
        setString(NAME, name);
    }

    public String getName()
    {
        return getString(NAME, null);
    }

    public void setEmail(String key)
    {
        setString(EMAIL, key);
    }

    public String getEmail()
    {
        return getString(EMAIL, null);
    }

    public void setCoverKey(String key)
    {
        setString(COVER_KEY, key);
    }

    public String getCoverKey()
    {
        return getString(COVER_KEY, null);
    }

    public void setConventionStartDate(@NotNull String startDate)
    {
        setString(CONVENTION_START, startDate);
    }

    public String getConventionStartDate()
    {
        return getString(CONVENTION_START, null);
    }

    public void setConventionEndDate(@NotNull String endDate)
    {
        setString(CONVENTION_END, endDate);
    }

    public String getConventionEndDate()
    {
        return getString(CONVENTION_END, null);
    }

    public void setRefreshTime(@NotNull Long time)
    {
        setLong(REFRESH_TIME, time);
    }

    public Long getRefreshTime()
    {
        return getLong(REFRESH_TIME, -1l);
    }

    //helper methods
    private void setBoolean(String key, Boolean value)
    {
        prefs.edit().putBoolean(key, value).apply();
    }

    private Boolean getBoolean(String key, Boolean defaultVal)
    {
        return prefs.getBoolean(key, defaultVal);
    }

    private void setString(String key, String value)
    {
        prefs.edit().putString(key, value).apply();
    }

    private String getString(String key, String defaultVal)
    {
        return prefs.getString(key, defaultVal);
    }

    private void setInt(String key, Integer value)
    {
        prefs.edit().putInt(key, value).apply();
    }

    private Integer getInt(String key, Integer defaultVal)
    {
        return prefs.getInt(key, defaultVal);
    }

    private void setLong(String key, Long value)
    {
        prefs.edit().putLong(key, value).apply();
    }

    private Long getLong(String key, Long defaultVal)
    {
        return prefs.getLong(key, defaultVal);
    }
}
