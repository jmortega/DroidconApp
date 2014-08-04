package co.touchlab.profilephotoeditor;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;

import java.io.Serializable;

/**
 * Created by kgalligan on 8/3/14.
 */
public abstract class MultiTouchEntity implements Serializable
{

    protected boolean mFirstLoad = true;

    protected final int MAX_SCALE = 4;

    protected transient Paint mPaint = new Paint();

    protected int mWidth;
    protected int mHeight;

    // width/height of screen
    protected int mDisplayWidth;
    protected int mDisplayHeight;

    // width/height of view
    protected int mViewWidth;
    protected int mViewHeight;


    protected float mCenterX;
    protected float mCenterY;
    protected float mScaleX;
    protected float mScaleY;
    protected float mAngle;

    protected float mOrigionalScale;
    protected float mMaxScale;

    protected float mMinX;
    protected float mMaxX;
    protected float mMinY;
    protected float mMaxY;

    // area of the entity that can be scaled/rotated
    // using single touch (grows from bottom right)
    protected final static int GRAB_AREA_SIZE = 40;
    protected boolean mIsGrabAreaSelected = false;
    protected boolean mIsLatestSelected = false;

    protected float mGrabAreaX1;
    protected float mGrabAreaY1;
    protected float mGrabAreaX2;
    protected float mGrabAreaY2;

    protected float mStartMidX;
    protected float mStartMidY;

	private static final int UI_MODE_ROTATE = 1;
    private static final int UI_MODE_ANISOTROPIC_SCALE = 2;
    protected int mUIMode = UI_MODE_ROTATE;
    private int ngle;

    public MultiTouchEntity(View parent) {
        mViewHeight = parent.getMeasuredHeight();
        mViewWidth = parent.getMeasuredWidth();
    }

    public MultiTouchEntity(View parent, Resources res) {
        this(parent);
        getMetrics(res);
    }

    protected void getMetrics(Resources res) {
        DisplayMetrics metrics = res.getDisplayMetrics();
        mDisplayWidth =
            (res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                ? Math.max(metrics.widthPixels, metrics.heightPixels)
                : Math.min(metrics.widthPixels, metrics.heightPixels);
        mDisplayHeight =
            (res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                ? Math.min(metrics.widthPixels, metrics.heightPixels)
                : Math.max(metrics.widthPixels, metrics.heightPixels);
    }

    /**
     * Set the position and scale of an image in screen coordinates
     */
    public boolean setPos (MultiTouchController.PositionAndScale newImgPosAndScale) {

        //Adjust scale so that the image doesn't scale down (past its original scaled value)
        float newScale = newImgPosAndScale.getScale();

        if (newScale < mOrigionalScale){
            newScale = mOrigionalScale;
        } else if (newScale > mMaxScale){
            newScale = mMaxScale;
        }

        //Adjust centerX so that the image fill the entire the view
        float newXOffset = calculateAxisOffset(getRotatedWidth(), mViewWidth,
                mCenterX, newImgPosAndScale.getXOff(), newScale);

        //Adjust centerY so that the image fill the entire the view
        float newYOffset = calculateAxisOffset(getRotatedHeight(), mViewHeight,
                mCenterY, newImgPosAndScale.getYOff(), newScale);

        return setPos(newXOffset,
                      newYOffset,
                      newScale,
                      newScale,
                      newImgPosAndScale.getAngle());
    }

    public float calculateAxisOffset(float drawableMeasurement, float viewMeasurement,
                                     float currentOffset, float axisOffset, float scale){

        float halfAxis = (drawableMeasurement/2) * scale;
        float newStartBound = axisOffset - halfAxis;
        float newEndBound = axisOffset + halfAxis;

        float newOffset = (newStartBound <= 0 && newEndBound >= viewMeasurement) ?
                axisOffset : currentOffset;

        if (newOffset - halfAxis > 0 ){
            float adjustment = newOffset - halfAxis ;
            newOffset -=  adjustment;
        } else if (newOffset + halfAxis < viewMeasurement){
            float adustment = viewMeasurement - (newOffset + halfAxis);
            newOffset +=  adustment;
        }

        return newOffset;

    }


    public void setAngle(float angle) {
        mAngle = angle;

        //Adjust centerX so that the image fill the entire the view
        float adjustedXOffset = calculateAxisOffset(getRotatedWidth(), mViewWidth,
                mCenterX, mCenterX, mScaleX);

        //Adjust centerY so that the image fill the entire the view
        float adjustedYOffset = calculateAxisOffset(getRotatedHeight(), mViewHeight,
                mCenterY, mCenterX, mScaleY);

        setPos(adjustedXOffset, adjustedYOffset, mScaleX, mScaleY, mAngle);
    }

    private int getRotatedWidth(){
        return (mAngle == 0 || mAngle == .5f) ? mWidth : mHeight;
    }

    private int getRotatedHeight(){
        return (mAngle == 0 || mAngle == .5f) ? mHeight : mWidth;
    }

    /**
     * Set the position and scale of an image in screen coordinates
     */
    protected boolean setPos(float centerX, float centerY,
                             float scaleX, float scaleY, float angle) {

        float ws = (mWidth/2) * scaleX;
        float hs = (mHeight/2) * scaleY;

        mMinX = centerX - ws;
        mMinY = centerY - hs;
        mMaxX = centerX + ws;
        mMaxY = centerY + hs;

        mGrabAreaX1 = mMaxX - GRAB_AREA_SIZE;
        mGrabAreaY1 = mMaxY - GRAB_AREA_SIZE;
        mGrabAreaX2 = mMaxX;
        mGrabAreaY2 = mMaxY;

        mCenterX = centerX;
        mCenterY = centerY;
        mScaleX = scaleX;
        mScaleY = scaleY;
        mAngle = angle;

        return true;
    }

    /**
     * Return whether or not the given screen coords are inside this image
     */
    public boolean containsPoint(float touchX, float touchY) {
        return (touchX >= mMinX && touchX <= mMaxX && touchY >= mMinY && touchY <= mMaxY);
    }

    public boolean grabAreaContainsPoint(float touchX, float touchY) {
        return (touchX >= mGrabAreaX1 && touchX <= mGrabAreaX2 &&
                touchY >= mGrabAreaY1 && touchY <= mGrabAreaY2);
    }

    public void reload(Context context) {
        mFirstLoad = false; // Let the load know properties have changed so reload those,
                            // don't go back and start with defaults
        load(context, mCenterX, mCenterY);
    }

    public abstract void draw(Canvas canvas);
    public abstract void load(Context context, float startMidX, float startMidY);
    public abstract void unload();

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public float getCenterX() {
        return mCenterX;
    }

    public float getCenterY() {
        return mCenterY;
    }

    public float getScaleX() {
        return mScaleX;
    }

    public float getScaleY() {
        return mScaleY;
    }

    public float getAngle() {
        return mAngle;
    }

    public float getMinX() {
        return mMinX;
    }

    public float getMaxX() {
        return mMaxX;
    }

    public float getMinY() {
        return mMinY;
    }

    public float getMaxY() {
        return mMaxY;
    }

    public void setIsGrabAreaSelected(boolean selected) {
        mIsGrabAreaSelected = selected;
    }

    public boolean isGrabAreaSelected() {
        return mIsGrabAreaSelected;
    }


}