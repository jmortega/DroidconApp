package co.touchlab.droidconandroid.ui

import android.os.Build
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.touchlab.droidconandroid.R
import co.touchlab.droidconandroid.data.Track
import java.util.ArrayList

/**
 *
 * Created by izzyoji :) on 8/7/15.
 */
class FilterAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    val VIEW_TYPE_HEADER: Int = 0
    val VIEW_TYPE_TRACK: Int = 1

    private var dataSet: List<Any>
    private val filterClickListener: FilterClickListener
    private var selectedTracks: ArrayList<Track>

    constructor(events: List<Any>, filterClickListener: FilterClickListener) : super() {
        dataSet = events;
        this.filterClickListener = filterClickListener
        selectedTracks = ArrayList()
    }

    override fun getItemCount(): Int {
        return dataSet.size()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        val v: View
        if (viewType == VIEW_TYPE_TRACK) {
            v = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.item_track, parent, false)
            return TrackViewHolder(v)
        }
        if (viewType == VIEW_TYPE_HEADER) {
            v = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.item_track_header, parent, false)
            return HeaderViewHolder(v as TextView)
        }
        throw UnsupportedOperationException()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val context = holder!!.itemView.getContext()
        val resources = context.getResources()
        if(getItemViewType(position) == VIEW_TYPE_HEADER){
            val title = dataSet.get(position) as String
            val headerHolder = holder as HeaderViewHolder

            (headerHolder.itemView as TextView).setText(title)

        } else if (getItemViewType(position) == VIEW_TYPE_TRACK) {
            val trackHolder = holder as TrackViewHolder
            val track = dataSet.get(position) as Track

            trackHolder.track.setText(track.getDisplayNameRes())
            val checked = selectedTracks.contains(track)
            holder.checkBox.setChecked(checked)
            if (checked) {
                holder.track.setTextColor(resources.getColor(track.getTextColorRes()))
            } else {
                holder.track.setTextColor(resources.getColor(R.color.text_gray))
            }

            //Only color the actual text boxes on lollipop+ couldnt find a good way to do so on
            //earlier devices without rolling out own assets
            if (Build.VERSION.SDK_INT >= 21)
            {
                holder.checkBox.setButtonTintList(resources.getColorStateList(track.getCheckBoxSelectorRes()));
                holder.checkBox.setBackgroundTintList(resources.getColorStateList(track.getTextColorRes()));
            }

            holder.itemView.setOnClickListener{
                if(!holder.checkBox.isChecked()) {
                    selectedTracks.add(track)
                } else {
                    selectedTracks.remove(track)
                }
                filterClickListener.onFilterClick(track)
                notifyDataSetChanged()
            }

        }
    }

    public fun clearSelectedTracks()
    {
        selectedTracks.clear()
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        if(position == 0) {
            return VIEW_TYPE_HEADER
        } else {
            return VIEW_TYPE_TRACK
        }

    }

    public class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public val track: TextView
        public val checkBox: AppCompatCheckBox

        init {
            track = itemView.findViewById(R.id.track) as TextView
            checkBox = itemView.findViewById(R.id.checkbox) as AppCompatCheckBox
        }
    }

    public class HeaderViewHolder(itemView: TextView) : RecyclerView.ViewHolder(itemView) {
    }

    fun getSelectedTracks(): ArrayList<Track> {
        return selectedTracks;
    }

    fun setSelectedTracks(tracks : ArrayList<Track>) {
        selectedTracks = tracks
        notifyDataSetChanged()
    }

}

interface FilterClickListener {

    fun onFilterClick(track: Track)

}