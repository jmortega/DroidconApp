package co.touchlab.droidconandroid;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import co.touchlab.droidconandroid.R;
import co.touchlab.droidconandroid.data.AppPrefs;
import co.touchlab.droidconandroid.dataops.DataProcessor;
import co.touchlab.droidconandroid.dataops.GoogleLoginOp;
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;
import java.util.Random;

public class DebugEnterUuidActivity extends Activity
{

    private GoogleApiClient mGoogleApiClient;
    public static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private BroadcastReceiver uuidReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            uuidEntry.setText(intent.getStringExtra(GoogleLoginOp.GOOGLE_LOGIN_UUID));
        }
    };

    private EditText uuidEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_enter_uuid);
        uuidEntry = (EditText) findViewById(R.id.uuidEntry);
        findViewById(R.id.go).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AppPrefs.getInstance(DebugEnterUuidActivity.this).setUserUuid(uuidEntry.getText().toString());
                finish();
            }
        });

        findViewById(R.id.callGoogle).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                forceGoogleConnect();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new ConnectionCallbacksImpl())
                .addOnConnectionFailedListener(new OnConnectionFailedListenerImpl())
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        LocalBroadcastManager.getInstance(this).registerReceiver(uuidReceiver, new IntentFilter(GoogleLoginOp.GOOGLE_LOGIN_COMPLETE));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(uuidReceiver);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        googleDisconnectIfConnected();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLVE_ERR)
        {
            if (resultCode == RESULT_OK)
            {
                mGoogleApiClient.connect();
            }
        }
    }

    private void googleDisconnectIfConnected()
    {
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    public void forceGoogleConnect()
    {
        googleDisconnectIfConnected();
        mGoogleApiClient.connect();
    }

    public class ConnectionCallbacksImpl implements GoogleApiClient.ConnectionCallbacks
    {
        @Override
        public void onConnected(Bundle bundle)
        {
            String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

            DataProcessor.runProcess(new GoogleLoginOp(DebugEnterUuidActivity.this, accountName, person.getDisplayName()));
        }

        @Override
        public void onConnectionSuspended(int i)
        {

        }
    }

    public class OnConnectionFailedListenerImpl implements GoogleApiClient.OnConnectionFailedListener
    {
        @Override
        public void onConnectionFailed(ConnectionResult result)
        {
            if (result.hasResolution())
            {
                try
                {
                    result.startResolutionForResult(DebugEnterUuidActivity.this, REQUEST_CODE_RESOLVE_ERR);
                }
                catch (IntentSender.SendIntentException e)
                {
                    mGoogleApiClient.connect();
                }
            }
            else
            {
                SuperToast.create(DebugEnterUuidActivity.this, getString(R.string.google_error), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.debug_enter_uuid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void callMe(Context c)
    {
        Intent i = new Intent(c, DebugEnterUuidActivity.class);
        c.startActivity(i);
    }
}
