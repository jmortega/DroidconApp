package co.touchlab.droidconandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.SearchView
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.sticky.StickyTaskManager
import co.touchlab.droidconandroid.network.dao.UserAccount
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask
import co.touchlab.droidconandroid.tasks.FindUserTaskKot
import co.touchlab.droidconandroid.tasks.Queues
import co.touchlab.droidconandroid.ui.EmailAccountsEditText
import co.touchlab.droidconandroid.utils.Toaster
import org.apache.commons.lang3.StringUtils

/**
 * Created by kgalligan on 7/26/14.
 */
public class FindUserKot : AppCompatActivity(), UserDetailFragment.Companion.FinishListener
{
    companion object
    {
        public fun startMe(c: Context)
        {
            val i = Intent(c, javaClass<FindUserKot>())
            c.startActivity(i)
        }
    }

    private var searchView: EmailAccountsEditText? = null
    private var stickyTaskManager: StickyTaskManager? = null

    public fun callMe(c: Context)
    {
        val i = Intent(c, javaClass<FindUserKot>())
        c.startActivity(i)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<AppCompatActivity>.onCreate(savedInstanceState)

        stickyTaskManager = StickyTaskManager(savedInstanceState)

        setContentView(R.layout.activity_find_user)
        val toolbar = findViewById(R.id.toolbar) as Toolbar;
        setSupportActionBar(toolbar);

        searchView = findViewById(R.id.search) as EmailAccountsEditText;

        searchView!!.setOnItemClickListener(ItemClick())
        searchView!!.setOnItemSelectedListener(ItemSelected())

        /*searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!TextUtils.isEmpty(query)) {
                    Queues.networkQueue(this@FindUserKot).execute(FindUserTaskKot(query!!))
                    searchView!!.clearFocus()
                }
                return false
            }
        })*/

        EventBusExt.getDefault().register(this)
    }

    inner class ItemClick : AdapterView.OnItemClickListener
    {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            callResult(position)
        }
    }

    inner class ItemSelected : AdapterView.OnItemSelectedListener
    {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            callResult(position)
        }
    }
    private fun callResult(position: Int) {
        val userAccount = searchView!!.getAdapter().getItem(position) as UserAccount
        UserDetailActivity.callMe(this, userAccount.userCode)
        searchView!!.clearListSelection()
    }

    override fun onResume() {
        super<AppCompatActivity>.onResume()
        if(getSupportFragmentManager().findFragmentByTag(UserDetailFragment.TAG) != null) {
            //unsuccessful attempt to hide keyboard
            searchView!!.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchView!!.getWindowToken(), 0);
        }
    }

    override fun onDestroy() {
        super<AppCompatActivity>.onDestroy()
        EventBusExt.getDefault().unregister(this)
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super<AppCompatActivity>.onSaveInstanceState(outState)
        stickyTaskManager!!.onSaveInstanceState(outState)
    }

    public fun onEventMainThread(findUserTask: AbstractFindUserTask)
    {
        if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            val userCode = findUserTask.user?.userCode

            if (findUserTask.isError() || StringUtils.isEmpty(userCode)) {
                Toaster.showMessage(this, findUserTask.errorStringCode!!)
            } else {
                val fragmentManager = getSupportFragmentManager()
                val ft = fragmentManager!!.beginTransaction()!!

                ft.replace(R.id.fragmentContainer, UserDetailFragment.createFragment(userCode!!), UserDetailFragment.TAG)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        }
    }

    override fun onFragmentFinished()
    {
        getSupportFragmentManager().popBackStack()
    }
}