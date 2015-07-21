package co.touchlab.droidconandroid

import android.app.Activity
import android.widget.EditText
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import co.touchlab.droidconandroid.tasks.FindUserTaskKot
import co.touchlab.droidconandroid.utils.Toaster
import android.view.MenuItem
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import org.apache.commons.lang3.StringUtils
import com.google.zxing.integration.android.IntentIntegrator
import android.graphics.Point
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.TaskQueue
import co.touchlab.android.threading.tasks.sticky.StickyTaskManager

/**
 * Created by kgalligan on 7/26/14.
 */
public class FindUserKot : FragmentActivity()
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

    override fun onCreate(savedInstanceState: Bundle)
    {
        super<FragmentActivity>.onCreate(savedInstanceState)

        stickyTaskManager = StickyTaskManager(savedInstanceState)

        setContentView(R.layout.activity_find_user)
        userCode = findViewById(R.id.userCode) as EditText

        findView(R.id.findUser).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                val userCodeVal = userCode!!.getText().toString()
                TaskQueue.loadQueueDefault(this@FindUserKot).execute(FindUserTaskKot(userCodeVal))
            }
        })

        findView(R.id.startScanner).setOnClickListener { v -> startScan() }

        EventBusExt.getDefault().register(this)
    }

    override fun onDestroy() {
        super<FragmentActivity>.onDestroy()
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
                userCode!!.setText(scanResults!!.substring(USER_PREFIX.length))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super<FragmentActivity>.onSaveInstanceState(outState)
        stickyTaskManager!!.onSaveInstanceState(outState)
    }

    public fun onEventMainThread(findUserTask: AbstractFindUserTask)
    {
        val userId = findUserTask.user?.id
        if (findUserTask.isError() || userId == null)
        {
            Toaster.showMessage(this, findUserTask.errorStringCode!!)
        }
        else
        {
            val fragmentManager = getSupportFragmentManager()
            val ft = fragmentManager!!.beginTransaction()!!

            ft.replace(R.id.fragmentContainer, UserDetailFragment.createFragment(userId), "USER_FRAGMENT")
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item!!.getItemId()
        if (id == R.id.action_settings)
        {
            return true
        }
        return super<FragmentActivity>.onOptionsItemSelected(item)
    }
}