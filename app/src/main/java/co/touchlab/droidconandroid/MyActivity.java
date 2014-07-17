package co.touchlab.droidconandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import co.touchlab.droidconandroid.data.AppPrefs;


public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!AppPrefs.getInstance(this).isLoggedIn())
        {
            DebugEnterUuidActivity.callMe(MyActivity.this);
            finish();
        }

        setContentView(R.layout.activity_my);

        findViewById(R.id.showSchedule).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DebugScheduleDisplayActivity.callMe(MyActivity.this);
            }
        });
        findViewById(R.id.goFindUser).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FindUser.callMe(MyActivity.this);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
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

    public static void callMe(Context c)
    {
        Intent i = new Intent(c, MyActivity.class);
        c.startActivity(i);
    }
}
