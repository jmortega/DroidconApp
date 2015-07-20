package co.touchlab.droidconandroid

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.LoaderManager.LoaderCallbacks
import co.touchlab.android.threading.loaders.networked.DoubleTapResult
import co.touchlab.droidconandroid.data.UserAccount
import android.support.v4.content.Loader
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
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View

/**
 * Created by kgalligan on 8/1/14.
 */
class MyProfileFragment : Fragment()
{
    companion object
    {
        fun newInstance(): MyProfileFragment
        {
            return MyProfileFragment()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super<Fragment>.onActivityCreated(savedInstanceState)
        getLoaderManager()!!.initLoader(0, null, this.UDLoaderCallbacks())
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater!!.inflate(R.layout.activity_my_profile, null)
        //Probably putting stuff here...
        return view
    }

    fun showDetail(user: UserAccount)
    {
        val qrwriter = QRCodeWriter()
        val bitMatrix = qrwriter.encode("user_" + user.userCode, BarcodeFormat.QR_CODE, 600, 600)

        val view = getView()!!
        (view.findView(R.id.userCode) as TextView).setText(user.userCode)
        (view.findView(R.id.userName) as TextView).setText(user.name)
        (view.findView(R.id.qrCode) as ImageView).setImageBitmap(toBitmap(bitMatrix!!))
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
            return UserDetailLoader(getActivity()!!, AppPrefs.getInstance(getActivity()).getUserId()!!)
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