package co.touchlab.droidconandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import co.touchlab.android.threading.tasks.TaskQueue;
import co.touchlab.droidconandroid.data.AppPrefs;
import co.touchlab.droidconandroid.data.UserAccount;
import co.touchlab.droidconandroid.tasks.GrabUserProfile;
import co.touchlab.droidconandroid.tasks.UpdateUserProfileTask;

public class EditUserProfile extends BsyncActivity implements GrabUserProfile.UserProfileUpdate
{

    private EditText myName;
    private EditText myProfile;
    private TextView myUserCode;
    private EditText myCompany;
    private EditText myTwitter;
    private EditText myWebsite;

    public static void callMe(Context c)
    {
        Intent i = new Intent(c, EditUserProfile.class);
        c.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        AppPrefs appPrefs = AppPrefs.getInstance(this);

        if(savedInstanceState == null)
            bsyncTaskManager.post(this, new GrabUserProfile(appPrefs.getUserId()));

        myName = (EditText) findViewById(R.id.myName);
        myProfile = (EditText) findViewById(R.id.myProfile);
        myUserCode = (TextView) findViewById(R.id.myUserCode);
        myCompany = (EditText) findViewById(R.id.myCompany);
        myTwitter = (EditText) findViewById(R.id.myTwitter);
        myWebsite = (EditText) findViewById(R.id.myWebsite);
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveProfile();
            }
        });
    }

    private void saveProfile()
    {
        TaskQueue.execute(this, new UpdateUserProfileTask(
                this,
                myName.getText().toString(),
                myProfile.getText().toString(),
                myCompany.getText().toString(),
                myTwitter.getText().toString(),
                null,
                myWebsite.getText().toString()
        ));
        finish();
    }

    @Override
    public void profile(UserAccount ua)
    {
        myName.setText(ua.name);
        myProfile.setText(ua.profile);
        myCompany.setText(ua.company);
        myTwitter.setText(ua.twitter);
        myWebsite.setText(ua.website);
    }
}
