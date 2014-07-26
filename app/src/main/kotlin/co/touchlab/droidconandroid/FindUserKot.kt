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

/**
 * Created by kgalligan on 7/26/14.
 */
public class FindUserKot : Activity()
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
    private var bsyncTaskManager: BsyncTaskManager<Activity>? = null

    public fun callMe(c: Context)
    {
        val i = Intent(c, javaClass<FindUserKot>())
        c.startActivity(i)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        bsyncTaskManager = BsyncTaskManager(savedInstanceState)
        bsyncTaskManager!!.register(this)

        setContentView(R.layout.activity_find_user)
        userCode = findViewById(R.id.userCode) as EditText
        userAvatar = findViewById(R.id.userAvatar) as ImageView
        userName = findViewById(R.id.userName) as TextView
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
        super.onSaveInstanceState(outState)
        bsyncTaskManager!!.onSaveInstanceState(outState)
    }

    override fun onDestroy()
    {
        super.onDestroy()
        bsyncTaskManager!!.unregister()
    }

    public fun showResult(findUserTask: FindUserTaskKot)
    {
        if (findUserTask.isError())
        {
            Toaster.showMessage(this, findUserTask.errorStringCode!!)
        }
        else
        {
            if (findUserTask.userData!!.avatarKey != null)
                Picasso.with(this)!!.load(HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES + findUserTask.userData!!.avatarKey)!!.into(userAvatar)
            userName!!.setText(findUserTask.userData!!.name)
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
        return super.onOptionsItemSelected(item)
    }
}