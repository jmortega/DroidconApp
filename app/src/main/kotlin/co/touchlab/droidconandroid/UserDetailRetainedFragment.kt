package co.touchlab.droidconandroid

import android.support.v4.app.Fragment
import android.os.Bundle
import co.touchlab.android.threading.tasks.BsyncTaskManager
import co.touchlab.droidconandroid.tasks.FindUserByIdTask
import co.touchlab.droidconandroid.tasks.UserInfoUpdate
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask

/**
 * Created by kgalligan on 7/28/14.
 */
class UserDetailRetainedFragment : Fragment(), UserInfoUpdate
{
    private var bsyncTaskManager: BsyncTaskManager<Fragment>? = null
    private var findUserTask: AbstractFindUserTask? = null
    private var inFlight = false
    private var userId : Long? = null

    {
        bsyncTaskManager = BsyncTaskManager(null)
        bsyncTaskManager!!.register(this)
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
        this.userId = userId
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super<Fragment>.onActivityCreated(savedInstanceState)
        if(inFlight)
            return
        if(findUserTask != null)
            deliverResults()
        else
        {
            callTask(userId!!)
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