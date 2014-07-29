package co.touchlab.droidconandroid

import android.support.v4.app.Fragment
import android.os.Bundle
import co.touchlab.android.threading.tasks.BsyncTaskManager
import co.touchlab.droidconandroid.tasks.FindUserByIdTask
import co.touchlab.droidconandroid.tasks.UserInfoUpdate
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask
import android.os.Handler
import android.os.Looper

/**
 * Created by kgalligan on 7/28/14.
 */
class UserDetailRetainedFragment : Fragment(), UserInfoUpdate
{
    private val bsyncTaskManager: BsyncTaskManager<Fragment>
    private val handler : Handler

    private var findUserTask: AbstractFindUserTask? = null
    private var inFlight = false

    {
        bsyncTaskManager = BsyncTaskManager(null)
        bsyncTaskManager.register(this)
        handler = Handler(Looper.getMainLooper()!!)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<Fragment>.onCreate(savedInstanceState)
        setRetainInstance(true)
    }

    fun reloadData(userId: Long)
    {
        findUserTask = null
        callTask(userId)
    }

    fun loadData(userId: Long)
    {
        handler.post {
            if(!inFlight)
            {
                if (findUserTask != null)
                    deliverResults()
                else
                {
                    callTask(userId!!)
                }
            }
        }
    }

    private fun callTask(userId: Long)
    {
        bsyncTaskManager!!.post(getActivity(), FindUserByIdTask(userId))
        inFlight = true
    }

    override fun showResult(findUserTask: AbstractFindUserTask)
    {
        inFlight = false
        this.findUserTask = findUserTask
        deliverResults()
    }

    fun deliverResults()
    {
        if(findUserTask != null)
            (getTargetFragment() as UserInfoUpdate).showResult(findUserTask!!)
    }

    override fun onDestroy()
    {
        super<Fragment>.onDestroy()
        bsyncTaskManager!!.unregister()
    }
}