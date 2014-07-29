package co.touchlab.droidconandroid

import android.app.Activity
import com.google.android.gms.common.api.GoogleApiClient
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.touchlab.droidconandroid.tasks.GoogleLoginTask
import android.os.Bundle
import android.view.View
import com.google.android.gms.plus.Plus
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import co.touchlab.android.threading.tasks.TaskQueue
import com.google.android.gms.common.ConnectionResult
import android.content.IntentSender
import co.touchlab.droidconandroid.utils.Toaster

/**
 * Created by kgalligan on 7/21/14.
 */
class EnterUuidActivity : FractivityAdapterActivity()
{

    class object
    {
        val REQUEST_CODE_RESOLVE_ERR = 9000
        public fun startMe(c: Context)
        {
            val i = Intent(c, javaClass<EnterUuidActivity>())
            c.startActivity(i)
        }
    }

    override fun createAdapter(savedInstanceState: Bundle?): FractivityAdapter
    {
        return EnterUuidAdapter(this, savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_RESOLVE_ERR)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                (adapter as EnterUuidAdapter)!!.googleClientConnect()
            }
        }
    }
}

class EnterUuidAdapter(c: Activity, savedInstanceState: Bundle?) : FractivityAdapter(c, savedInstanceState)
{
    val mGoogleApiClient: GoogleApiClient
    val uuidReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            c.finish()
            MyActivity.startMe(c)
        }
    }

    {
        c.setContentView(R.layout.activity_debug_enter_uuid)
        mGoogleApiClient = GoogleApiClient.Builder(c).addConnectionCallbacks(ConnectionCallbacksImpl())!!.addOnConnectionFailedListener(OnConnectionFailedListenerImpl())!!.addApi(Plus.API)!!.addScope(Plus.SCOPE_PLUS_LOGIN)!!.build()!!

        c.findView(R.id.callGoogle).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                forceGoogleConnect()
            }
        })

        c.findView(R.id.emailLogin).setOnClickListener {v -> EmailLoginActivity.startMe(c)}

        LocalBroadcastManager.getInstance(c)!!.registerReceiver(uuidReceiver, IntentFilter(GoogleLoginTask.GOOGLE_LOGIN_COMPLETE))
    }

    override fun onStop()
    {
        googleDisconnectIfConnected()
    }

    override fun onDestroy()
    {
        LocalBroadcastManager.getInstance(c)!!.unregisterReceiver(uuidReceiver)
    }

    fun googleClientConnect()
    {
        mGoogleApiClient.connect()
    }

    private fun googleDisconnectIfConnected()
    {
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect()
    }

    public fun forceGoogleConnect()
    {
        googleDisconnectIfConnected()
        mGoogleApiClient.connect()
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

            TaskQueue.execute(c, GoogleLoginTask(accountName!!, person?.getDisplayName(), imageURL))
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
                    result.startResolutionForResult(c, EnterUuidActivity.REQUEST_CODE_RESOLVE_ERR)
                }
                catch (e: IntentSender.SendIntentException)
                {
                    mGoogleApiClient.connect()
                }

            }
            else
            {
                Toaster.showMessage(c, R.string.google_error)
            }
        }


    }
}