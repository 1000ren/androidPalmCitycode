package com.busx.activity.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.Overlay;
import com.busx.R;
import com.busx.entities.BusRoute;
import com.busx.entities.GPoint;
import com.busx.entities.POIItem;
import com.busx.entities.RoutePathShp;

public class BusRouteOverlay extends Overlay
{
	private BusRoute mBusRoute = null;
	private POIItem mStartPoiItem = null;
	private POIItem mDestPoiItem = null;
	private GeoPoint mStartGeoPt = null;
	private GeoPoint mDestGeoPt = null;
	private Paint mPaintLine = new Paint();
	private Paint mPaintDashLine = new Paint();
	private Point mPtLast = new Point();
	private Point mPt = new Point();
	private Bitmap mStartBitmap = null;
	private Bitmap mDestBitmap = null;
	private Bitmap mBusBitmap = null;
	private Bitmap mRailwayBitmap = null;

	public BusRouteOverlay( Context context, BusRoute busRoute, POIItem startpoi, POIItem destpoi )
	{
		mBusRoute = busRoute;
		mStartPoiItem = startpoi;
		mDestPoiItem = destpoi;
		mStartGeoPt = new GeoPoint( (int)(mStartPoiItem.gPoint.lat*1E6), (int)(mStartPoiItem.gPoint.lon*1E6) );
		mDestGeoPt = new GeoPoint( (int)(mDestPoiItem.gPoint.lat*1E6), (int)(mDestPoiItem.gPoint.lon*1E6) );

		mPaintLine.setColor(context.getResources().getColor(R.color.line_bus));
		mPaintLine.setStrokeWidth(8.0f);
		mPaintLine.setStyle(Paint.Style.STROKE);
		mPaintLine.setAntiAlias(true);
		mPaintLine.setStrokeJoin(Paint.Join.ROUND);
		mPaintLine.setStrokeCap(Paint.Cap.ROUND);

		mPaintDashLine.setColor(context.getResources().getColor(R.color.line_walk));
		mPaintDashLine.setStrokeWidth(8.0f);
		mPaintDashLine.setStyle(Paint.Style.STROKE);
		mPaintDashLine.setAntiAlias(true);
		mPaintDashLine.setStrokeJoin(Paint.Join.ROUND);
		mPaintDashLine.setStrokeCap(Paint.Cap.ROUND);
		// 虚线绘制
		//PathEffect effects = new DashPathEffect(new float[] {16,8,16,8}, 1);
		//mPaintDashLine.setPathEffect( effects );
		mStartBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_nav_start);
		mDestBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_nav_end);
		mBusBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_bus_pt);
		mRailwayBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_trans_pt);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		super.draw(canvas, mapView, shadow);

		mapView.getProjection().toPixels(mStartGeoPt, mPtLast);
		mapView.getProjection().toPixels(mDestGeoPt, mPtLast);
		// 绘制线路
		// 绘制起始点
		mapView.getProjection().toPixels(mStartGeoPt, mPtLast);
		canvas.drawBitmap( mStartBitmap, mPtLast.x-mStartBitmap.getWidth()/2, mPtLast.y-mStartBitmap.getHeight()+2, null );
		
		for (int i=0,len=mBusRoute.pathShpList.size()-1; i<len; i++)
		{
			RoutePathShp pathShp = mBusRoute.pathShpList.get(i);
			if ( pathShp == null || pathShp.pointList == null )
			{
				continue;
			}
			if ( pathShp.pointList.size() <= 0 )
			{
				continue;
			}
			if ( i != 0 )
			{
				// 绘制两段之间的连接线路，步行模式
				GPoint gPt = pathShp.pointList.get(0);
				GeoPoint point = new GeoPoint( (int)(gPt.lat*1E6), (int)(gPt.lon*1E6) );
				mapView.getProjection().toPixels(point, mPt);
				canvas.drawLine( mPtLast.x, mPtLast.y, mPt.x, mPt.y, mPaintDashLine);
			}
			for (int j=0,size=pathShp.pointList.size(); j<size-1; j++)
			{
				GPoint gPt = pathShp.pointList.get(j);
				GeoPoint point = new GeoPoint( (int)(gPt.lat*1E6), (int)(gPt.lon*1E6) );
				mapView.getProjection().toPixels(point, mPt);

				gPt = pathShp.pointList.get(j+1);
				point = new GeoPoint( (int)(gPt.lat*1E6), (int)(gPt.lon*1E6) );
				mapView.getProjection().toPixels(point, mPtLast);

				if ( pathShp.iType == 4 )
				{
					canvas.drawLine( mPt.x, mPt.y, mPtLast.x, mPtLast.y, mPaintDashLine);
				}
				else
				{
					canvas.drawLine( mPt.x, mPt.y, mPtLast.x, mPtLast.y, mPaintLine);
				}

				// 绘制每一段的起始终止图标
				if ( pathShp.iType == 1 )
				{
					if ( j == 0 )
					{
						canvas.drawBitmap( mBusBitmap, 
								mPt.x-mStartBitmap.getWidth()/2, 
								mPt.y-mStartBitmap.getHeight()+2, null );
					}
					else if ( j+1 == size-1 )
					{
						canvas.drawBitmap( mBusBitmap, 
								mPtLast.x-mStartBitmap.getWidth()/2, 
								mPtLast.y-mStartBitmap.getHeight()+2, null );
					}
				}
				else if ( pathShp.iType == 2 )
				{
					if ( j == 0 )
					{
						canvas.drawBitmap( mRailwayBitmap, 
								mPt.x-mStartBitmap.getWidth()/2, 
								mPt.y-mStartBitmap.getHeight()+2, null );
					}
					else if ( j+1 == size-1 )
					{
						canvas.drawBitmap( mRailwayBitmap, 
								mPtLast.x-mStartBitmap.getWidth()/2, 
								mPtLast.y-mStartBitmap.getHeight()+2, null );
					}
				}
			}

		}
		//
		
		// 绘制终点
		mapView.getProjection().toPixels(mDestGeoPt, mPtLast);
		canvas.drawBitmap( mDestBitmap, 
				mPtLast.x-mDestBitmap.getWidth()/2, 
				mPtLast.y-mDestBitmap.getHeight()+2, null );
		canvas.drawLine( mPtLast.x, mPtLast.y, mPt.x, mPt.y, mPaintDashLine);
	}

	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1)
	{
		return super.onTap(arg0, arg1);
	}

}
