package co.touchlab.profilephotoeditor;

import android.hardware.Camera;

/**
 * Created by kgalligan on 8/3/14.
 */
public class CameraUtils
{
    public static boolean hasFrontFacingCamera()
    {
        try
        {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();

            for (int camIdx = 0; camIdx < cameraCount; camIdx++)
            {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return false;
    }
}
