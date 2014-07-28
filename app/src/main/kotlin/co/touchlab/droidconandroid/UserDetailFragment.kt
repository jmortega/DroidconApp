package co.touchlab.droidconandroid

import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import co.touchlab.android.threading.tasks.BsyncTaskManager
import android.app.Activity
import co.touchlab.droidconandroid.tasks.FindUserByIdTask
import co.touchlab.droidconandroid.tasks.UserInfoUpdate
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask
import co.touchlab.droidconandroid.utils.Toaster
import android.text.TextUtils
import com.squareup.picasso.Picasso
import android.text.Html
import co.touchlab.droidconandroid.utils.TextHelper
import android.text.method.LinkMovementMethod
import android.widget.Button
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.android.threading.tasks.TaskQueue
import co.touchlab.droidconandroid.tasks.FollowToggleTask

/**
 * Created by kgalligan on 7/27/14.
 */
class UserDetailFragment() : Fragment(), UserInfoUpdate
{
    private var userAvatar: ImageView? = null
    private var userName: TextView? = null
    private var profile: TextView? = null
    private var userCodeVal: TextView? = null
    private var company: TextView? = null
    private var twitter: TextView? = null
    private var linkedIn: TextView? = null
    private var website: TextView? = null
    private var followToggle: Button? = null
    private var bsyncTaskManager: BsyncTaskManager<Fragment>? = null

    class object
    {
        val HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES: String = "https://s3.amazonaws.com/droidconimages/"
        val USER_ID = "USER_ID"

        fun createFragment(id: Long): UserDetailFragment
        {
            val bundle = Bundle()
            bundle.putLong(USER_ID, id);

            val f = UserDetailFragment()
            f.setArguments(bundle);

            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<Fragment>.onCreate(savedInstanceState)
        bsyncTaskManager = BsyncTaskManager(savedInstanceState)
        bsyncTaskManager!!.register(this)
        bsyncTaskManager!!.post(getActivity(), FindUserByIdTask(findUserIdArg()))
    }

    private fun findUserIdArg():Long
    {
        var userId = getArguments()?.getLong(USER_ID, -1)
        if(userId == null || userId == -1L)
        {
            userId = getActivity()!!.getIntent()!!.getLongExtra(USER_ID, -1)
        }

        if(userId == null || userId == -1L)
            throw IllegalArgumentException("Must set user id");

        return userId!!
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater!!.inflate(R.layout.fragment_user_detail, null)!!

        userAvatar = view.findView(R.id.userAvatar) as ImageView
        userName = view.findView(R.id.userName) as TextView
        profile = view.findView(R.id.profile) as TextView
        userCodeVal = view.findView(R.id.userCodeVal) as TextView
        company = view.findView(R.id.company) as TextView
        twitter = view.findView(R.id.twitter) as TextView
        linkedIn = view.findView(R.id.linkedIn) as TextView
        website = view.findView(R.id.website) as TextView
        followToggle = view.findView(R.id.followToggle) as Button

        return view
    }

    override fun showResult(findUserTask: AbstractFindUserTask)
    {
        if (findUserTask.isError())
        {
            Toaster.showMessage(getActivity(), findUserTask.errorStringCode!!)
        }
        else
        {
            val userAccount = findUserTask.userInfoResponse?.user!!
            val avatarKey = userAccount.avatarKey
            if (!TextUtils.isEmpty(avatarKey))
                Picasso.with(getActivity())!!.load(HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES + avatarKey)!!.into(userAvatar)
            userName!!.setText(userAccount.name)
            val profileString = Html.fromHtml(TextHelper.findTagLinks(userAccount.profile))
            profile!!.setMovementMethod(LinkMovementMethod.getInstance())
            profile!!.setText(profileString)
            userCodeVal!!.setText(userAccount.userCode)
            company!!.setText(userAccount.company)
            twitter!!.setText(userAccount.twitter)
            linkedIn!!.setText(userAccount.linkedIn)
            website!!.setText(userAccount.website)

            val appPrefs = AppPrefs.getInstance(getActivity())
            if(userAccount.id.equals(appPrefs.getUserId()))
            {
                followToggle!!.setVisibility(View.GONE)
            }
            else
            {
                followToggle!!.setVisibility(View.VISIBLE)
                followToggle!!.setText(R.string.follow)
                followToggle!!.setOnClickListener { v ->
                    FollowToggleTask.createTask(getActivity()!!, userAccount.id)
                }
            }
        }
    }

}