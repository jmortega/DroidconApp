package co.touchlab.droidconandroid

import android.os.Bundle
import android.app.Activity
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import co.touchlab.android.threading.tasks.BsyncTaskManager
import co.touchlab.droidconandroid.tasks.FindUserTaskKot
import co.touchlab.droidconandroid.utils.Toaster
import android.text.TextUtils
import com.squareup.picasso.Picasso
import android.text.Html
import co.touchlab.droidconandroid.utils.TextHelper
import android.text.method.LinkMovementMethod

/**
 * Created by kgalligan on 7/27/14.
 */
class UserDetailActivity : FractivityAdapterActivity()
{
    class object
    {
        val HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES: String = "https://s3.amazonaws.com/droidconimages/"
    }

    override fun createAdapter(savedInstanceState: Bundle?): FractivityAdapter
    {
        return UserDetailAdapter(this, savedInstanceState)
    }

    class UserDetailAdapter(c: Activity, savedInstanceState: Bundle?) : FractivityAdapter(c, savedInstanceState)
    {
        private var userCode: EditText
        private var userAvatar: ImageView
        private var userName: TextView
        private var profile: TextView
        private var userCodeVal: TextView
        private var company: TextView
        private var twitter: TextView
        private var linkedIn: TextView
        private var website: TextView
        private var bsyncTaskManager: BsyncTaskManager<Activity>

        {
            bsyncTaskManager = BsyncTaskManager(savedInstanceState)
            bsyncTaskManager.register(c)

            c.setContentView(R.layout.activity_find_user)
            userCode = c.findView(R.id.userCode) as EditText
            userAvatar = c.findView(R.id.userAvatar) as ImageView
            userName = c.findView(R.id.userName) as TextView
            profile = c.findView(R.id.profile) as TextView
            userCodeVal = c.findView(R.id.userCodeVal) as TextView
            company = c.findView(R.id.company) as TextView
            twitter = c.findView(R.id.twitter) as TextView
            linkedIn = c.findView(R.id.linkedIn) as TextView
            website = c.findView(R.id.website) as TextView
        }

        override fun onSaveInstanceState(outState: Bundle)
        {
            super.onSaveInstanceState(outState)
            bsyncTaskManager.onSaveInstanceState(outState)
        }

        override fun onDestroy()
        {
            super.onDestroy()
            bsyncTaskManager.unregister()
        }
        
        public fun showResult(findUserTask: FindUserTaskKot)
        {
            if (findUserTask.isError())
            {
                Toaster.showMessage(c, findUserTask.errorStringCode!!)
            }
            else
            {
                val userAccount = findUserTask.userInfoResponse?.user!!
                val avatarKey = userAccount.avatarKey
                if (!TextUtils.isEmpty(avatarKey))
                    Picasso.with(c)!!.load(HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES + avatarKey)!!.into(userAvatar)
                userName.setText(userAccount.name)
                val profileString = Html.fromHtml(TextHelper.findTagLinks(userAccount.profile))
                profile.setMovementMethod(LinkMovementMethod.getInstance())
                profile.setText(profileString)
                userCodeVal.setText(userAccount.userCode)
                company.setText(userAccount.company)
                twitter.setText(userAccount.twitter)
                linkedIn.setText(userAccount.linkedIn)
                website.setText(userAccount.website)
            }
        }
    }
}