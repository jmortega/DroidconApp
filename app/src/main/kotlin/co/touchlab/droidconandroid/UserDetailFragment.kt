package co.touchlab.droidconandroid

import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
import co.touchlab.droidconandroid.tasks.FollowToggleTask
import org.apache.commons.lang3.StringUtils
import android.support.v4.app.LoaderManager
import co.touchlab.droidconandroid.data.UserAccount
import android.support.v4.content.Loader
import android.support.v4.app.LoaderManager.LoaderCallbacks
import co.touchlab.android.threading.loaders.networked.DoubleTapResult
import co.touchlab.android.threading.loaders.networked.DoubleTapResult.Status

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

    private fun findUserIdArg(): Long
    {
        var userId = getArguments()?.getLong(USER_ID, -1)
        if (userId == null || userId == -1L)
        {
            userId = getActivity()!!.getIntent()!!.getLongExtra(USER_ID, -1)
        }

        if (userId == null || userId == -1L)
            throw IllegalArgumentException("Must set user id");

        return userId!!
    }

    private fun refreshUserData()
    {
//        bsyncTaskManager!!.post(getActivity(), FindUserByIdTask(findUserIdArg()))
    }

    class LambdaLoaderCallbacks<D>(
            val create: (id: Int, args: Bundle?)  -> Loader<D>,
            val finish: (loader: Loader<D>?, data: D?) -> Unit): LoaderManager.LoaderCallbacks<D>
    {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<D>?
        {
            return create(id, args)
        }

        override fun onLoadFinished(loader: Loader<D>?, data: D?)
        {
            finish(loader, data)
        }

        override fun onLoaderReset(loader: Loader<D>?)
        {
            //Ehh
        }

    }

    inner class UDLoaderCallbacks() : LoaderCallbacks<DoubleTapResult<UserAccount, Int>>
    {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<DoubleTapResult<UserAccount, Int>>?
        {
            return UserDetailLoader(getActivity()!!, findUserIdArg())
        }
        override fun onLoadFinished(loader: Loader<DoubleTapResult<UserAccount, Int>>?, data: DoubleTapResult<UserAccount, Int>?)
        {
            val status = data!!.getStatus()
            when(status)
            {
                Status.Data -> showUserData(data!!.getResult()!!)
                Status.NoData -> Toaster.showMessage(getActivity(), "NoData")
                Status.Waiting -> Toaster.showMessage(getActivity(), "Waiting")
                Status.Error -> Toaster.showMessage(getActivity(), "Error")
            }
        }
        override fun onLoaderReset(loader: Loader<DoubleTapResult<UserAccount, Int>>?)
        {

        }
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

        getLoaderManager()!!.initLoader(0, null, this.UDLoaderCallbacks())

//                getLoaderManager()!!.initLoader(0, null, LambdaLoaderCallbacks<UserAccount>(
//                {id, args -> UserDetailLoader(getActivity()!!, findUserIdArg())},
//                {loader, data -> if(data != null)showUserData(data)}
//        ))

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
            val userAccount = findUserTask.user!!
            showUserData(userAccount)
        }
    }

    private fun showUserData(userAccount: UserAccount)
    {
        val avatarKey = userAccount.avatarKey
        if (!TextUtils.isEmpty(avatarKey))
            Picasso.with(getActivity())!!.load(HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES + avatarKey)!!.into(userAvatar)
        userName!!.setText(userAccount.name)
        val profileString = Html.fromHtml(TextHelper.findTagLinks(StringUtils.trimToEmpty(userAccount.profile)!!))
        profile!!.setMovementMethod(LinkMovementMethod.getInstance())
        profile!!.setText(profileString)
        userCodeVal!!.setText(userAccount.userCode)
        company!!.setText(userAccount.company)
        twitter!!.setText(userAccount.twitter)
        linkedIn!!.setText(userAccount.linkedIn)
        website!!.setText(userAccount.website)

        val appPrefs = AppPrefs.getInstance(getActivity())
        if (userAccount.id.equals(appPrefs.getUserId()))
        {
            followToggle!!.setVisibility(View.GONE)
        }
        else
        {
            followToggle!!.setVisibility(View.VISIBLE)
            if (userAccount.following)
            {
                followToggle!!.setText(R.string.unfollow)
                followToggle!!.setOnClickListener { v ->
                    FollowToggleTask.createTask(getActivity()!!, userAccount.id!!)
                    refreshUserData()
                }
            }
            else
            {
                followToggle!!.setText(R.string.follow)
                followToggle!!.setOnClickListener { v ->
                    FollowToggleTask.createTask(getActivity()!!, userAccount.id!!)
                    refreshUserData()
                }
            }


        }
    }

}