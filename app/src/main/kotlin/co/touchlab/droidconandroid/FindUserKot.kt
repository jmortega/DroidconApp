package co.touchlab.droidconandroid

import android.app.Activity
import android.widget.ImageView
import android.widget.EditText
import android.widget.TextView
import co.touchlab.android.threading.tasks.BsyncTaskManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import co.touchlab.droidconandroid.tasks.FindUserTaskKot
import co.touchlab.droidconandroid.utils.Toaster
import com.squareup.picasso.Picasso
import android.view.MenuItem
import android.text.TextUtils
import co.touchlab.droidconandroid.utils.TextHelper
import android.text.Html
import android.text.method.LinkMovementMethod
import co.touchlab.droidconandroid.tasks.UserInfoUpdate
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask

/**
 * Created by kgalligan on 7/26/14.
 */
public class FindUserKot : Activity(), UserInfoUpdate
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
    private var userAvatar: ImageView? = null
    private var userName: TextView? = null
    private var profile: TextView? = null
    private var userCodeVal: TextView? = null
    private var company: TextView? = null
    private var twitter: TextView? = null
    private var linkedIn: TextView? = null
    private var website: TextView? = null
    private var bsyncTaskManager: BsyncTaskManager<Activity>? = null
    private var openedUserDetail = true

    public fun callMe(c: Context)
    {
        val i = Intent(c, javaClass<FindUserKot>())
        c.startActivity(i)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<Activity>.onCreate(savedInstanceState)

        bsyncTaskManager = BsyncTaskManager(savedInstanceState)
        bsyncTaskManager!!.register(this)

        setContentView(R.layout.activity_find_user)
        userCode = findViewById(R.id.userCode) as EditText
        userAvatar = findViewById(R.id.userAvatar) as ImageView
        userName = findViewById(R.id.userName) as TextView
        profile = findViewById(R.id.profile) as TextView
        userCodeVal = findViewById(R.id.userCodeVal) as TextView
        company = findViewById(R.id.company) as TextView
        twitter = findViewById(R.id.twitter) as TextView
        linkedIn = findViewById(R.id.linkedIn) as TextView
        website = findViewById(R.id.website) as TextView

        findView(R.id.findUser).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                val userCodeVal = userCode!!.getText().toString()
                openedUserDetail = false
                bsyncTaskManager!!.post(this@FindUserKot, FindUserTaskKot(userCodeVal))
            }
        })

    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super<Activity>.onSaveInstanceState(outState)
        bsyncTaskManager!!.onSaveInstanceState(outState)
    }

    override fun onDestroy()
    {
        super<Activity>.onDestroy()
        bsyncTaskManager!!.unregister()
    }

    override fun showResult(findUserTask: AbstractFindUserTask)
    {
        val userId = findUserTask.userInfoResponse?.user?.id
        if (findUserTask.isError() || userId == null)
        {
            Toaster.showMessage(this, findUserTask.errorStringCode!!)
        }
        else
        {
            if(!openedUserDetail)
            {
                openedUserDetail = true
                UserDetailActivity.callMe(this, userId)
            }
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
        return super<Activity>.onOptionsItemSelected(item)
    }
}