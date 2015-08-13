package co.touchlab.droidconandroid.superbus

import android.content.Context
import android.util.Log
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.helper.RetrofitPersistedTask
import co.touchlab.android.threading.tasks.persisted.PersistedTask
import co.touchlab.droidconandroid.BuildConfig
import co.touchlab.droidconandroid.R
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.network.dao.UserInfoResponse
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask
import com.crashlytics.android.Crashlytics
import com.google.gson.Gson
import com.turbomanage.httpclient.BasicHttpClient

/**
 *
 * Created by izzyoji :) on 8/4/15.
 */
open class UploadCoverCommand(val coverURL: String? = null) : RetrofitPersistedTask() {
    override fun logSummary(): String {
        return "coverURL: " + coverURL
    }

    override fun same(command: PersistedTask): Boolean {
        return false
    }

    override fun runNetwork(context: Context?) {
        var body: ByteArray

        val client = BasicHttpClient("")
        val response = client.get(coverURL, null)
//        client.checkAndThrowError()
        body = response!!.getBody()!!

        val uuid = AppPrefs.getInstance(context).getUserUuid()
        val postClient = BasicHttpClient(BuildConfig.BASE_URL)
        postClient.addHeader("uuid", uuid);
        val uploadResponse = postClient.post("dataTest/uploadCover", "image/jpeg", body)
//        postClient.checkAndThrowError()

        val userResponseString = uploadResponse?.getBodyAsString() ?: throw RuntimeException("No user response")
        val gson = Gson()
        val userInfoResponse = gson.fromJson(userResponseString, javaClass<UserInfoResponse>())
        AbstractFindUserTask.saveUserResponse(context!!, null, userInfoResponse!!)

        EventBusExt.getDefault()!!.post(this)
    }

    override fun handleError(context: Context?, e: Throwable?): Boolean {
        Log.w("asdf", "Whoops", e);
        Crashlytics.logException(e);
        return true;
    }
}
