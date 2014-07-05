package co.touchlab.droidconandroid.tasks;

import android.content.Context;
import co.touchlab.android.threading.tasks.TaskQueue;
import co.touchlab.droidconandroid.R;
import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import de.greenrobot.event.EventBus;
import org.json.JSONObject;

/**
 * Created by kgalligan on 7/5/14.
 */
public class FindUserTask extends LiveNetworkTask
{
    private Context context;
    private String code;
    public UserData userData;

    public FindUserTask(Context context, String code)
    {
        this.context = context;
        this.code = code;
    }

    @Override
    public void run() throws Exception
    {
        BasicHttpClient client = new BasicHttpClient(context.getString(R.string.base_url));
        HttpResponse httpResponse = client.get("dataTest/findUserByCode/" + code, null);
        if(httpResponse.getStatus() == 404)
        {
            errorStringCode = R.string.error_user_not_found;
        }
        else
        {
            JSONObject json = new JSONObject(httpResponse.getBodyAsString());
            UserData userData = new UserData();
            userData.id = json.getLong("id");
            userData.name = json.getString("name");
            userData.avatarKey = json.getString("avatarKey");
            userData.userCode = json.getString("userCode");
            this.userData = userData;
        }

        EventBus.getDefault().post(this);
    }

    @Override
    public boolean handleError(Exception e)
    {
        return false;
    }

    public static class UserData
    {
        public Long id;
        public String name;
        public String avatarKey;
        public String userCode;
    }
}
