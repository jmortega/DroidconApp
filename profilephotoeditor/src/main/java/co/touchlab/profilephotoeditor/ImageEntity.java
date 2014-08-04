package co.touchlab.profilephotoeditor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by kgalligan on 8/3/14.
 */
public class ImageEntity extends MultiTouchEntity
{

    private transient Drawable mDrawable;

    public ImageEntity(View parent, Drawable drawable, Resources res)  {
        super(parent, res);

        mDrawable = drawable;
    }

    public ImageEntity(View parent, ImageEntity e, Resources res) {
        super(parent, res);

        mDrawable = e.mDrawable;
        mScaleX = e.mScaleX;
        mScaleY = e.mScaleY;
        mCenterX = e.mCenterX;
        mCenterY = e.mCenterY;
        mAngle = e.mAngle;
        mViewHeight = e.mViewHeight;
        mViewWidth = e.mViewWidth;
    }

    public void draw(Canvas canvas) {
        canvas.save();

        float dx = (mMaxX + mMinX) / 2;
        float dy = (mMaxY + mMinY) / 2;

        mDrawable.setBounds((int) mMinX, (int) mMinY, (int) mMaxX, (int) mMaxY);
        canvas.rotate(mAngle * 360, dx, dy);
        mDrawable.draw(canvas);

        canvas.restore();
    }

    /**
     * Called by activity's onPause() method to free memory used for loading the images
     */
    @Override
    public void unload() {
        this.mDrawable = null;
    }

    /** Called by activity's onResume() method to load the images */
    @Override
    public void load(Context context, float startMidX, float startMidY) {
        Resources res = context.getResources();
        getMetrics(res);

        mStartMidX = startMidX;
        mStartMidY = startMidY;

        mWidth = mDrawable.getIntrinsicWidth();
        mHeight = mDrawable.getIntrinsicHeight();

        float centerX;
        float centerY;
        float scaleX;
        float scaleY;
        float angle;

        if (mFirstLoad) {
            centerX = startMidX;
            centerY = startMidY;

            float scaleFactor;
            if (mWidth > mHeight){
                scaleFactor = (float) mViewHeight / mHeight;
            } else {
                scaleFactor = (float) mViewWidth / mWidth;
            }

            scaleX = scaleY = scaleFactor;
            mOrigionalScale = scaleFactor;
            mMaxScale = mOrigionalScale * MAX_SCALE;

            angle = 0.0f;

            mFirstLoad = false;
        } else {
            centerX = mCenterX;
            centerY = mCenterY;
            scaleX = mScaleX;
            scaleY = mScaleY;
            angle = mAngle;
        }

        setPos(centerX, centerY, scaleX, scaleY, angle);
    }
}