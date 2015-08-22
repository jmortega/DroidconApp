package co.touchlab.droidconandroid.tasks

import android.content.Context
import co.touchlab.android.threading.tasks.Task

/**
 * Created by kgalligan on 8/22/15.
 */
class UserSearchTask(val search: String): Task()
{
    override fun handleError(context: Context?, e: Throwable?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun run(context: Context?) {
        throw UnsupportedOperationException()
    }

}