package co.touchlab.droidconandroid.tasks

import co.touchlab.android.threading.tasks.BsyncTask

/**
 * Created by kgalligan on 7/21/14.
 */
abstract class LiveNetworkBsyncTaskKot<D> : BsyncTask<D>()
{
    var errorStringCode : Int? = null

    fun isError() : Boolean
    {
        return errorStringCode != null
    }
}