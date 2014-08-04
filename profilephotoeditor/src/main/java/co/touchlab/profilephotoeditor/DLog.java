package co.touchlab.profilephotoeditor;

import android.util.Log;

/**
 * Created by kgalligan on 8/3/14.
 */
public class DLog
{
    public static void i(String s)
    {
        Log.i("DLog", s);
    }
    public static void e(String s, Throwable e)
    {
        Log.e("DLog", s, e);
    }

}
