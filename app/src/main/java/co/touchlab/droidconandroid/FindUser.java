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
import co.touchlab.android.threading.tasks.BsyncTaskManager;
import co.touchlab.droidconandroid.tasks.FindUserTaskKot;
import co.touchlab.droidconandroid.utils.Toaster;
import com.squareup.picasso.Picasso;

public class FindUser extends Activity {

    public static final String HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES = "https://s3.amazonaws.com/droidconimages/";
    private EditText userCode;
    private ImageView userAvatar;
    private TextView userName;
    private BsyncTaskManager bsyncTaskManager;

    public static void callMe(Context c)
    {
        Intent i = new Intent(c, FindUser.class);
        c.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bsyncTaskManager = new BsyncTaskManager(savedInstanceState);
        bsyncTaskManager.register(this);

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
                bsyncTaskManager.post(FindUser.this, new FindUserTaskKot(userCodeVal));
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        bsyncTaskManager.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        bsyncTaskManager.unregister();
    }

    public void showResult(FindUserTaskKot findUserTask)
    {
        if(findUserTask.isError())
        {
            Toaster.showMessage(this, findUserTask.getErrorStringCode());
        }
        else
        {
            if(findUserTask.getUserData().getAvatarKey() != null)
                Picasso.with(this).load(HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES + findUserTask.getUserData().getAvatarKey()).into(userAvatar);
            userName.setText(findUserTask.getUserData().getName());
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
