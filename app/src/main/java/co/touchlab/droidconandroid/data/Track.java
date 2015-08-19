package co.touchlab.droidconandroid.data;
import android.text.TextUtils;

import co.touchlab.droidconandroid.R;

/**
 * Created by izzyoji :) on 8/9/15.
 */
public enum Track
{
    DEVELOPMENT("Develop", R.string.development, R.color.droidcon_blue, R.color.selector_blue),
    DESIGN("Design", R.string.design, R.color.droidcon_pink, R.color.selector_pink),
    BUSINESS("Business", R.string.business, R.color.orange, R.color.selector_orange);

    public String getServerName()
    {
        return serverName;
    }

    public int getDisplayNameRes()
    {
        return displayNameRes;
    }

    public int getCheckBoxSelectorRes()
    {
        return checkBoxSelectorRes;
    }

    public int getTextColorRes()
    {
        return textColorRes;
    }

    String serverName;
    int    displayNameRes;
    int    textColorRes;
    int    checkBoxSelectorRes;

    Track(String serverName, int displayNameRes, int textColor, int checkBoxSelector)
    {
        this.serverName = serverName;
        this.displayNameRes = displayNameRes;
        this.textColorRes = textColor;
        this.checkBoxSelectorRes = checkBoxSelector;
    }

    public static Track findByServerName(String serverName)
    {
        for(Track track : values())
        {
            if(TextUtils.equals(track.serverName, serverName))
            {
                return track;
            }
        }
        return null;
    }
}
