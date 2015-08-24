package co.touchlab.droidconandroid.ui

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import co.touchlab.droidconandroid.R
import co.touchlab.droidconandroid.UserDetailFragment
import co.touchlab.droidconandroid.data.AppPrefs
import com.squareup.picasso.Picasso
import com.wnafee.vector.compat.ResourcesCompat

class DrawerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    val VIEW_TYPE_HEADER: Int = 0
    val VIEW_TYPE_NAVIGATION: Int = 1
    val VIEW_TYPE_DIVIDER: Int = 2

    var selectedPos: Int = 1


    private val dataSet: List<Any>
    private val drawerClickListener: DrawerClickListener

    constructor(drawerItems: List<Any>, drawerClickListener: DrawerClickListener) : super() {
        dataSet = drawerItems;
        this.drawerClickListener = drawerClickListener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val context = holder!!.itemView.getContext()
        val resources = context.getResources()
        if(getItemViewType(position) == VIEW_TYPE_HEADER){
            val headerHolder = holder as HeaderViewHolder
            val avatarKey = AppPrefs.getInstance(context).getAvatarKey()
            if (!TextUtils.isEmpty(avatarKey)) {

                Picasso.with(context)
                        .load(UserDetailFragment.HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES + avatarKey)
                        .into(headerHolder.avatar)

            }

            val coverKey = AppPrefs.getInstance(context).getCoverKey()
            if (!TextUtils.isEmpty(coverKey)) {

                Picasso.with(context)
                        .load(UserDetailFragment.HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES + coverKey)
                        .into(headerHolder.cover)

            }

            headerHolder.name.setText(AppPrefs.getInstance(context).getName())
            headerHolder.email.setText(AppPrefs.getInstance(context).getEmail())

            headerHolder.itemView.setOnClickListener{
                if(selectedPos != position) {
                    drawerClickListener.onHeaderItemClick()
                }
            }

        } else if(getItemViewType(position) == VIEW_TYPE_NAVIGATION) {
            val navItem = dataSet.get(position) as NavigationItem
            val navHolder = holder as NavigationViewHolder
            navHolder.title.setText(navItem.titleRes)
            val drawable = ResourcesCompat.getDrawable(context, navItem.iconRes);

            val selected = selectedPos == position
            navHolder.itemView.setSelected(selected)
            if(selected) {
                drawable.setColorFilter(PorterDuffColorFilter(resources.getColor(R.color.primary), PorterDuff.Mode.SRC_IN))
                navHolder.highlight.setVisibility(View.VISIBLE)
            } else {
                drawable.setColorFilter(PorterDuffColorFilter(resources.getColor(R.color.drawer_icons), PorterDuff.Mode.SRC_IN))
                navHolder.highlight.setVisibility(View.GONE)
            }
            navHolder.icon.setImageDrawable(drawable)

            navHolder.itemView.setOnClickListener{
                if(selectedPos != position) {
                    drawerClickListener.onNavigationItemClick(position, navItem.titleRes)
                }
            }
        }


    }

    public  fun setSelectedPosition(position: Int) {
        selectedPos = position
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataSet.size()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        val v: View
        if (viewType == VIEW_TYPE_NAVIGATION) {
            v = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.item_drawer, parent, false)
            return NavigationViewHolder(v)
        } else if (viewType == VIEW_TYPE_HEADER){
            v = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.item_drawer_header, parent, false)
            return HeaderViewHolder(v)
        } else {
            v = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.item_drawer_divider, parent, false)
            return DividerViewHolder(v)
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return VIEW_TYPE_HEADER
        } else if (dataSet.get(position) is NavigationItem) {
            return VIEW_TYPE_NAVIGATION
        } else {
            return VIEW_TYPE_DIVIDER;
        }
    }

    public class NavigationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public val title: TextView
        public val icon: ImageView
        public val highlight: View

        init {
            title = itemView.findViewById(R.id.title) as TextView
            icon = itemView.findViewById(R.id.icon) as ImageView
            highlight = itemView.findViewById(R.id.highlight)
        }
    }

    public class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public val name: TextView
        public val email: TextView
        public val avatar: ImageView
        public val cover: ImageView

        init {
            name = itemView.findViewById(R.id.name) as TextView
            email = itemView.findViewById(R.id.email) as TextView
            avatar = itemView.findViewById(R.id.profile_image) as ImageView
            cover = itemView.findViewById(R.id.cover) as ImageView
        }
    }

    public class DividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}

interface DrawerClickListener {

    fun onNavigationItemClick(position: Int, titleRes: Int)

    fun onHeaderItemClick()

}

class NavigationItem {

    public val titleRes: Int
    public val iconRes: Int

    constructor(titleRes: Int, iconRes: Int){
        this.titleRes = titleRes;
        this.iconRes = iconRes;
    }
}
