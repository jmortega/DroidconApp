package co.touchlab.droidconandroid

import android.app.Activity
import com.google.android.gms.common.api.GoogleApiClient
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.touchlab.droidconandroid.tasks.GoogleLoginTask
import android.widget.EditText
import android.os.Bundle
import android.view.View
import com.google.android.gms.plus.Plus
import co.touchlab.droidconandroid.data.AppPrefs
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import co.touchlab.android.threading.tasks.TaskQueue
import com.google.android.gms.common.ConnectionResult
import android.content.IntentSender
import co.touchlab.droidconandroid.utils.Toaster
import android.view.MenuItem
import android.view.Menu

/**
 * Created by kgalligan on 7/21/14.
 */
class EnterUuidActivity : Activity()
{
    var mGoogleApiClient: GoogleApiClient? = null
    var uuidEntry: EditText? = null

    class object
    {
        val REQUEST_CODE_RESOLVE_ERR = 9000
        public fun startMe(c : Context)
        {
            val i = Intent(c, javaClass<EnterUuidActivity>())
            c.startActivity(i)
        }
    }

    val uuidReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            uuidEntry!!.setText(intent.getStringExtra(GoogleLoginTask.GOOGLE_LOGIN_UUID))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug_enter_uuid)
        uuidEntry = findView(R.id.uuidEntry) as EditText
        findView(R.id.go).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                AppPrefs.getInstance(this@EnterUuidActivity).setUserUuid(uuidEntry!!.getText().toString())
                MyActivity.startMe(this@EnterUuidActivity)
                finish()
            }
        })

        findView(R.id.callGoogle).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                forceGoogleConnect()
            }
        })

        mGoogleApiClient = GoogleApiClient.Builder(this).addConnectionCallbacks(ConnectionCallbacksImpl())!!.addOnConnectionFailedListener(OnConnectionFailedListenerImpl())!!.addApi(Plus.API)!!.addScope(Plus.SCOPE_PLUS_LOGIN)!!.build()

        LocalBroadcastManager.getInstance(this)!!.registerReceiver(uuidReceiver, IntentFilter(GoogleLoginTask.GOOGLE_LOGIN_COMPLETE))
    }

    override fun onDestroy()
    {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this)!!.unregisterReceiver(uuidReceiver)
    }

    override fun onStop()
    {
        super.onStop()
        googleDisconnectIfConnected()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_RESOLVE_ERR)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                mGoogleApiClient!!.connect()
            }
        }
    }

    private fun googleDisconnectIfConnected()
    {
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected())
            mGoogleApiClient?.disconnect()
    }

    public fun forceGoogleConnect()
    {
        googleDisconnectIfConnected()
        mGoogleApiClient!!.connect()
    }

    public inner class ConnectionCallbacksImpl() : GoogleApiClient.ConnectionCallbacks
    {
        override fun onConnected(bundle: Bundle?)
        {
            val accountName = Plus.AccountApi.getAccountName(mGoogleApiClient)
            val person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient)
            var imageURL: String? = null
            if (person != null && person.hasImage())
            {
                val image = person.getImage()

                if (image != null && image.hasUrl())
                {
                    imageURL = image.getUrl()
                }
            }

            TaskQueue.execute(this@EnterUuidActivity, GoogleLoginTask(accountName!!, person?.getDisplayName(), imageURL))
        }

        override fun onConnectionSuspended(i: Int)
        {
        }
    }

    public inner class OnConnectionFailedListenerImpl() : GoogleApiClient.OnConnectionFailedListener
    {
        override fun onConnectionFailed(result: ConnectionResult?)
        {
            if (result != null && result.hasResolution())
            {
                try
                {
                    result.startResolutionForResult(this@EnterUuidActivity, REQUEST_CODE_RESOLVE_ERR)
                }
                catch (e: IntentSender.SendIntentException)
                {
                    mGoogleApiClient!!.connect()
                }

            }
            else
            {
                Toaster.showMessage(this@EnterUuidActivity, R.string.google_error)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.debug_enter_uuid, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item?.getItemId()
        if (id == R.id.action_settings)
        {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}