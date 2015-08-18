package co.touchlab.droidconandroid

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import co.touchlab.droidconandroid.data.UserAccount
import com.squareup.picasso.Picasso
import com.wnafee.vector.compat.ResourcesCompat
import org.apache.commons.lang3.StringUtils
import java.util.ArrayList

/**
 * Created by samuelhill on 8/7/15.
 */

class EventDetailAdapter(val context: Context, val trackColor: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    //dataset
    private var data = ArrayList<Detail>()

    //=================== Adapter types ===================
    public val TYPE_HEADER: Int = 0
    public val TYPE_BODY: Int = 1
    public val TYPE_DIVIDER: Int = 3
    public val TYPE_SPACE: Int = 4
    public val TYPE_SPEAKER: Int = 5

    //=================== Public helper functions ===================
    public fun addHeader(venue: String, icon: Int)
    {
        data.add(TextDetail(TYPE_HEADER, venue, icon))
    }

    public fun addBody(description: String)
    {
        data.add(TextDetail(TYPE_BODY, description, 0))
    }

    public fun addDivider()
    {
        data.add(Detail(TYPE_DIVIDER))
    }

    public fun addSpace(size: Int)
    {
        data.add(SpaceDetail(TYPE_SPACE, size))
    }

    public fun addSpeaker(speaker: UserAccount)
    {
        data.add(SpeakerDetail(TYPE_SPEAKER, speaker.avatarImageUrl(), speaker.name, speaker.profile, speaker.id))
    }

    //=================== Adapter Overrides ===================
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder?
    {
        var holder: RecyclerView.ViewHolder? = null
        when (viewType)
        {
            TYPE_HEADER, TYPE_BODY ->
            {
                var view = LayoutInflater.from(context).inflate(R.layout.item_event_text, parent, false)
                holder = TextVH(view)
            }
            TYPE_DIVIDER ->
            {
                var view = LayoutInflater.from(context).inflate(R.layout.item_drawer_divider, parent, false)
                holder = DividerVH(view)
            }
            TYPE_SPEAKER ->
            {
                var view = LayoutInflater.from(context).inflate(R.layout.list_user_summary, parent, false)
                holder = SpeakerVH(view)
            }
            TYPE_SPACE ->
            {
                var view = View(context)
                parent!!.addView(view)
                holder = object: RecyclerView.ViewHolder(view){}
            }
        }
        return holder
    }

    override fun getItemCount(): Int
    {
        return data.size()
    }

    override  fun getItemViewType (position: Int): Int
    {
        return data.get(position).getItemType();
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int)
    {
        when (holder!!.getItemViewType())
        {
            TYPE_HEADER ->
            {
                var headerVH = holder as TextVH
                var iconRes = (data.get(position) as TextDetail).icon
                var drawable = ResourcesCompat.getDrawable(context, iconRes)
                drawable.setColorFilter(PorterDuffColorFilter(trackColor, PorterDuff.Mode.SRC_IN))
                headerVH.image!!.setImageDrawable(drawable)

                headerVH.text!!.setText((data.get(position) as TextDetail).text)
                headerVH.text!!.setTextColor(trackColor)
                headerVH.text!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            }

            TYPE_BODY ->
            {
                var bodyVH = holder as TextVH
                bodyVH.image!!.setVisibility(View.INVISIBLE)

                val descriptionSpanned = Html.fromHtml(StringUtils.trimToEmpty((data.get(position) as TextDetail).text)!!)
                bodyVH.text!!.setText(descriptionSpanned)
            }

            TYPE_SPEAKER ->
            {
                var speakerVH = holder as SpeakerVH
                val avatarView = speakerVH.image
                val nameView = speakerVH.name
                val user = data.get(position) as SpeakerDetail

                if (!TextUtils.isEmpty(user.avatar))
                {
                    Picasso.with(context).load(user.avatar)
                            .noFade()
                            .placeholder(R.drawable.profile_placeholder)
                            .into(avatarView)
                }

                val formatString = context.getResources().getString(R.string.event_speaker_name);
                nameView!!.setText(formatString.format(user.name))
                nameView.setTextColor(trackColor)

                speakerVH.itemView.setOnClickListener(View.OnClickListener
                {
                    UserDetailActivity.callMe(context as Activity, user.id)
                })

                val bioSpanned = Html.fromHtml(StringUtils.trimToEmpty(user.bio)!!)
                speakerVH.bio!!.setText(bioSpanned)
            }

            TYPE_SPACE ->
            {
                var p = holder.itemView.getLayoutParams()
                p.height = (data.get(position) as SpaceDetail).size
                holder.itemView.setLayoutParams(p)
            }
        }
    }

    //=================== Adapter type models ===================
    open inner class Detail(val type: Int)
    {
        public fun getItemType(): Int
        {
            return type;
        }
    }

    inner data class TextDetail(type: Int, val text: String, val icon: Int): Detail(type)

    inner data class SpeakerDetail(type: Int, val avatar: String?, val name: String, val bio: String?, val id: Long): Detail(type)

    inner data class SpaceDetail(type: Int, val size: Int): Detail(type)

    //=================== Type ViewHolders ===================
    inner class TextVH(val item: View) : RecyclerView.ViewHolder(item)
    {
        public var image: ImageView? = null
        public var text: TextView? = null

        init
        {
            image = item.findViewById(R.id.image) as ImageView
            text = item.findViewById(R.id.text) as TextView
        }
    }

    inner data class DividerVH(val item: View) : RecyclerView.ViewHolder(item)

    inner class SpeakerVH(val item: View): RecyclerView.ViewHolder(item)
    {
        public var image: ImageView? = null
        public var name: TextView? = null
        public var bio: TextView? = null

        init
        {
            image = item.findViewById(R.id.profile_image) as ImageView
            name = item.findViewById(R.id.name) as TextView
            bio = item.findViewById(R.id.bio) as TextView
        }
    }
}