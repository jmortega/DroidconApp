package co.touchlab.droidconandroid.superbus

import android.content.Context
import android.util.Log
import co.touchlab.android.threading.eventbus.EventBusExt
import co.touchlab.android.threading.tasks.Task
import co.touchlab.android.threading.tasks.helper.RetrofitPersistedTask
import co.touchlab.android.threading.tasks.persisted.PersistedTask
import co.touchlab.droidconandroid.BuildConfig
import co.touchlab.droidconandroid.R
import co.touchlab.droidconandroid.data.AppPrefs
import co.touchlab.droidconandroid.data.DatabaseHelper
import co.touchlab.droidconandroid.network.dao.UserInfoResponse
import co.touchlab.droidconandroid.tasks.AbstractFindUserTask
import com.crashlytics.android.Crashlytics
import com.google.gson.Gson
import com.turbomanage.httpclient.BasicHttpClient
import org.apache.commons.io.IOUtils
import java.io.FileInputStream

/**
 * Created by kgalligan on 7/20/14.
 */
open class UploadAvatarCommand(val imageURL: String? = null) : RetrofitPersistedTask() {
    override fun logSummary(): String {
        return "imageURL: " + imageURL
    }

    override fun same(command: PersistedTask): Boolean {
        return false
    }

    override fun runNetwork(context: Context?) {
        var body: ByteArray

        if (imageURL!!.startsWith("http")) {
            val client = BasicHttpClient("")
            val response = client.get(imageURL, null)
//            client.checkAndThrowError()
            body = response!!.getBody()!!
        } else {
            val inp = FileInputStream(imageURL)
            body = IOUtils.toByteArray(inp)!!
            inp.close()
        }

        val uuid = AppPrefs.getInstance(context).getUserUuid()
        val postClient = BasicHttpClient(BuildConfig.BASE_URL)
        postClient.addHeader("uuid", uuid);
        val uploadResponse = postClient.post("dataTest/uploadAvatar", "image/jpeg", body)
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

/**
 * Remove reference to the avatar image.  For use after uploading new avatar.
 */
class QuickClearAvatarTask(val userId: Long) : Task() {
    override fun handleError(context: Context?, e: Throwable?): Boolean {
        return false
    }

    override fun run(context: Context?) {
        val dao = DatabaseHelper.getInstance(context).getUserAccountDao()
        val userAccount = dao.queryForId(userId)
        userAccount!!.avatarKey = null;
        AppPrefs.getInstance(context).setAvatarKey(null)
        dao.createOrUpdate(userAccount)

    }
}