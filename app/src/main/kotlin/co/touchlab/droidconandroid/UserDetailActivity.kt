package co.touchlab.droidconandroid

import android.app.Activity
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import java.nio.ByteBuffer

/**
 * Created by kgalligan on 7/27/14.
 */
class UserDetailActivity : AppCompatActivity(), UserDetailFragment.Companion.FinishListener
{
    companion object
    {
        val USER_CODE = "USER_CODE"
        fun callMe(a: Activity, userCode: String)
        {
            val i = Intent(a, javaClass<UserDetailActivity>())
            i.putExtra(USER_CODE, userCode)
            a.startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<AppCompatActivity>.onCreate(savedInstanceState)

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }

        setContentView(R.layout.activity_user_detail)
    }

    override fun onFragmentFinished()
    {
        if (isTaskRoot())
        {
            MyActivity.startMe(this)
        }

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
        setIntent(intent);
    }

    fun processIntent(intent: Intent) {
        var rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);

        var msg = rawMsgs[0] as NdefMessage

        var newIntent = Intent()
        var buf = ByteBuffer.wrap(msg.getRecords()[0].getPayload())
        val sb = StringBuilder()
        while(buf.hasRemaining())
        {
            sb.append(buf.getChar())
        }

        newIntent.putExtra(USER_CODE, sb.toString())
        setIntent(newIntent)
    }

    override fun onBackPressed() {
        onFragmentFinished()
    }
}