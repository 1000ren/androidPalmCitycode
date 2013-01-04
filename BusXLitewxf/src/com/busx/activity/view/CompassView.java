package com.busx.activity.view;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.busx.R;

public class CompassView extends View {
	 
    private Bitmap[] mBitmapArray = new Bitmap[4];
    InputStream is;
    private float[] mValues;
    int[] mBitmapWidth = new int[4];
    int[] mBitmapHeight = new int[4];
    public CompassView(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }
 
    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
 
        BitmapFactory.Options opts = new Options();
        opts.inJustDecodeBounds = false;
        setBitMapArray(context, 0, opts, R.drawable.compass_bg);
        setBitMapArray(context, 1, opts, R.drawable.compass_point);
 
    }
 
    /**
     * 设置bitmap数组个下标的值
     * 
     * @param index
     * @param opts
     * @param resid
     */
    private void setBitMapArray(Context context, int index,
            BitmapFactory.Options opts, int resid) {
        is = context.getResources().openRawResource(resid);
        mBitmapArray[index] = BitmapFactory.decodeStream(is);
        mBitmapWidth[index] = mBitmapArray[index].getWidth();
        mBitmapHeight[index] = mBitmapArray[index].getHeight();
        mBitmapArray[index + 2] = BitmapFactory.decodeStream(is, null, opts);
        mBitmapHeight[index + 2] = mBitmapArray[index + 2].getHeight();
        mBitmapWidth[index + 2] = mBitmapArray[index + 2].getWidth();
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
 
        canvas.translate(getWidth() / 15, getHeight() / 6);
        drawPictures(canvas, 0);
 
    }
 
    private void drawPictures(Canvas canvas, int idDelta) {
        if (mValues != null) {
            canvas.drawBitmap(mBitmapArray[0 + idDelta],
                    -mBitmapWidth[0 + idDelta] / 2,
                    -mBitmapHeight[0 + idDelta] / 2, null);
            canvas.rotate(-mValues[0]);
            canvas.drawBitmap(mBitmapArray[1 + idDelta],
                    -mBitmapWidth[1 + idDelta] / 2,
                    -mBitmapHeight[1 + idDelta] / 2, null);
            
 
        } else {
            canvas.drawBitmap(mBitmapArray[0 + idDelta],
                    -mBitmapWidth[0 + idDelta] / 2,
                    -mBitmapHeight[0 + idDelta] / 2, null);
 
            canvas.drawBitmap(mBitmapArray[1 + idDelta],
                    -mBitmapWidth[1 + idDelta] / 2,
                    -mBitmapHeight[1 + idDelta] / 2, null);
        }
    }
    //角度值
    public void setValue(float[] value) {
        this.mValues = value;
    }
 
}