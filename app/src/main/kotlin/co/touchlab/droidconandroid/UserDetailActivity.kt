package co.touchlab.droidconandroid

import android.app.Activity
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by kgalligan on 7/27/14.
 */
class UserDetailActivity : AppCompatActivity(), UserDetailFragment.Companion.FinishListener
{
    companion object
    {
        val USER_ID = "USER_ID"
        fun callMe(a: Activity, id: Long)
        {
            val i = Intent(a, javaClass<UserDetailActivity>())
            i.putExtra(USER_ID, id)
            a.startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
    }

    override fun onFragmentFinished() {
        finish()
    }

    override fun onResume() {
        super<AppCompatActivity>.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    override fun onNewIntent(intent: Intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    fun processIntent(intent: Intent) {
        var rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        var msg = rawMsgs[0] as NdefMessage
        // record 0 contains the MIME type, record 1 is the AAR, if present
    }
}