package co.touchlab.droidconandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.ui.DrawerAdapter
import co.touchlab.droidconandroid.ui.DrawerClickListener
import co.touchlab.droidconandroid.ui.NavigationItem
import java.util.ArrayList

public class MyActivity : AppCompatActivity()
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

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)


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
        setUpDrawer()

        if(savedInstanceState == null)
        {
            replaceContentWithFragment(ScheduleDataFragment.newInstance(true))
        }
    }

    private fun replaceContentWithFragment(fragment: Fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, ScheduleDataFragment.EXPLORE)
                .commit()
    }

    private var drawerAdapter: DrawerAdapter? = null

    private fun setUpDrawer() {
        var drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout;
        var drawerToggle = ActionBarDrawerToggle(
                this,  drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle.syncState();

        var recyclerView = findView(R.id.drawer_list) as RecyclerView
        drawerAdapter = DrawerAdapter(getDrawerItems(), object : DrawerClickListener {
            override fun onNavigationItemClick(position: Int, titleRes: Int) {
                drawerLayout.closeDrawer(recyclerView)

                var fragment: Fragment? = null

                when (titleRes) {
                    R.string.explore -> {
                        fragment = ScheduleDataFragment.newInstance(true)
                        toolbar!!.setTitle(R.string.app_name)
                    }
                    R.string.my_schedule -> {
                        fragment = ScheduleDataFragment.newInstance(false)
                        toolbar!!.setTitle(R.string.my_schedule)
                    }
                    R.string.social -> FindUserKot.startMe(this@MyActivity)
                    R.string.settings -> EditUserProfile.callMe(this@MyActivity)
                }

                if (fragment != null) {
                    replaceContentWithFragment(fragment)
                    drawerAdapter!!.setSelectedPosition(position)

                }
            }
        })
        recyclerView.setAdapter(drawerAdapter)

        recyclerView.setLayoutManager(LinearLayoutManager(this))

    }

    override fun onResume() {
        super.onResume()
        drawerAdapter!!.notifyDataSetChanged()
    }

    private fun getDrawerItems(): List<Any> {

        var drawerItems = ArrayList<Any>()
        drawerItems.add("header_placeholder")
        drawerItems.add(NavigationItem(R.string.explore, R.drawable.ic_explore))
        drawerItems.add(NavigationItem(R.string.my_schedule, R.drawable.ic_myschedule))
        drawerItems.add(NavigationItem(R.string.map, R.drawable.ic_map))
        drawerItems.add(NavigationItem(R.string.social, R.drawable.ic_social))
        drawerItems.add("divider_placeholder")
        drawerItems.add(NavigationItem(R.string.settings, R.drawable.ic_settings))
        drawerItems.add(NavigationItem(R.string.about, R.drawable.ic_info))
        return drawerItems;
    }
}

