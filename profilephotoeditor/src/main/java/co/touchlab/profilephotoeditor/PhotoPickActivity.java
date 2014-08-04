package co.touchlab.profilephotoeditor;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by kgalligan on 8/3/14.
 */
public class PhotoPickActivity extends Activity
{
    public static final int PHOTO_SELECT = 100;
    public static final int PHOTO_CAPTURE = 101;
    public static final int PHOTO_EDIT_COMPLETE = 102;
    public static final String GALLERY_CONTENT_URI_PREFIX = "content://media/";
    private String photoPath;

    public interface UserProfileCallback
    {
        void onDialogShow();

        void onDialogClose();

        void onDialogHide();

        void startCamera();

        String cameraPhotoPath();

        void photoEditComplete(String path);

    }

    private UserProfileCallback mCallback;
    private Uri imageURI;

    public void setImageURI(Uri imageURI)
    {
        this.imageURI = imageURI;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode)
        {
            case PHOTO_SELECT:
                if (resultCode == Activity.RESULT_OK)
                {
                    boolean pickSuccess = false;

                    Uri selectedImage = intent.getData();
                    if (selectedImage != null)
                    {
                        String filePath = selectedImage.toString();
                        //We can only actually resolve to a file path if
                        //our URI looks like a gallery media content URI
                        if (filePath.startsWith(GALLERY_CONTENT_URI_PREFIX))
                        {
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};

                            Cursor cursor = getContentResolver().query(
                                    selectedImage, filePathColumn, null, null, null);
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            filePath = cursor.getString(columnIndex);
                            cursor.close();
                        }

                        if (TextUtils.isEmpty(filePath))
                        {
                            showErrorMessage(R.string.error_picture_select);
                        }
                        else
                        {
                            Intent photoEditIntent = PhotoScaleActivity.callMe(this, filePath);
                            startActivityForResult(photoEditIntent, PHOTO_EDIT_COMPLETE);
                        }

                    }
                }
                break;
            case PHOTO_CAPTURE:
                if (resultCode == Activity.RESULT_OK)
                {
                    Bitmap bitmap;
                    String cameraPhotoPath = mCallback.cameraPhotoPath();
                    File cameraPhotoFile = cameraPhotoPath != null ? new File(cameraPhotoPath) : null;

                    if (cameraPhotoFile != null && cameraPhotoFile.exists())
                    {
                        bitmap = BitmapUtils.decodeSampledBitmapFromPath(cameraPhotoPath, 700, 700);
                        bitmap = BitmapUtils.rotateBitmap(cameraPhotoPath, bitmap);
                    }
                    else if (intent != null && intent.getExtras().containsKey("data"))
                    {
                        Bundle extras = intent.getExtras();
                        bitmap = (Bitmap) extras.get("data");
                    }
                    else
                    {
                        bitmap = getBitmapFromUri();
                    }
                    new SaveBitmapTask().execute(bitmap);
                }
                break;
            case PHOTO_EDIT_COMPLETE:
                if (resultCode == Activity.RESULT_OK)
                {
                    mCallback.onDialogHide();
                    final String avatarPath = intent.getStringExtra("avatarPath");
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            // Loading screen is inside of handler because this case gets called
                            // during the transition from MoveScaleAct and UserProfileActivity. We
                            // slap on 300 seconds to let this transition to finish, then show
                            // the loader. The SendNewAvatar runnable has a delay of 600ms before
                            // so the loading screen has enough visibility time.
//                            showLoadingScreen(true);
                            mCallback.photoEditComplete(avatarPath);
                        }
                    }, 300);
                }
                else if (resultCode == PhotoScaleActivity.RESULT_FAILED)
                {
                    showErrorMessage(R.string.error_picture_select);
                }
                break;
        }
    }

    public void startCamera()
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (CameraUtils.hasFrontFacingCamera())
            {
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_FRONT);
            }

            String type = Environment.DIRECTORY_PICTURES;
            File path = Environment.getExternalStoragePublicDirectory(type);

            File filePhoto = new File(path, "avatar_" + System.currentTimeMillis() + ".jpg");
            photoPath = filePhoto.getPath();

            Uri imageUri = Uri.fromFile(filePhoto);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            setImageURI(imageUri);
            startActivityForResult(takePictureIntent, PHOTO_CAPTURE);
        }
        else
        {
            Toast.makeText(this, "SD card not mounted", Toast.LENGTH_LONG).show();
        }
    }

    public String cameraPhotoPath()
    {
        String path = photoPath;
        photoPath = null;
        return path;
    }

    public void startGalleryPicker()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PHOTO_SELECT);
    }

    private class SaveBitmapTask extends AsyncTask<Bitmap, Void, File>
    {
        @Override
        protected File doInBackground(Bitmap... params)
        {
            try
            {
                File avatarFile = new File(getFilesDir(), "avatar_" + System.currentTimeMillis() + ".jpg");
                FileOutputStream out = new FileOutputStream(avatarFile);
                params[0].compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.close();

                return avatarFile;
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(File file)
        {
            Intent intent = PhotoScaleActivity.callMe(PhotoPickActivity.this, file.getPath());
            startActivityForResult(intent, PHOTO_EDIT_COMPLETE);
        }
    }

    public Bitmap getBitmapFromUri()
    {
        getContentResolver().notifyChange(imageURI, null);
        ContentResolver cr = getContentResolver();
        try
        {

            return android.provider.MediaStore.Images.Media.getBitmap(cr, imageURI);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private void showErrorMessage(int messageId)
    {
        Toast.makeText(this, messageId, Toast.LENGTH_LONG).show();
    }
}
