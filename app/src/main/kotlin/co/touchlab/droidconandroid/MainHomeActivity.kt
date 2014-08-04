package co.touchlab.droidconandroid

/**
 * Created by kgalligan on 8/4/14.
 */
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import co.touchlab.droidconandroid.ScheduleDataFragment
import android.os.Bundle
import co.touchlab.droidconandroid.R
import android.support.v4.view.ViewPager
import android.app.Activity
import android.content.Intent
import android.support.v4.view.PagerTabStrip

class MainHomeActivity : FragmentActivity()
{
    class object
    {
        fun callMe(c: Activity)
        {
            c.startActivity(Intent(c, javaClass<MainHomeActivity>()))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<FragmentActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_home)
        val pager = findViewById(R.id.viewPager)!! as ViewPager
        pager.setAdapter(MyPagerAdapter(getSupportFragmentManager()!!));
//        val pagerTabStrip = findViewById(R.id.pager_header)!! as PagerTabStrip
//        pagerTabStrip.
    }
}

class MyPagerAdapter(val fm: FragmentManager) : FragmentPagerAdapter(fm)
{

    override fun getItem(position: Int): Fragment
    {
        when (position)
        {
            0 -> return ScheduleDataFragment.newInstance()
            1 -> return MyProfileFragment.newInstance()
//            2 -> return ThirdFragment.newInstance("ThirdFragment, Instance 1");
//            3 -> return ThirdFragment.newInstance("ThirdFragment, Instance 2");
//            4 -> return ThirdFragment.newInstance("ThirdFragment, Instance 3");
        }
        throw IllegalStateException("Too many fragments")
    }

    override fun getPageTitle(position: Int): CharSequence?
    {
        when (position)
                {
            0 -> return "Schedule"
            1 -> return "My Profile"
//            2 -> return ThirdFragment.newInstance("ThirdFragment, Instance 1");
//            3 -> return ThirdFragment.newInstance("ThirdFragment, Instance 2");
//            4 -> return ThirdFragment.newInstance("ThirdFragment, Instance 3");
        }
        throw IllegalStateException("Too many fragments")
    }

    override fun getCount(): Int
    {
        return 2;
    }
}