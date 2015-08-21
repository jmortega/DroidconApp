package co.touchlab.droidconandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import java.util.ArrayList

class AboutActivity : AppCompatActivity()
{
    companion object
    {
        fun callMe(a: Activity)
        {
            val i = Intent(a, javaClass<AboutActivity>())
            a.startActivity(i)
        }
    }

    final var COLLAPSED_LINE_COUNT = 3

    var recycler: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setTitle(R.string.about)
        setSupportActionBar(toolbar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true)
        getSupportActionBar().setDisplayShowHomeEnabled(true)

        recycler = findViewById(R.id.about_list) as RecyclerView

        recycler!!.setLayoutManager(LinearLayoutManager(this))

        var adapter = AboutAdapter()
        adapter.add(R.string.about_app_header, R.string.about_app)
        adapter.add(R.string.about_con_header, R.string.about_con)
        adapter.add(R.string.about_touch_header, R.string.about_touch)
        recycler!!.setAdapter(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if (item.getItemId() == android.R.id.home)
            finish()

        return super.onOptionsItemSelected(item)
    }

    inner class AboutAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>()
    {
        private var dataset = ArrayList<AboutItem>()

        public fun add(headerRes: Int, bodyRes: Int)
        {
            dataset.add(AboutItem(headerRes, bodyRes, false))
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
            var view = LayoutInflater.from(this@AboutActivity).inflate(R.layout.item_about, parent, false)
            return AboutVH(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            var vh = holder as AboutVH
            var data = dataset.get(position)
            vh.body!!.setText(data.bodyRes)
            vh.header!!.setText(data.headerRes)
            vh.body!!.setMaxLines(Int.MAX_VALUE)

            if (!data.expanded)
                vh.body!!.setMaxLines(COLLAPSED_LINE_COUNT)
            else
                vh.body!!.setMaxLines(Int.MAX_VALUE)

            vh.button!!.setOnClickListener {

                if (data.expanded) {
                    vh.body!!.setMaxLines(COLLAPSED_LINE_COUNT)
                    vh.button!!.setText(getResources().getString(R.string.more))
                    data.expanded = false
                }
                else
                {
                    vh.body!!.setMaxLines(Int.MAX_VALUE)
                    vh.button!!.setText(getResources().getString(R.string.less))
                    data.expanded = true
                }
            }

            if (position == dataset.size()-1)
                vh.divider!!.setVisibility(View.GONE)
        }

        override fun getItemCount(): Int {
            return dataset.size()
        }

        inner class AboutVH(val item: View): RecyclerView.ViewHolder(item)
        {
            var header: TextView? = null
            var body: TextView? = null
            var button: Button? = null
            var divider: View? = null
            init
            {
                header = item.findViewById(R.id.header) as TextView
                body = item.findViewById(R.id.body) as TextView
                button = item.findViewById(R.id.more) as Button
                divider = item.findViewById(R.id.divider)
            }
        }

        inner data class AboutItem(val headerRes: Int, val bodyRes: Int, var expanded: Boolean)
    }
}