package co.touchlab.droidconandroid

import android.app.Activity
import android.widget.EditText
import co.touchlab.android.threading.tasks.BsyncTaskManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import co.touchlab.droidconandroid.tasks.FindUserTaskKot
import co.touchlab.droidconandroid.utils.Toaster
import android.view.MenuItem
import co.touchlab.droidconandroid.tasks.UserInfoUpdate
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction

/**
 * Created by kgalligan on 7/26/14.
 */
public class FindUserKot : FragmentActivity(), UserInfoUpdate
{
    class object
    {
        val HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES: String = "https://s3.amazonaws.com/droidconimages/"
        public fun startMe(c : Context)
        {
            val i = Intent(c, javaClass<FindUserKot>())
            c.startActivity(i)
        }
    }

    private var userCode: EditText? = null
    private var bsyncTaskManager: BsyncTaskManager<Activity>? = null

    public fun callMe(c: Context)
    {
        val i = Intent(c, javaClass<FindUserKot>())
        c.startActivity(i)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<FragmentActivity>.onCreate(savedInstanceState)

        bsyncTaskManager = BsyncTaskManager(savedInstanceState)
        bsyncTaskManager!!.register(this)

        setContentView(R.layout.activity_find_user)
        userCode = findViewById(R.id.userCode) as EditText

        findView(R.id.findUser).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                val userCodeVal = userCode!!.getText().toString()
                bsyncTaskManager!!.post(this@FindUserKot, FindUserTaskKot(userCodeVal))
            }
        })

    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super<FragmentActivity>.onSaveInstanceState(outState)
        bsyncTaskManager!!.onSaveInstanceState(outState)
    }

    override fun onDestroy()
    {
        super<FragmentActivity>.onDestroy()
        bsyncTaskManager!!.unregister()
    }

    override fun showResult(findUserTask: AbstractFindUserTask)
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