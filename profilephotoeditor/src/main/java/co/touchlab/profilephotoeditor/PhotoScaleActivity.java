package co.touchlab.profilephotoeditor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by kgalligan on 8/3/14.
 */
public class PhotoScaleActivity extends AppCompatActivity
{
	public static final String CONTENT_URI_PREFIX = "content://";

    public static final String IMAGE_PATH   = "IMAGE_PATH";
    public static final String AVATAR_PATH  = "avatarPath";

	public static final int RESULT_FAILED = 100;

    private static final int DEFAULT_MAX_SIZE = 640;

	private boolean mPhotoLoaded;
    private PhotoSortrView photoSortrView;
	private View mLoadingView;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.test_photo_activity);
//        setActionBar(getActionBar());

        photoSortrView = (PhotoSortrView) findViewById(R.id.photoSorter);
	    mLoadingView = findViewById(R.id.include_network);
//	    mLoadingView.findViewById(R.id.progress_wrapper).setVisibility(View.VISIBLE);
	    final long createdTime = System.currentTimeMillis();

        photoSortrView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                photoSortrView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

	            String filePath = getIntent().getStringExtra(IMAGE_PATH);
                new AsyncTask<String, Void, Bitmap>()
                {
					@Override
					protected Bitmap doInBackground(String... params){
						String path = params[0];
						int preferredSize = photoSortrView.getMeasuredWidth() > DEFAULT_MAX_SIZE ?
								DEFAULT_MAX_SIZE : photoSortrView.getMeasuredWidth();

						Bitmap raw;
						if (path.startsWith(CONTENT_URI_PREFIX)) {
							//If we were passed a URI path, we need to decode it from there,
							//which is probably from the network, so show a loading spinner
							publishProgress();
							raw = BitmapUtils.decodeSampledBitmapFromURI(
									PhotoScaleActivity.this, Uri.parse(path), preferredSize, preferredSize);
						} else {
							//Otherwise we just decode it locally which should be super fast
							raw =  BitmapUtils.decodeSampledBitmapFromPath(
									path, preferredSize ,preferredSize);
						}

						if(raw != null)
							return BitmapUtils.rotateBitmap(path, raw);
						else
							return null;
					}

	                @Override
	                protected void onProgressUpdate(Void... params)
	                {
		                mLoadingView.setVisibility(View.VISIBLE);
	                }

					@Override
					protected void onPostExecute(Bitmap bitmap)
					{
						if(bitmap == null) {
							// We should only get here if we don't have network and
							// we had to download the image
							Runnable exitRunnable = new Runnable() {
								@Override
								public void run() {
									setResult(RESULT_FAILED);
									onBackPressed();
								}
							};
							long elapsedTime = System.currentTimeMillis() - createdTime;
							long timeRemaining = 600 - elapsedTime;
							if(timeRemaining > 0)
								new Handler().postDelayed(exitRunnable, timeRemaining);
							else
								exitRunnable.run();
							return;
						}

						if(mLoadingView.getVisibility() == View.VISIBLE)
						{
							mLoadingView.animate().alpha(0).setListener(new AnimatorListenerAdapter() {
								@Override
								public void onAnimationEnd(Animator animation) {
									super.onAnimationEnd(animation);
									mLoadingView.setVisibility(View.GONE);
								}
							});
						}

						photoSortrView.addImage(new BitmapDrawable(getResources(), bitmap));
						photoSortrView.invalidate();
						mPhotoLoaded = true;
					}
                }.execute(filePath);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_scale, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.menu_rotate_image) {
            photoSortrView.rotateImage();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void setActionBar(ActionBar actionBar) {

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setIcon(new ColorDrawable(0));
        actionBar.setTitle(R.string.user_profile_pic_scale);
    }

    public void saveAvatar(View save)
    {
	    if(mPhotoLoaded)
	    {
	        Bitmap bitmap = null;
	        File scaledAvatar = new File(getFilesDir(), "scaledAvatar_" + System.currentTimeMillis() + ".jpg");

	        try
	        {
	            bitmap = photoSortrView.takeScreenshot();

	            FileOutputStream out = new FileOutputStream(scaledAvatar);
	            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
	            out.close();
	        }
	        catch (IOException e)
	        {
	            throw new RuntimeException(e);
	        }
	        Intent intent = new Intent();
	        intent.putExtra("avatarPath", scaledAvatar.getPath());


	        super.setResult(RESULT_OK, intent);
	        super.finish();
	    }
    }

    public static Intent callMe(Context context, String path)
    {
        Intent intent = new Intent(context, PhotoScaleActivity.class);
        intent.putExtra(IMAGE_PATH, path);
        return intent;
    }

}