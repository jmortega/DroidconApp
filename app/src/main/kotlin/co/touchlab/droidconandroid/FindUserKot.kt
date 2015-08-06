package co.touchlab.droidconandroid

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.TaskQueue
import co.touchlab.android.threading.tasks.sticky.StickyTaskManager
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask
import co.touchlab.droidconandroid.tasks.FindUserByIdTask
import co.touchlab.droidconandroid.utils.Toaster
import com.google.zxing.integration.android.IntentIntegrator
import org.apache.commons.lang3.StringUtils

/**
 * Created by kgalligan on 7/26/14.
 */
public class FindUserKot : AppCompatActivity()
{
    companion object
    {
        val HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES: String = "https://s3.amazonaws.com/droidconimages/"
        val USER_PREFIX: String = "user_"
        public fun startMe(c: Context)
        {
            val i = Intent(c, javaClass<FindUserKot>())
            c.startActivity(i)
        }
    }

    private var userCode: EditText? = null
    private var stickyTaskManager: StickyTaskManager? = null

    public fun callMe(c: Context)
    {
        val i = Intent(c, javaClass<FindUserKot>())
        c.startActivity(i)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        stickyTaskManager = StickyTaskManager(savedInstanceState)

        setContentView(R.layout.activity_find_user)
        userCode = findViewById(R.id.userCode) as EditText

        findView(R.id.findUser).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                val userCodeVal = userCode!!.getText().toString()
                if(!TextUtils.isEmpty(userCodeVal)) {

                    try {
                        TaskQueue.loadQueueDefault(this@FindUserKot).execute(FindUserByIdTask(java.lang.Long.parseLong(userCodeVal)))
                    } catch(e: NumberFormatException) {
                        Toaster.showMessage(this@FindUserKot, "Search is only by ID right now.")
                    }
                }
            }
        })

        findView(R.id.startScanner).setOnClickListener { v -> startScan() }

        EventBusExt.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusExt.getDefault().unregister(this)
    }

    fun startScan()
    {
        val display = getWindowManager()!!.getDefaultDisplay()!!
        val size = Point();
        display.getSize(size);
        val width = size.x;
        val height = size.y;
        val minSize = Math.min(width, height)
        val scanSize = (minSize * .8).toInt()
        val integrator = IntentIntegrator(this)
        integrator.addExtra("SCAN_MODE", "QR_CODE_MODE")
        integrator.addExtra("SCAN_WIDTH", scanSize)
        integrator.addExtra("SCAN_HEIGHT", scanSize)
        integrator.addExtra("SAVE_HISTORY", false)

        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?)
    {
        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null)
        {
            val scanResults = scanResult.getContents()
            if(StringUtils.startsWith(scanResults, USER_PREFIX))
            {
                userCode!!.setText(scanResults!!.substring(USER_PREFIX.length()))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        stickyTaskManager!!.onSaveInstanceState(outState)
    }

    public fun onEventMainThread(findUserTask: AbstractFindUserTask)
    {
        if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            val userId = findUserTask.user?.id
            if (findUserTask.isError() || userId == null) {
                Toaster.showMessage(this, findUserTask.errorStringCode!!)
            } else {
                val fragmentManager = getSupportFragmentManager()
                val ft = fragmentManager!!.beginTransaction()!!

                ft.replace(R.id.fragmentContainer, UserDetailFragment.createFragment(userId), UserDetailFragment.TAG)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        }
    }

    fun closeFragment() {
        getSupportFragmentManager().popBackStack()
    }
}