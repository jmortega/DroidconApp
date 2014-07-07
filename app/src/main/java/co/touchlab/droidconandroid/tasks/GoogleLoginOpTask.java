package co.touchlab.droidconandroid.tasks;

import android.content.Context;
import android.text.TextUtils;
import co.touchlab.android.superbus.appsupport.CommandBusHelper;
import co.touchlab.android.threading.tasks.TaskQueue;
import co.touchlab.droidconandroid.network.DataHelper;
import co.touchlab.droidconandroid.superbus.UploadAvatarCommand;
import com.google.android.gms.auth.GoogleAuthUtil;

/**
 * Created by kgalligan on 7/5/14.
 */
public class GoogleLoginOpTask implements TaskQueue.Task
{
    public static final String GOOGLE_LOGIN_COMPLETE = "GOOGLE_LOGIN_COMPLETE";
    public static final String GOOGLE_LOGIN_UUID = "GOOGLE_LOGIN_UUID";
    private final static String SCOPE = "audience:server:client_id:654878069390-0rs83f4a457ggmlln2jnmedv1b808bkv.apps.googleusercontent.com";

    private String email;
    private String name;
    private String imageURL;

    public GoogleLoginOpTask(String email, String name, String imageURL)
    {
        this.email = email;
        this.name = name;
        this.imageURL = imageURL;
    }

    @Override
    public void run(Context context) throws Exception
    {
        String token = GoogleAuthUtil.getToken(context, email, SCOPE);
        DataHelper.loginGoogle(context, token, name);
        if(!TextUtils.isEmpty(imageURL))
            CommandBusHelper.submitCommandSync(context, new UploadAvatarCommand(imageURL));
    }

    @Override
    public boolean handleError(Exception e)
    {
        return false;
    }
}
