package co.touchlab.droidconandroid.superbus;

import android.content.Context;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.errorcontrol.PermanentException;
import co.touchlab.android.superbus.errorcontrol.TransientException;
import co.touchlab.android.superbus.http.BusHttpClient;
import co.touchlab.droidconandroid.R;
import co.touchlab.droidconandroid.data.AppPrefs;
import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;

/**
 * Created by kgalligan on 7/5/14.
 */
public class UploadAvatarCommand extends Command
{
    private String imageURL;

    public UploadAvatarCommand(String imageURL)
    {
        this.imageURL = imageURL;
    }

    public UploadAvatarCommand()
    {
    }

    @Override
    public String logSummary()
    {
        return "imageURL: "+ imageURL;
    }

    @Override
    public boolean same(Command command)
    {
        return false;
    }

    @Override
    public void callCommand(Context context) throws TransientException, PermanentException
    {
        BusHttpClient client = new BusHttpClient("");
        HttpResponse response = client.get(imageURL, null);
        byte[] body = response.getBody();
        client.checkAndThrowError();

        String uuid = AppPrefs.getInstance(context).getUserUuid();
        BusHttpClient postClient = new BusHttpClient(context.getString(R.string.base_url));
        postClient.post("dataTest/uploadAvatar/"+ uuid, "image/jpeg", body);
        postClient.checkAndThrowError();
    }
}
