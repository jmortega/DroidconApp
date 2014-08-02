package co.touchlab.droidconandroid

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.LoaderManager.LoaderCallbacks
import co.touchlab.android.threading.loaders.networked.DoubleTapResult
import co.touchlab.droidconandroid.data.UserAccount
import android.support.v4.content.Loader
import co.touchlab.android.threading.loaders.networked.DoubleTapResult.Status
import co.touchlab.droidconandroid.utils.Toaster
import co.touchlab.droidconandroid.data.AppPrefs
import android.widget.TextView
import android.widget.ImageView
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import android.graphics.Bitmap
import android.graphics.Color
import android.content.Intent
import android.support.v4.app.FragmentActivity

/**
 * Created by kgalligan on 8/1/14.
 */
class MyProfileActivity : FragmentActivity()
{
    class object
    {
        fun callMe(a: Activity)
        {
            val i = Intent(a, javaClass <MyProfileActivity> ())
            a.startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<FragmentActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        getSupportLoaderManager()!!.initLoader(0, null, this.UDLoaderCallbacks())
    }

    fun showDetail(user: UserAccount)
    {
        val qrwriter = QRCodeWriter()
        val bitMatrix = qrwriter.encode("user_" + user.userCode, BarcodeFormat.QR_CODE, 600, 600)

        (findView(R.id.userCode) as TextView).setText(user.userCode)
        (findView(R.id.userName) as TextView).setText(user.name)
        (findView(R.id.qrCode) as ImageView).setImageBitmap(toBitmap(bitMatrix!!))
    }

    fun toBitmap(matrix: BitMatrix): Bitmap
    {
        val height = matrix.getHeight()
        val width = matrix.getWidth()
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)!!
        for (x in 0..(width - 1))
        {
            for (y in 0..(height - 1))
            {
                bmp.setPixel(x, y, if (matrix.get(x, y)) Color.BLACK else Color.WHITE);
            }
        }
        return bmp;
    }


    inner class UDLoaderCallbacks() : LoaderCallbacks<DoubleTapResult<UserAccount, Int>>
    {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<DoubleTapResult<UserAccount, Int>>?
        {
            return UserDetailLoader(this@MyProfileActivity, AppPrefs.getInstance(this@MyProfileActivity).getUserId()!!)
        }
        override fun onLoadFinished(loader: Loader<DoubleTapResult<UserAccount, Int>>?, data: DoubleTapResult<UserAccount, Int>?)
        {
            val userAccount = data!!.getResult()
            if (userAccount != null)
            {
                showDetail(userAccount)
            }
        }
        override fun onLoaderReset(loader: Loader<DoubleTapResult<UserAccount, Int>>?)
        {

        }
    }
}