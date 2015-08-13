package co.touchlab.droidconandroid

import android.content.Context
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.data.Track
import co.touchlab.droidconandroid.superbus.UploadAvatarCommand
import co.touchlab.droidconandroid.superbus.UploadCoverCommand
import co.touchlab.droidconandroid.ui.*
import com.wnafee.vector.compat.ResourcesCompat
import java.nio.ByteBuffer
import java.util.ArrayList

public class MyActivity : AppCompatActivity(), FilterInterface, NfcAdapter.CreateNdefMessageCallback
{
    public companion object
    {
        public fun startMe(c : Context)
        {
            val i = Intent(c, javaClass<MyActivity>())
            c.startActivity(i)
        }
    }

    var toolbar: Toolbar? = null
    private var drawerAdapter: DrawerAdapter? = null
    private var drawerLayout: DrawerLayout? = null
    private var filterAdapter: FilterAdapter? = null
    private val SELECTED_TRACKS = "tracks"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<AppCompatActivity>.onCreate(savedInstanceState)


        if (!AppPrefs.getInstance(this).getHasSeenWelcome())
        {
            startActivity(WelcomeActivity.getLaunchIntent(this@MyActivity))
            finish()
        }
        else if (!AppPrefs.getInstance(this).isLoggedIn())
        {
            startActivity(SignInActivity.getLaunchIntent(this@MyActivity))
            finish()
        }

        setContentView(R.layout.activity_my)

        toolbar = findViewById(R.id.toolbar) as Toolbar;
        setSupportActionBar(toolbar);
        setUpDrawers()

        var nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter != null)
        {
            nfcAdapter.setNdefPushMessageCallback(this,this)
        }


        if(savedInstanceState == null)
        {
            replaceContentWithFragment(ScheduleFragment.newInstance(true), ScheduleFragment.EXPLORE)
        }
        else
        {
            val filters = savedInstanceState.getStringArrayList(SELECTED_TRACKS)
            val tracks = ArrayList<Track>()
            for (trackServerName in filters) {
                tracks.add(Track.findByServerName(trackServerName))
            }

            filterAdapter!!.setSelectedTracks(tracks)

            val fragment = getSupportFragmentManager().findFragmentById(R.id.container)
            if(fragment != null)
            {
                val tag = fragment.getTag()
                adjustToolBarAndDrawers(tag)
            }
        }

        EventBusExt.getDefault().register(this)
    }

    private fun adjustToolBarAndDrawers(tag: String) {

        if (TextUtils.equals(tag, ScheduleFragment.MY_SCHEDULE)) {
            toolbar!!.setTitle(R.string.my_schedule)
            toolbar!!.setBackgroundColor(getResources().getColor(R.color.blue_grey))
            drawerAdapter!!.setSelectedPosition(2)
        } else if (TextUtils.equals(tag, ScheduleFragment.EXPLORE)) {
            toolbar!!.setTitle(R.string.app_name)
            toolbar!!.setBackgroundColor(getResources().getColor(R.color.primary))
            drawerAdapter!!.setSelectedPosition(1)
        }
    }

    public fun onEventMainThread(command: UploadAvatarCommand) {
        drawerAdapter!!.notifyDataSetChanged()
    }

    public fun onEventMainThread(command: UploadCoverCommand) {
        drawerAdapter!!.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super<AppCompatActivity>.onDestroy()
        EventBusExt.getDefault().unregister(this)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super<AppCompatActivity>.onSaveInstanceState(outState)
        outState!!.putStringArrayList(SELECTED_TRACKS, getCurrentFilters())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.home, menu)
        var filter = menu!!.findItem(R.id.action_filter)
        filter.setIcon(ResourcesCompat.getDrawable(this, R.drawable.ic_filter))
        return super<AppCompatActivity>.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.getItemId() == R.id.action_filter){
            drawerLayout!!.openDrawer(findViewById(R.id.filter_wrapper))
        }
        return super<AppCompatActivity>.onOptionsItemSelected(item)
    }

    private fun replaceContentWithFragment(fragment: Fragment, tag: String) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit()
    }

    private fun setUpDrawers() {
        drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout;
        var drawerToggle = ActionBarDrawerToggle(
                this,  drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout!!.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle.syncState();

        var recyclerView = findView(R.id.drawer_list) as RecyclerView
        drawerAdapter = DrawerAdapter(getDrawerItems(), object : DrawerClickListener {
            override fun onNavigationItemClick(position: Int, titleRes: Int) {
                drawerLayout!!.closeDrawer(recyclerView)

                var fragment: Fragment? = null
                var tag: String? = null

                when (titleRes) {
                    R.string.explore -> {
                        fragment = ScheduleFragment.newInstance(true)
                        tag = ScheduleFragment.EXPLORE
                    }
                    R.string.my_schedule -> {
                        fragment = ScheduleFragment.newInstance(false)
                        tag = ScheduleFragment.MY_SCHEDULE

                    }
                    R.string.social -> FindUserKot.startMe(this@MyActivity)
                    R.string.settings -> EditUserProfile.callMe(this@MyActivity)
                }

                if (fragment != null) {
                    replaceContentWithFragment(fragment, tag!!)
                    drawerAdapter!!.setSelectedPosition(position)
                    adjustToolBarAndDrawers(tag)
                    filterAdapter!!.clearSelectedTracks()
                }
            }
        })
        recyclerView.setAdapter(drawerAdapter)

        recyclerView.setLayoutManager(LinearLayoutManager(this))

        var filterRecycler = findView(R.id.filter) as RecyclerView
        filterRecycler.setLayoutManager(LinearLayoutManager(this))

        filterAdapter = FilterAdapter(getFilterItems(), object : FilterClickListener {
            override fun onFilterClick(track: Track) {

                val fragment = getSupportFragmentManager().findFragmentById(R.id.container)
                if (fragment is FilterableFragmentInterface) {
                    fragment.applyFilters(track)
                }
            }
        })
        filterRecycler.setAdapter(filterAdapter)

        findViewById(R.id.back).setOnClickListener{
            drawerLayout!!.closeDrawer(findViewById(R.id.filter_wrapper))
        }

    }

    override fun getCurrentFilters(): ArrayList<String> {
        val filters = ArrayList<String>()
        for (track in filterAdapter!!.getSelectedTracks()) {
            filters.add(track.getServerName())
        }
        return filters
    }

    private fun getFilterItems(): List<Any> {
        var filterItems = ArrayList<Any>()
        filterItems.add(getString(R.string.tracks))
        filterItems.add(Track.DEVELOPMENT)
        filterItems.add(Track.DESIGN)
        filterItems.add(Track.BUSINESS)

        return filterItems
    }

    private fun getDrawerItems(): List<Any> {

        var drawerItems = ArrayList<Any>()
        drawerItems.add("header_placeholder")
        drawerItems.add(NavigationItem(R.string.explore, R.drawable.ic_explore))
        drawerItems.add(NavigationItem(R.string.my_schedule, R.drawable.ic_myschedule))
//        drawerItems.add(NavigationItem(R.string.map, R.drawable.ic_map))
//        drawerItems.add(NavigationItem(R.string.social, R.drawable.ic_social))
        drawerItems.add("divider_placeholder")
        drawerItems.add(NavigationItem(R.string.settings, R.drawable.ic_settings))
        drawerItems.add(NavigationItem(R.string.about, R.drawable.ic_info))
        return drawerItems;
    }

    override fun createNdefMessage(event: NfcEvent?): NdefMessage?
    {
        val appPrefs = AppPrefs.getInstance(this)
        var id = ByteBuffer.allocate(java.lang.Long.SIZE / java.lang.Byte.SIZE).putLong(appPrefs.getUserId()).array()
        var msg = NdefMessage( arrayOf(NdefRecord.createMime("application/vnd.co.touchlab.droidconandroid", id)
                   ,NdefRecord.createApplicationRecord("co.touchlab.droidconandroid")))
        return msg;
    }
}

interface FilterInterface {

    fun getCurrentFilters(): ArrayList<String>

}

interface FilterableFragmentInterface {

    fun applyFilters(track: Track)

}

