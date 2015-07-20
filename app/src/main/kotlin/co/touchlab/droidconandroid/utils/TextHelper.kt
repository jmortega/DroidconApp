package co.touchlab.droidconandroid.utils

import org.apache.commons.lang3.StringUtils

/**
 * Created by kgalligan on 7/27/14.
 */
class TextHelper
{
    companion object
    {
        fun findTagLinks(s: String): String
        {
            if(StringUtils.isEmpty(s))
                return ""

            val regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
            val result = s.replace(regex.toRegex(), { match -> "<a href='" + match + "'>" + match + "</a>" })
            return result
        }
    }
}