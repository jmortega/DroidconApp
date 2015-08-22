package co.touchlab.droidconandroid.utils

import android.content.Context
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter

/**
 * Created by kgalligan on 7/27/14.
 */
class CacheHelper
{
    companion object
    {
        synchronized fun findFile(c: Context, name: String): String?
        {
            val file = locateFile(c, name)
            if (file.exists() && file.length() > 0)
            {
                val fileInputStream = FileInputStream(file)
                try
                {
                    val lines = IOUtils.readLines(fileInputStream)
                    return StringUtils.join(lines, "\n")
                }
                finally
                {
                    fileInputStream.close()
                }
            }

            return null;
        }


        synchronized fun saveFile(c: Context, name: String, data: String)
        {
            val file = locateFile(c, name)
            val tempFile = File(file.getParent(), file.getName() + ".tmp")
            if(tempFile.exists())
                tempFile.delete()
            val out = FileWriter(tempFile)
            IOUtils.write(data, out)
            out.close()
            if(file.exists())
                file.delete()
            tempFile.renameTo(file)
        }

        private fun locateFile(c: Context, name: String) : File
        {
            val filesDir = c.getFilesDir()
            return File(filesDir, name)
        }
    }
}