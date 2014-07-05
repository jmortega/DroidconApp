package co.touchlab.droidconandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import co.touchlab.android.threading.tasks.TaskQueue;
import co.touchlab.droidconandroid.tasks.FindUserTask;
import com.github.johnpersano.supertoasts.SuperToast;
import com.squareup.picasso.Picasso;
import de.greenrobot.event.EventBus;

public class FindUser extends Activity {

    public static final String HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES = "https://s3.amazonaws.com/droidconimages/";
    private EditText userCode;
    private ImageView userAvatar;
    private TextView userName;

    public static void callMe(Context c)
    {
        Intent i = new Intent(c, FindUser.class);
        c.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        userCode = (EditText) findViewById(R.id.userCode);
        userAvatar = (ImageView) findViewById(R.id.userAvatar);
        userName = (TextView) findViewById(R.id.userName);
        findViewById(R.id.findUser).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String userCodeVal = userCode.getText().toString();
                TaskQueue.runProcess(new FindUserTask(FindUser.this, userCodeVal));
            }
        });

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(FindUserTask findUserTask)
    {
        if(findUserTask.isError())
        {
            SuperToast.create(this, getString(findUserTask.errorStringCode), Toast.LENGTH_LONG).show();
        }
        else
        {
            if(findUserTask.userData.avatarKey != null)
                Picasso.with(this).load(HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES + findUserTask.userData.avatarKey).into(userAvatar);
            userName.setText(findUserTask.userData.name);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
