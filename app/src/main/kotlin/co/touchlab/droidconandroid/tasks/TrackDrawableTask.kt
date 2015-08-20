package co.touchlab.droidconandroid.tasks

import android.content.Context
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.Log
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.Task
import co.touchlab.droidconandroid.R
import com.crashlytics.android.Crashlytics
import com.wnafee.vector.compat.ResourcesCompat

/**
 * Created by samuelhill on 8/20/15.
 */
class TrackDrawableTask(val context: Context, val drawableRes: Int): Task()
{
    var drawable: Drawable? = null
    override fun run(context: Context?) {
        drawable = ResourcesCompat.getDrawable(context, drawableRes)
        drawable!!.setColorFilter(PorterDuffColorFilter(context!!.getResources().getColor(R.color.black_40), PorterDuff.Mode.DARKEN) as ColorFilter)
        EventBusExt.getDefault()!!.post(this);
    }

    override fun handleError(context: Context?, e: Throwable?): Boolean {
        Log.e("EventDetails", "Error loading track drawables", e)
        Crashlytics.logException(e)
        return true
    }

}