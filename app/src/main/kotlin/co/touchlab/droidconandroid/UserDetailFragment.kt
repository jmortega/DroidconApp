package co.touchlab.droidconandroid

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.app.LoaderManager.LoaderCallbacks
import android.support.v4.content.Loader
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.loaders.networked.DoubleTapResult
import co.touchlab.android.threading.loaders.networked.DoubleTapResult.Status
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.data.UserAccount
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask
import co.touchlab.droidconandroid.tasks.FollowToggleTask
import co.touchlab.droidconandroid.utils.CustomTarget
import co.touchlab.droidconandroid.utils.PaletteTransformation
import co.touchlab.droidconandroid.utils.Toaster
import com.squareup.picasso.Picasso
import com.wnafee.vector.compat.ResourcesCompat

/**
 * Created by kgalligan on 7/27/14.
 */
class UserDetailFragment() : Fragment()
{
    private var avatar: ImageView? = null
    private var name: TextView? = null
    private var phone: TextView? = null
    private var phoneIcon: ImageView? = null
    private var phoneWrapper: View? = null
    private var email: TextView? = null
    private var emailIcon: ImageView? = null
    private var emailWrapper: View? = null
    private var company: TextView? = null
    private var twitter: TextView? = null
    private var twitterWrapper: View? = null
    private var gPlus: TextView? = null
    private var gPlusWrapper: View? = null
    private var website: TextView? = null
    private var websiteIcon: ImageView? = null
    private var websiteWrapper: View? = null
    private var company2: TextView? = null
    private var companyIcon: ImageView? = null
    private var companyWrapper: View? = null
    private var followToggle: Button? = null
    private var header: ImageView? = null

    companion object
    {
        val TAG: String = UserDetailFragment.javaClass.getSimpleName()
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

        interface FinishListener
        {
            fun onFragmentFinished()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBusExt.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusExt.getDefault().unregister(this)
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

        avatar = view.findView(R.id.profile_image) as ImageView
        name = view.findView(R.id.name) as TextView
        phone = view.findView(R.id.phone) as TextView
        phoneIcon = view.findView(R.id.phone_icon) as ImageView
        phoneWrapper = view.findView(R.id.phone_wrapper)
        email = view.findView(R.id.email) as TextView
        emailIcon = view.findView(R.id.email_icon) as ImageView
        emailWrapper = view.findView(R.id.email_wrapper)
        company = view.findView(R.id.company) as TextView
        twitter = view.findView(R.id.twitter) as TextView
        twitterWrapper = view.findView(R.id.wrapper_twitter)
        gPlus = view.findView(R.id.gPlus) as TextView
        gPlusWrapper = view.findViewById(R.id.gPlus_wrapper)
        website = view.findView(R.id.website) as TextView
        websiteIcon = view.findView(R.id.website_icon) as ImageView
        websiteWrapper = view.findView(R.id.website_wrapper)
        company2 = view.findView(R.id.company2) as TextView
        companyIcon = view.findView(R.id.company_icon) as ImageView
        companyWrapper = view.findView(R.id.company_wrapper)
        followToggle = view.findView(R.id.followToggle) as Button
        header = view.findView(R.id.header) as ImageView

        var close = view.findView(R.id.close);
        close.setOnClickListener{
        if (getActivity() is FinishListener)
            (getActivity() as FinishListener).onFragmentFinished()
        }

        getLoaderManager()!!.initLoader(0, null, this.UDLoaderCallbacks())

//                getLoaderManager()!!.initLoader(0, null, LambdaLoaderCallbacks<UserAccount>(
//                {id, args -> UserDetailLoader(getActivity()!!, findUserIdArg())},
//                {loader, data -> if(data != null)showUserData(data)}
//        ))

        return view
    }

    public fun onEventMainThread(findUserTask: AbstractFindUserTask)
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
        if (!TextUtils.isEmpty(avatarKey)) {

            Picasso.with(getActivity())!!
                    .load(HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES + avatarKey)
                    .into(avatar)

        }

        val coverKey = userAccount.coverKey
        val iconsDefaultColor = getResources().getColor(R.color.social_icons)
        if (!TextUtils.isEmpty(coverKey)) {
            //       http://jakewharton.com/coercing-picasso-to-play-with-palette/
            Picasso.with(getActivity())!!
                    .load(HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES + coverKey)
                    .transform(PaletteTransformation.instance())
                    .into(object : CustomTarget(){
                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            super.onBitmapLoaded(bitmap, from)
                            if(getActivity() != null)
                            {
                                var palette = PaletteTransformation.getPalette(bitmap);

                                header!!.setImageBitmap(bitmap)
                                val darkVibrantColor = palette.getDarkVibrantColor(iconsDefaultColor)

                                makeIconsPretty(darkVibrantColor)
                            }
                        }
                    })

        }
        else
        {
            makeIconsPretty(iconsDefaultColor)
        }

        if(!TextUtils.isEmpty(userAccount.phoneticName))
        {
            name!!.setText("${userAccount.name} (${userAccount.phoneticName})")
        }
        else
        {
            name!!.setText(userAccount.name)
        }

        if(!TextUtils.isEmpty(userAccount.phone)) {
            phone!!.setText(userAccount.phone)
            phoneWrapper!!.setVisibility(View.VISIBLE)
        }

        if(!TextUtils.isEmpty(userAccount.email)) {
            email!!.setText(userAccount.email)
            emailWrapper!!.setVisibility(View.VISIBLE)
        }

        if(!TextUtils.isEmpty(userAccount.company)) {
            company!!.setText(userAccount.company)
            company2!!.setText(userAccount.company)
            companyWrapper!!.setVisibility(View.VISIBLE)
            company!!.setVisibility(View.VISIBLE)
        }

        if(!TextUtils.isEmpty(userAccount.twitter)) {
            twitter!!.setText("@${userAccount.twitter}")
            twitterWrapper!!.setVisibility(View.VISIBLE)
        }

        if(!TextUtils.isEmpty(userAccount.gPlus)) {
            gPlus!!.setText("+${userAccount.gPlus}")
            gPlusWrapper!!.setVisibility(View.VISIBLE)
        }

        if(!TextUtils.isEmpty(userAccount.website)) {
            website!!.setText(userAccount.website)
            websiteWrapper!!.setVisibility(View.VISIBLE)
        }

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

    private fun makeIconsPretty(darkVibrantColor: Int) {
        val phoneDrawable = ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_phone);
        phoneDrawable.setColorFilter(PorterDuffColorFilter(darkVibrantColor, PorterDuff.Mode.SRC_IN))
        phoneIcon!!.setImageDrawable(phoneDrawable)
        val emailDrawable = ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_email);
        emailDrawable.setColorFilter(PorterDuffColorFilter(darkVibrantColor, PorterDuff.Mode.SRC_IN))
        emailIcon!!.setImageDrawable(emailDrawable)
        val companyDrawable = ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_work);
        companyDrawable.setColorFilter(PorterDuffColorFilter(darkVibrantColor, PorterDuff.Mode.SRC_IN))
        companyIcon!!.setImageDrawable(phoneDrawable)
        val websiteDrawable = ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_website);
        websiteDrawable.setColorFilter(PorterDuffColorFilter(darkVibrantColor, PorterDuff.Mode.SRC_IN))
        websiteIcon!!.setImageDrawable(phoneDrawable)
    }

}