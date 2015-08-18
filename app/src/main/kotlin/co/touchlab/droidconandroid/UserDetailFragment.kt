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
import co.touchlab.droidconandroid.tasks.FindUserByIdTask
import co.touchlab.droidconandroid.tasks.Queues
import co.touchlab.droidconandroid.utils.CustomTarget
import co.touchlab.droidconandroid.utils.PaletteTransformation
import co.touchlab.droidconandroid.utils.Toaster
import com.squareup.picasso.Picasso
import com.wnafee.vector.MorphButton
import com.wnafee.vector.compat.ResourcesCompat

/**
 * Created by kgalligan on 7/27/14.
 */
class UserDetailFragment() : Fragment()
{
    private var avatar: ImageView? = null
    private var name: TextView? = null
    private var phone: TextView? = null
    private var phoneIcon: MorphButton? = null
    private var phoneWrapper: View? = null
    private var email: TextView? = null
    private var emailIcon: MorphButton? = null
    private var emailWrapper: View? = null
    private var company: TextView? = null
    private var twitter: TextView? = null
    private var twitterWrapper: View? = null
    private var gPlus: TextView? = null
    private var gPlusWrapper: View? = null
    private var website: TextView? = null
    private var websiteIcon: MorphButton? = null
    private var websiteWrapper: View? = null
    private var company2: TextView? = null
    private var companyIcon: MorphButton? = null
    private var companyWrapper: View? = null
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
        Queues.networkQueue(getActivity()).execute(FindUserByIdTask(findUserIdArg()))
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater!!.inflate(R.layout.fragment_user_detail, null)!!

        avatar = view.findView(R.id.profile_image) as ImageView
        name = view.findView(R.id.name) as TextView
        phone = view.findView(R.id.phone) as TextView
        phoneIcon = view.findView(R.id.phone_icon) as MorphButton
        phoneWrapper = view.findView(R.id.phone_wrapper)
        email = view.findView(R.id.email) as TextView
        emailIcon = view.findView(R.id.email_icon) as MorphButton
        emailWrapper = view.findView(R.id.email_wrapper)
        company = view.findView(R.id.company) as TextView
        twitter = view.findView(R.id.twitter) as TextView
        twitterWrapper = view.findView(R.id.wrapper_twitter)
        gPlus = view.findView(R.id.gPlus) as TextView
        gPlusWrapper = view.findViewById(R.id.gPlus_wrapper)
        website = view.findView(R.id.website) as TextView
        websiteIcon = view.findView(R.id.website_icon) as MorphButton
        websiteWrapper = view.findView(R.id.website_wrapper)
        company2 = view.findView(R.id.company2) as TextView
        companyIcon = view.findView(R.id.company_icon) as MorphButton
        companyWrapper = view.findView(R.id.company_wrapper)
        header = view.findView(R.id.header) as ImageView

        var close = view.findView(R.id.close);
        close.setOnClickListener{
        if (getActivity() is FinishListener)
            (getActivity() as FinishListener).onFragmentFinished()
        }

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
        val avatarKey = userAccount.avatarImageUrl()
        if (!TextUtils.isEmpty(avatarKey)) {

            Picasso.with(getActivity())!!
                    .load(avatarKey)
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
//            emailWrapper!!.setVisibility(View.VISIBLE)
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
    }

    private fun makeIconsPretty(darkVibrantColor: Int) {
        val phoneDrawable = ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_phone);
        phoneDrawable.setColorFilter(PorterDuffColorFilter(darkVibrantColor, PorterDuff.Mode.SRC_IN))
        phoneIcon!!.setStartDrawable(phoneDrawable)
        val emailDrawable = ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_email);
        emailDrawable.setColorFilter(PorterDuffColorFilter(darkVibrantColor, PorterDuff.Mode.SRC_IN))
        emailIcon!!.setStartDrawable(emailDrawable)
        val companyDrawable = ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_work);
        companyDrawable.setColorFilter(PorterDuffColorFilter(darkVibrantColor, PorterDuff.Mode.SRC_IN))
        companyIcon!!.setStartDrawable(companyDrawable)
        val websiteDrawable = ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_website);
        websiteDrawable.setColorFilter(PorterDuffColorFilter(darkVibrantColor, PorterDuff.Mode.SRC_IN))
        websiteIcon!!.setStartDrawable(websiteDrawable)
    }

}