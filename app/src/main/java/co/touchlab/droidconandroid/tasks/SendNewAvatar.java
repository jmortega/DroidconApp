package co.touchlab.droidconandroid.tasks;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import co.touchlab.android.threading.tasks.Task;
import co.touchlab.android.threading.tasks.TaskQueue;

/**
 * Created by kgalligan on 8/3/14.
 */
public class SendNewAvatar extends Task
{
    private String path;
    private int width;

    public SendNewAvatar(Application context, String path, int width)
    {
        this.path = path;
        this.width = width;
    }

    @Override
    public void run(Context context) throws Exception
    {
        /*try
        {
            String scaledPath = path;

            try
            {
                Bitmap scaledBitmap = scaleBitmap(path);
                File scaledFile = new File(path.replace(".jpg", "_scaled.jpg"));

                FileOutputStream out = new FileOutputStream(scaledFile);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.close();

                scaledPath = scaledFile.getPath();
            }
            catch (Throwable e)
            {
                throw new RuntimeException(e);
            }

            Customer customer = NetworkCalls.checkUserInfoLoaded(getContext()).customer;
            JSONObject json = NetworkCalls.userUploadAvatar(getContext(), customer, scaledPath);

            String filename = json.getString("filename");
            String url = json.getString("url");
            String key = filename + ".jpg";

            JSONObject fields = json.getJSONObject("fields");

            String awsAccessKeyId = fields.getString("AWSAccessKeyId");
            String policy = fields.getString("policy");
            String signature = fields.getString("signature");
            String Secure = fields.getString("Secure");
            String acl = fields.getString("acl");
            String contentType = fields.getString("Content-Type");

            File scaledFile = new File(scaledPath);

            NetworkCalls.uploadAvatarMultipart(url, key, awsAccessKeyId, policy, signature, Secure, acl, contentType, new File(scaledPath));

            User user = NetworkCalls.userUpdateAvatarFilename(getContext(), key);

            sendSuccess(BroadcastKeys.USER_AVATAR_UPDATED);
            sendSuccess(BroadcastKeys.USER_INFO_LOADED);
            sendSuccess(BroadcastKeys.USER_INFO_CHANGED);
        }
        catch (CheckedConnectionException e)
        {
            sendFailure(BroadcastKeys.USER_AVATAR_UPDATED);
        }
        catch (JSONException e)
        {
            sendFailure(BroadcastKeys.USER_AVATAR_UPDATED);
        }
        catch (InterruptedException e)
        {

            sendFailure(BroadcastKeys.USER_AVATAR_UPDATED);
        }
        finally
        {
            suppressOff();
        }*/
    }

    @Override
    protected boolean handleError(Context context, Throwable e)
    {
        return false;
    }

    private Bitmap scaleBitmap(String path)
    {
        int length = width;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, length, length);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

}
