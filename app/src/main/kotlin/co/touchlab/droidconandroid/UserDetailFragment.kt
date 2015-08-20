package co.touchlab.droidconandroid

import android.app.SearchManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.data.UserAccount
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask
import co.touchlab.droidconandroid.tasks.FindUserByIdTask
import co.touchlab.droidconandroid.tasks.Queues
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
    private var facebookIcon: ImageView? = null
    private var facebook: TextView? = null
    private var facebookWrapper: View? = null
    private var twitter: TextView? = null
    private var twitterIcon: ImageView? = null
    private var twitterWrapper: View? = null
    private var linkedInIcon: ImageView? = null
    private var linkedIn: TextView? = null
    private var linkedInWrapper: View? = null
    private var gPlus: TextView? = null
    private var gPlusIcon: ImageView? = null
    private var gPlusWrapper: View? = null
    private var website: TextView? = null
    private var websiteIcon: ImageView? = null
    private var websiteWrapper: View? = null
    private var company2: TextView? = null
    private var companyIcon: ImageView? = null
    private var companyWrapper: View? = null
    private var header: ImageView? = null

    companion object
    {
        val TAG: String = UserDetailFragment.javaClass.getSimpleName()
        val HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES: String = "https://s3.amazonaws.com/droidconimages/"
        val TWITTER_PREFIX: String = "http://www.twitter.com/"
        val GPLUS_PREFIX: String = "http://www.google.com/+"
        val LINKEDIN_PREFIX: String = "http://www.linkedin.com/in/"
        val FACEBOOK_PREFIX: String = "http://www.facebook.com/"
        val PHONE_PREFIX: String = "tel:"
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
        phoneIcon = view.findView(R.id.phone_icon) as ImageView
        phoneWrapper = view.findView(R.id.phone_wrapper)
        email = view.findView(R.id.email) as TextView
        emailIcon = view.findView(R.id.email_icon) as ImageView
        emailWrapper = view.findView(R.id.email_wrapper)
        company = view.findView(R.id.company) as TextView
        facebookIcon = view.findView(R.id.facebook_icon) as ImageView
        facebook = view.findView(R.id.facebook) as TextView
        facebookWrapper = view.findView(R.id.wrapper_facebook)
        twitter = view.findView(R.id.twitter) as TextView
        twitterIcon = view.findView(R.id.twitter_icon) as ImageView
        twitterWrapper = view.findView(R.id.wrapper_twitter)
        linkedInIcon = view.findView(R.id.linkedIn_icon) as ImageView
        linkedIn = view.findView(R.id.linkedIn) as TextView
        linkedInWrapper = view.findView(R.id.wrapper_linkedIn)
        gPlus = view.findView(R.id.gPlus) as TextView
        gPlusIcon = view.findView(R.id.gPlus_icon) as ImageView
        gPlusWrapper = view.findViewById(R.id.gPlus_wrapper)
        website = view.findView(R.id.website) as TextView
        websiteIcon = view.findView(R.id.website_icon) as ImageView
        websiteWrapper = view.findView(R.id.website_wrapper)
        company2 = view.findView(R.id.company2) as TextView
        companyIcon = view.findView(R.id.company_icon) as ImageView
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

            if (getActivity() is UserDetailActivity)
                (getActivity() as UserDetailActivity).onFragmentFinished()
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
//                    .transform(PaletteTransformation.instance())
                    /*.into(object : CustomTarget(){
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
                    })*/
            .into(header)

        }
        else
        {
            makeIconsPretty(iconsDefaultColor)
        }

        if(!TextUtils.isEmpty(userAccount.phone)) {
            name!!.setText(userAccount.name)
        }

        if(!TextUtils.isEmpty(userAccount.phone)) {
            phone!!.setText(userAccount.phone)
            phoneWrapper!!.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(PHONE_PREFIX + userAccount.phone));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
            phoneWrapper!!.setVisibility(View.VISIBLE)
        }

        if(!TextUtils.isEmpty(userAccount.email) && userAccount.emailPublic != null && userAccount.emailPublic) {
            email!!.setText(userAccount.email)

            emailWrapper!!.setOnClickListener {
                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", userAccount.email, null));
                startActivity(emailIntent);
            }
            emailWrapper!!.setVisibility(View.VISIBLE)
        }

        if(!TextUtils.isEmpty(userAccount.company)) {
            company!!.setText(userAccount.company)
            company2!!.setText(userAccount.company)
            companyWrapper!!.setVisibility(View.VISIBLE)
            companyWrapper!!.setOnClickListener{
                val intent = Intent(Intent.ACTION_WEB_SEARCH);
                val keyword= userAccount.company;
                intent.putExtra(SearchManager.QUERY, keyword);
                startActivity(intent);
            }
            company!!.setVisibility(View.VISIBLE)
        }

        var facebookAccount = userAccount.facebook
        if(!TextUtils.isEmpty(facebookAccount)) {
            facebook!!.setText(facebookAccount)
            facebookWrapper!!.setOnClickListener {
                openLink(Uri.parse(FACEBOOK_PREFIX + facebookAccount))
            }
            facebookWrapper!!.setVisibility(View.VISIBLE)
        }

        var twitterAccount = userAccount.twitter
        if(!TextUtils.isEmpty(twitterAccount)) {
            twitterAccount = twitterAccount.replace("@", "")
            twitter!!.setText("@$twitterAccount")
            twitterWrapper!!.setOnClickListener {
                openLink(Uri.parse(TWITTER_PREFIX + twitterAccount))
            }
            twitterWrapper!!.setVisibility(View.VISIBLE)
        }

        var linkedInAccount = userAccount.linkedIn
        if(!TextUtils.isEmpty(linkedInAccount)) {
            linkedIn!!.setText(linkedInAccount)
            linkedInWrapper!!.setOnClickListener {
                openLink(Uri.parse(LINKEDIN_PREFIX + linkedInAccount))
            }
            linkedInWrapper!!.setVisibility(View.VISIBLE)
        }

        var gPlusAccount = userAccount.gPlus
        if(!TextUtils.isEmpty(gPlusAccount)) {
            gPlusAccount = gPlusAccount.replace("+", "")
            gPlus!!.setText("+$gPlusAccount")
            gPlusWrapper!!.setOnClickListener {
                openLink(Uri.parse(GPLUS_PREFIX + gPlusAccount))
            }
            gPlusWrapper!!.setVisibility(View.VISIBLE)
        }

        if(!TextUtils.isEmpty(userAccount.website)) {
            website!!.setText(userAccount.website)
            websiteWrapper!!.setOnClickListener{
                var url = userAccount.website

                if (!url.startsWith("http://")) {
                    url = "http://" + url;
                }
                openLink(Uri.parse(url))
            }
            websiteWrapper!!.setVisibility(View.VISIBLE)
        }
        

        val appPrefs = AppPrefs.getInstance(getActivity())
        if (!userAccount.id.equals(appPrefs.getUserId()))
        {
            val addContact = getView().findView(R.id.addContact) as ImageView
            addContact.setOnClickListener{
                // Creates a new Intent to insert a contact
                val intent = Intent(ContactsContract.Intents.Insert.ACTION);
                // Sets the MIME type to match the Contacts Provider
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                if (userAccount.emailPublic != null && userAccount.emailPublic)
                    intent.putExtra(ContactsContract.Intents.Insert.EMAIL, userAccount.email)
                intent.putExtra(ContactsContract.Intents.Insert.COMPANY, userAccount.company)
                intent.putExtra(ContactsContract.Intents.Insert.NAME, userAccount.name)
                startActivity(intent);
            }
            addContact.setImageDrawable(ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_addcontact))
            addContact.setVisibility(View.VISIBLE)
        }
    }

    private fun openLink(webpage: Uri?) {
        val intent = Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivity(intent);
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
        companyIcon!!.setImageDrawable(companyDrawable)
        val websiteDrawable = ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_website);
        websiteDrawable.setColorFilter(PorterDuffColorFilter(darkVibrantColor, PorterDuff.Mode.SRC_IN))
        websiteIcon!!.setImageDrawable(websiteDrawable)

        twitterIcon!!.setImageDrawable(ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_twitter))
        facebookIcon!!.setImageDrawable(ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_facebook))
        linkedInIcon!!.setImageDrawable(ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_linkedin))
        gPlusIcon!!.setImageDrawable(ResourcesCompat.getDrawable(getActivity(), R.drawable.ic_gplus))
    }

}