package co.touchlab.droidconandroid

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.TaskQueue
import co.touchlab.droidconandroid.tasks.AbstractLoginTask
import co.touchlab.droidconandroid.tasks.GoogleLoginTask
import co.touchlab.droidconandroid.tasks.Queues
import co.touchlab.droidconandroid.utils.Toaster
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.plus.Plus

/**
 *
 * Created by izzyoji :) on 7/23/15.
 */
public class SignInActivity : AppCompatActivity() {

    public companion object {
        val REQUEST_CODE_RESOLVE_ERR = 9000
        var googleApiClient: GoogleApiClient? = null

        public fun getLaunchIntent(c: Context): Intent {
            return Intent(c, javaClass<SignInActivity>())
        }
    }

    private var okButton: Button? = null
    private var progressBar: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in);

        val accounts = AccountManager.get(this).getAccountsByType("com.google")
        val listView = findViewById(R.id.list) as ListView;
        val accountAdapter = AccountAdapter(this, accounts, R.layout.item_account)

        listView.setAdapter(accountAdapter)
        listView.setOnItemClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            accountAdapter.setSelectedAccount(i)
            okButton!!.setEnabled(true);
        }

        progressBar = findView(R.id.progress)
        (progressBar as ProgressBar).getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)

        okButton = findView(R.id.ok) as Button
        (okButton as Button).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                googleApiClient = GoogleApiClient.Builder(this@SignInActivity)
                        .addConnectionCallbacks(ConnectionCallbacksImpl())
                        .addOnConnectionFailedListener(OnConnectionFailedListenerImpl())
                        .addApi(Plus.API)
                        .setAccountName(accountAdapter.getSelectedAccount())
                        .addScope(Plus.SCOPE_PLUS_LOGIN)
                        .build()!!
                forceGoogleConnect()

                okButton!!.setEnabled(false);
                progressBar!!.setVisibility(View.VISIBLE)
            }
        })

        findView(R.id.cancel).setOnClickListener{
            finish()
        }

        EventBusExt.getDefault()!!.register(this)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_RESOLVE_ERR) {
            if (resultCode == Activity.RESULT_OK) {
                googleClientConnect()
            }
        }
    }

    public fun onEventMainThread(t: AbstractLoginTask) {
        if (!t.failed) {
            finish()
            MyActivity.startMe(this)
            if (t.firstLogin)
                EditUserProfile.callMe(this)
        }
        else
        {
            okButton!!.setEnabled(true);
            progressBar!!.setVisibility(View.GONE)
            Toaster.showMessage(this@SignInActivity, R.string.google_error)
        }
    }

    override fun onStop() {
        super.onStop()
        googleDisconnectIfConnected()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusExt.getDefault()!!.unregister(this)
    }

    fun googleClientConnect() {
        googleApiClient!!.connect()
    }

    private fun googleDisconnectIfConnected() {
        if (googleApiClient != null && googleApiClient!!.isConnected())
            googleApiClient!!.disconnect()
    }

    public fun forceGoogleConnect() {
        googleDisconnectIfConnected()
        googleApiClient!!.connect()
    }

    private val PROFILE_PIC_SIZE: Int = 100

    public inner class ConnectionCallbacksImpl() : GoogleApiClient.ConnectionCallbacks {
        override fun onConnected(bundle: Bundle?) {
            val accountName = Plus.AccountApi.getAccountName(googleApiClient)
            val person = Plus.PeopleApi.getCurrentPerson(googleApiClient)
            var imageURL: String? = null
            var coverURL: String? = null
            if (person != null && person.hasImage()) {
                val image = person.getImage()

                if (image != null && image.hasUrl()) {
                    val url = image.getUrl()
                    imageURL = url.substring(0, url.length() - 2) + PROFILE_PIC_SIZE;
                }

                val cover = person.getCover()
                if(cover != null && cover.getCoverPhoto() != null && cover.getCoverPhoto().hasUrl())
                {
                    coverURL = cover.getCoverPhoto().getUrl();
                }
            }

            Queues.networkQueue(this@SignInActivity).execute(GoogleLoginTask(accountName!!, person?.getDisplayName(), imageURL, coverURL))
        }

        override fun onConnectionSuspended(i: Int) {
        }
    }

    public inner class OnConnectionFailedListenerImpl() : GoogleApiClient.OnConnectionFailedListener {
        override fun onConnectionFailed(result: ConnectionResult?) {
            if (result != null && result.hasResolution()) {
                try {
                    result.startResolutionForResult(this@SignInActivity, SignInActivity.REQUEST_CODE_RESOLVE_ERR)
                    okButton!!.setEnabled(true);
                    progressBar!!.setVisibility(View.GONE)
                } catch (e: IntentSender.SendIntentException) {
                    googleApiClient!!.connect()
                }

            } else {
                Toaster.showMessage(this@SignInActivity, R.string.google_error)
            }
        }


    }


}

class AccountAdapter : ArrayAdapter<Account> {

    private var inflater: LayoutInflater
    private var selectedPos: Int = -1
    private var resource: Int

    constructor(context: Context, accounts: Array<Account>, resource: Int) : super(context, resource, accounts) {

        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.resource = resource;
    }


    fun getSelectedAccount(): String {
        return getItem(selectedPos).name
    }

    fun setSelectedAccount(position: Int) {
        selectedPos = position
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

        var view: View
        if (convertView == null) {
            view = inflater.inflate(resource, parent, false)
        } else {
            view = convertView
        }

        var name = view.findViewById(R.id.account) as TextView
        name.setText(getItem(position).name)

        var radioButton = view.findViewById(R.id.radio) as RadioButton
        radioButton.setChecked(selectedPos == position)

        return view;
    }
}
