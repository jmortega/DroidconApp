package co.touchlab.droidconandroid

import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.wnafee.vector.compat.ResourcesCompat
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

/**
 * Created by samuelhill on 8/7/15.
 */

class EventDetailAdapter(val trackColor: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var data = ArrayList<Detail>()

    public val TYPE_HEADER: Int = 0
    public val TYPE_BODY: Int = 1
    public val TYPE_DIVIDER: Int = 3
    public val TYPE_SPEAKER: Int = 4

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder?
    {
        var holder: RecyclerView.ViewHolder? = null
        when (viewType)
        {
            TYPE_HEADER, TYPE_BODY ->
            {
                var view = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.item_event_text, parent, false)
                holder = TextVH(view)
            }
            TYPE_DIVIDER ->
            {
                var view = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.item_drawer_divider, parent, false)
                holder = DividerVH(view)
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
                var context = headerVH.image!!.getContext()
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
                var headerVH = holder as TextVH
                headerVH.image!!.setVisibility(View.INVISIBLE)

                headerVH.text!!.setText((data.get(position) as TextDetail).text)
            }
        }
    }

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
        public var image: CircleImageView? = null
        public var name: TextView? = null

        init
        {
            image = item.findViewById(R.id.profile_image) as CircleImageView
            name = item.findViewById(R.id.name) as TextView
        }
    }

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

    open inner class Detail(val type: Int)
    {
        public fun getItemType(): Int
        {
            return type;
        }
    }

    inner data class TextDetail(type: Int, val text: String, val icon: Int): Detail(type)
}