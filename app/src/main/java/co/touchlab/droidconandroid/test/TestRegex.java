package co.touchlab.droidconandroid.test;

import co.touchlab.droidconandroid.utils.TextHelper;

/**
 * Created by kgalligan on 7/27/14.
 */
public class TestRegex
{
    public static void main(String[] asdf)
    {
        String testString = "He does things. http://touchlab.co and http://kgalligan.com";
        String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
//        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        String result = testString.replaceAll(regex, "balls");
        System.out.println(result);
    }
}
