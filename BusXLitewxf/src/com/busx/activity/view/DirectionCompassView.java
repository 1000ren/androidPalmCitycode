package com.busx.activity.view;

import com.busx.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class DirectionCompassView extends LinearLayout
{
	private Paint mPaint = new Paint();
	private Path mPath = new Path();
	private float[] mValues = null;
	private double directionAngle;
	private Resources res=getResources();
	public DirectionCompassView(Context context, AttributeSet attrs)
	{  
        super(context, attrs);
		mPath.moveTo(0, -10);
		mPath.lineTo(-8, 10);
		mPath.lineTo(0, 5);
		mPath.lineTo(8, 10);
		mPath.close();
    }
	
	public DirectionCompassView(Context context) 
	{
		super(context);
		mPath.moveTo(0, -10);
		mPath.lineTo(-8, 10);
		mPath.lineTo(0, 5);
		mPath.lineTo(8, 10);
		mPath.close();
	}

	@Override
	protected void onDraw(Canvas canvas) 
	{
		Paint paint = mPaint;
		paint.setAntiAlias(true);
		
		paint.setColor(res.getColor(R.color.directionCompass));
		paint.setStyle(Paint.Style.FILL);
		canvas.translate(getWidth()/2, getHeight()/2 );
		if (mValues != null)
		{
			float f = -mValues[0] + (float)directionAngle;
			canvas.rotate( f );
		}
		canvas.drawPath(mPath, mPaint);
	}

	public void setValues(float[] values)
	{
		if ( values != null && values.length > 0 )
		{
			mValues = new float[values.length];
		    mValues[0] = values[0];
		}
	}

	public void setDirectionAngle(double directionAngle)
	{
		this.directionAngle = directionAngle;
	}

}