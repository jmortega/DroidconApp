package co.touchlab.droidconandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import co.touchlab.droidconandroid.data.AppPrefs
import com.viewpagerindicator.CirclePageIndicator

/**
 *
 * Created by izzyoji :) on 7/21/15.
 */

public class WelcomeActivity : AppCompatActivity()
{
    override fun finish() {
        super.finish()
        AppPrefs.getInstance(this).setHasSeenWelcome();
    }

    public companion object
    {
        public fun getLaunchIntent(c : Context, short: Boolean) : Intent
        {
            val intent = Intent(c, javaClass<WelcomeActivity>())
            intent.putExtra("SHORT", short)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val short = getIntent().getBooleanExtra("SHORT", false)
        var lastIndex = if(short){1}else{4}
        val advanceButton = findViewById(R.id.advance)!! as Button

        val pager = findViewById(R.id.viewPager)!! as ViewPager
        pager.setAdapter(WelcomePagerAdapter(getSupportFragmentManager()!!, short))

        val indicator = findViewById(R.id.indicator)!! as CirclePageIndicator
        indicator.setViewPager(pager)
        if(short)
            indicator.setFillColor(getResources().getColor(R.color.orange))

        indicator.setOnPageChangeListener( object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageSelected(position: Int) {

                if(position >= lastIndex-1) {
                    advanceButton.setTextColor(getResources().getColor(R.color.orange))
                    indicator.setFillColor(getResources().getColor(R.color.orange))
                } else {
                    advanceButton.setTextColor(getResources().getColor(R.color.white))
                    indicator.setFillColor(getResources().getColor(R.color.white))
                }

                if(position == lastIndex) {
                    advanceButton.setText(R.string.lets_go)
                } else {
                    advanceButton.setText(R.string.next)
                }
            }
        })

        advanceButton.setOnClickListener { v ->
            val position = pager.getCurrentItem()
            when(position) {
                lastIndex -> {
//                    if(!short)
                        MyActivity.startMe(this@WelcomeActivity)
                    finish()
                }
                else -> {
                    indicator.setCurrentItem(position + 1)
                }
            }
        }
    }
}

class WelcomePagerAdapter(fragmentManager: FragmentManager, val short: Boolean) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment? {
        if(short){
            when (position)
            {
                0 -> return SponsorWelcomeFragment()
                1 -> return Sponsor2WelcomeFragment()
            }
            throw IllegalStateException("Too many fragments")
        }
        else{
            when (position)
            {
                0 -> return WelcomeFragment.newInstance(R.color.primary, R.drawable.welcome_0, R.color.white, R.string.welcome_0_title, R.string.welcome_0_desc)
                1 -> return WelcomeFragment.newInstance(R.color.droidcon_pink, R.drawable.welcome_1, R.color.white, R.string.welcome_1_title, R.string.welcome_1_desc)
                2 -> return WelcomeFragment.newInstance(R.color.droidcon_blue, R.drawable.welcome_2, R.color.white, R.string.welcome_2_title, R.string.welcome_2_desc)
            //3 -> return WelcomeFragment.newInstance(android.R.color.white, R.drawable.welcome_2, R.color.orange, R.string.welcome_3_title, R.string.welcome_3_desc)
                3 -> return SponsorWelcomeFragment()
                4 -> return Sponsor2WelcomeFragment()
            }
            throw IllegalStateException("Too many fragments")
        }
    }

    override fun getCount(): Int {
        return if(short){2}else{5};
    }

}
