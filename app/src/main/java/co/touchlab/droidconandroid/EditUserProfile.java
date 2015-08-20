package co.touchlab.droidconandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import co.touchlab.android.threading.eventbus.EventBusExt;
import co.touchlab.android.threading.tasks.TaskQueue;
import co.touchlab.droidconandroid.data.AppPrefs;
import co.touchlab.droidconandroid.data.UserAccount;
import co.touchlab.droidconandroid.superbus.QuickClearAvatarTask;
import co.touchlab.droidconandroid.superbus.UploadAvatarCommand;
import co.touchlab.droidconandroid.tasks.GrabUserProfile;
import co.touchlab.droidconandroid.tasks.Queues;
import co.touchlab.droidconandroid.tasks.UpdateUserProfileTask;
import co.touchlab.droidconandroid.tasks.persisted.PersistedTaskQueueFactory;
import co.touchlab.droidconandroid.utils.Toaster;
import co.touchlab.profilephotoeditor.BitmapUtils;
import co.touchlab.profilephotoeditor.CameraUtils;
import co.touchlab.profilephotoeditor.PhotoScaleActivity;

public class EditUserProfile extends StickyTaskManagerActivity
{
    public static final String HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES = "https://s3.amazonaws.com/droidconimages/";
    private EditText  name;
    private TextView  userCode;
    private EditText  company;
    private EditText  facebook;
    private EditText  twitter;
    private EditText  linkedIn;
    private EditText  gPlus;
    private EditText  website;
    private EditText  phone;
    private EditText  email;
    private EditText  bio;
    private ImageView myPic;
    private CheckBox  shareEmail;

    public static void callMe(Context c)
    {
        Intent i = new Intent(c, EditUserProfile.class);
        c.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        if(savedInstanceState == null)
        {
            refreshProfile();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_edit_user_profile);
        toolbar.setNavigationIcon(R.drawable.ic_action_tick);
        setSupportActionBar(toolbar);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        userCode = (TextView) findViewById(R.id.myUserCode);
        company = (EditText) findViewById(R.id.company);
        facebook = (EditText) findViewById(R.id.facebook);
        twitter = (EditText) findViewById(R.id.twitter);
        linkedIn = (EditText) findViewById(R.id.linkedIn);
        gPlus = (EditText) findViewById(R.id.gPlus);
        website = (EditText) findViewById(R.id.website);
        phone = (EditText) findViewById(R.id.phone);
        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        bio = (EditText) findViewById(R.id.bio);
        myPic = (ImageView) findViewById(R.id.profile_image);
        shareEmail = (CheckBox) findViewById(R.id.public_email);

        EventBusExt.getDefault().register(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            validateChanges();

        }
        return super.onOptionsItemSelected(item);
    }

    private void validateChanges()
    {
        String phoneString = getStringFromEditText(phone);
        String emailString = getStringFromEditText(email);
        String nameString = getStringFromEditText(name);
        String twitterString = getStringFromEditText(twitter);

        while(twitterString.startsWith("@"))
        {
            twitterString = twitterString.substring(1);
            twitter.setText(twitterString);
        }

        if(TextUtils.isEmpty(nameString))
        {
            Toast.makeText(this, R.string.error_name, Toast.LENGTH_SHORT).show();
        }
        else if(!TextUtils.isEmpty(phoneString) && PhoneNumberUtils.isGlobalPhoneNumber(phoneString))
        {
            Toast.makeText(this, R.string.error_phone, Toast.LENGTH_SHORT).show();
        }
        else if(!TextUtils.isEmpty(emailString) && !Patterns.EMAIL_ADDRESS.matcher(getStringFromEditText(email)).matches())
        {
            Toast.makeText(this, R.string.error_email, Toast.LENGTH_SHORT).show();
        }
        else
        {
            saveProfile();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBusExt.getDefault().unregister(this);
    }

    public void onEventMainThread(UploadAvatarCommand command)
    {
        refreshProfile();
    }

    private void refreshProfile()
    {
        AppPrefs appPrefs = AppPrefs.getInstance(this);
        Queues.localQueue(this).execute(new GrabUserProfile(appPrefs.getUserId()));
    }

    private void saveProfile()
    {
        Queues.localQueue(this).execute(
                new UpdateUserProfileTask(this, getStringFromEditText(name),
                                          getStringFromEditText(bio),
                                          getStringFromEditText(company),
                                          getStringFromEditText(twitter),
                                          getStringFromEditText(linkedIn),
                                          getStringFromEditText(website),
                                          getStringFromEditText(facebook),
                                          getStringFromEditText(phone),
                                          getStringFromEditText(email),
                                          getStringFromEditText(gPlus),
                                          shareEmail.isChecked()));
        finish();
    }

    @NonNull
    private String getStringFromEditText(EditText editText)
    {
        return editText.getText().toString();
    }

    public void onEventMainThread(GrabUserProfile grabUserProfile)
    {
        profile(grabUserProfile.userAccount);
    }

    public void profile(UserAccount ua)
    {
        //This is a little shitty.  Assuming an empty code means we haven't
        //done the initial data set.  Updates should only be coming from
        //avatar uploads
        if(StringUtils.isEmpty(userCode.getText()))
        {
            name.setText(ua.name);
            email.setText(ua.email);
            phone.setText(ua.phone);
            company.setText(ua.company);
            facebook.setText(ua.facebook == null ? "" : ua.facebook);
            twitter.setText(ua.twitter);
            linkedIn.setText(ua.linkedIn);
            gPlus.setText(ua.gPlus);
            website.setText(ua.website);
            userCode.setText(ua.userCode);
            bio.setText(ua.profile);
            shareEmail.setChecked(ua.emailPublic == null
                                          ? false
                                          : ua.emailPublic);
        }

        if (!TextUtils.isEmpty(ua.avatarKey))
        {
            Picasso.with(this).load(HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES + ua.avatarKey).into(myPic);
        }
        else
        {
            myPic.setImageResource(R.drawable.profile_placeholder);
        }

        myPic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callPhotoGrabber();
            }
        });
    }

    private void callPhotoGrabber()
    {
        new AlertDialog.Builder(this).setMessage("The thing and stuff")
                .setPositiveButton("Camera", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        startCamera();
                    }
                })
                .setNegativeButton("Gallery", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        startGalleryPicker();
                    }
                }).show();
    }

    public static final int PHOTO_SELECT = 100;
    public static final int PHOTO_CAPTURE = 101;
    public static final int PHOTO_EDIT_COMPLETE = 102;
    public static final String GALLERY_CONTENT_URI_PREFIX = "content://media/";
    private String photoPath;

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

                            Cursor cursor = getContentResolver().query(selectedImage,
                                                                       filePathColumn, null, null,
                                                                       null);
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
                    String cameraPhotoPath = cameraPhotoPath();
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
                    onDialogHide();
                    final String avatarPath = intent.getStringExtra("avatarPath");
                    photoEditComplete(avatarPath);
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

    public void onDialogHide()
    {

    }

    public String cameraPhotoPath()
    {
        String path = photoPath;
        photoPath = null;
        return path;
    }

    public void photoEditComplete(String path)
    {
        Queues.localQueue(this).execute(
                new QuickClearAvatarTask(AppPrefs.getInstance(this).getUserId()));
        refreshProfile();
        PersistedTaskQueueFactory.getInstance(this).execute(new UploadAvatarCommand(path));
        Toaster.showMessage(this, "Photo updating.  May take a bit...");
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
            Intent intent = PhotoScaleActivity.callMe(EditUserProfile.this, file.getPath());
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
