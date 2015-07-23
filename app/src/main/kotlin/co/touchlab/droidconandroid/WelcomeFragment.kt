package co.touchlab.droidconandroid

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.wnafee.vector.compat.ResourcesCompat

/**
 *
 * Created by izzyoji :) on 7/22/15.
 */
class WelcomeFragment : Fragment() {


    companion object
    {
        private val BACKGROUND_COLOR_RES = "background_color_res"
        private val IMAGE_RES = "image_res"
        private val TEXT_COLOR_RES = "text_color_res"
        private val TITLE_RES = "title_res"
        private val DESC_RES = "desc_res"

        fun newInstance(backgroundColorRes: Int, imageRes: Int, textColorRes: Int, titleRes: Int, descRes: Int): WelcomeFragment
        {
            val fragment = WelcomeFragment()
            val args = Bundle()
            args.putInt(BACKGROUND_COLOR_RES, backgroundColorRes)
            args.putInt(IMAGE_RES, imageRes)
            args.putInt(TEXT_COLOR_RES, textColorRes)
            args.putInt(TITLE_RES, titleRes)
            args.putInt(DESC_RES, descRes)
            fragment.setArguments(args)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return  inflater!!.inflate(R.layout.fragment_welcome, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val bundle = getArguments()
        getView().setBackgroundColor(getResources().getColor(bundle.getInt(BACKGROUND_COLOR_RES)))

        val root = getView()
        val image = root.findViewById(R.id.image)!! as ImageView
        val imageRes = bundle.getInt(IMAGE_RES)
        image.setImageDrawable(ResourcesCompat.getDrawable(getActivity(), imageRes))


        val titleTV = root.findViewById(R.id.title)!! as TextView
        titleTV.setText(bundle.getInt(TITLE_RES))
        titleTV.setTextColor(getResources().getColor(bundle.getInt(TEXT_COLOR_RES)))

        val descTV = root.findViewById(R.id.description)!! as TextView
        descTV.setText(bundle.getInt(DESC_RES))
        descTV.setTextColor(getResources().getColor(bundle.getInt(TEXT_COLOR_RES)))

    }
}
