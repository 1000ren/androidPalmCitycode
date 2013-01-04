package com.busx.activity.map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.Overlay;
import com.busx.R;
import com.busx.entities.BusLine;
import com.busx.entities.GPoint;

public class BusLineOverlay extends Overlay 
{
	private BusLine mBusLine = null;
	private Paint mPaintLine = new Paint();
	private Point mPt = new Point();
	private Point mPtLast = new Point();
	private Bitmap mBitMapStart = null;
	private Bitmap mBitMapEnd = null;
	private Resources mResources = null;

	public BusLineOverlay(Drawable marker, Context context, BusLine busline ,Resources resources )
	{
		this.mResources = resources;
		mBusLine = busline;
		
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		super.draw(canvas, mapView, shadow); 
		mPaintLine.setColor(mResources.getColor(R.color.line_bus));
		mPaintLine.setStrokeWidth(8.0f);
		mPaintLine.setStyle(Paint.Style.STROKE);
		mPaintLine.setAntiAlias(true);
		mPaintLine.setStrokeJoin(Paint.Join.ROUND);
		mPaintLine.setStrokeCap(Paint.Cap.ROUND);
		
		mBitMapStart = BitmapFactory.decodeResource(mResources, R.drawable.icon_nav_start);
		mBitMapEnd = BitmapFactory.decodeResource(mResources, R.drawable.icon_nav_end);
		// 画线
		for (int a=1;a<mBusLine.gPoints.size();a++)
		{
			GPoint gPt =mBusLine.gPoints.get(a);
			GeoPoint point = new GeoPoint( (int)(gPt.lat*1E6), (int)(gPt.lon*1E6) );
			mapView.getProjection().toPixels(point, mPt);
			
			GPoint gPtLast =mBusLine.gPoints.get(a-1);
			GeoPoint pointLast = new GeoPoint( (int)(gPtLast.lat*1E6), (int)(gPtLast.lon*1E6) );
			mapView.getProjection().toPixels(pointLast, mPtLast);
			if(a==1)
			{
				canvas.drawBitmap(mBitMapStart, mPtLast.x-16, mPtLast.y-45, mPaintLine);
			}
			else if(a==mBusLine.gPoints.size()-1)
			{
				canvas.drawBitmap(mBitMapEnd, mPt.x-16, mPt.y-45, mPaintLine);
			}
				canvas.drawLine( mPt.x, mPt.y, mPtLast.x, mPtLast.y, mPaintLine);
		}
	}
	
	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1)
	{
		return super.onTap(arg0, arg1);
	}
	
	
}
