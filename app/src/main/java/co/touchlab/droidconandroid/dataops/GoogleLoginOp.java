package co.touchlab.droidconandroid.dataops;

import android.content.Context;
import co.touchlab.droidconandroid.network.DataHelper;
import com.google.android.gms.auth.GoogleAuthUtil;

/**
 * Created by kgalligan on 7/5/14.
 */
public class GoogleLoginOp implements DataProcessor.RunnableEx
{
    public static final String GOOGLE_LOGIN_COMPLETE = "GOOGLE_LOGIN_COMPLETE";
    public static final String GOOGLE_LOGIN_UUID = "GOOGLE_LOGIN_UUID";
    private final static String SCOPE = "audience:server:client_id:654878069390-0rs83f4a457ggmlln2jnmedv1b808bkv.apps.googleusercontent.com";

    private Context context;
    private String email;
    private String name;

    public GoogleLoginOp(Context context, String email, String name)
    {
        this.context = context;
        this.email = email;
        this.name = name;
    }

    @Override
    public void run() throws Exception
    {
        String token = GoogleAuthUtil.getToken(context, email, SCOPE);
        DataHelper.loginGoogle(context, token, name);
    }

    @Override
    public boolean handleError(Exception e)
    {
        return false;
    }
}
