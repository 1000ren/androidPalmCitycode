package com.busx.location;

import java.util.LinkedList;

import android.content.Context;
import android.location.Location;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.MyLocationOverlay;
import com.busx.map.MapUtil;

public class MyLocationOverlayProxy extends MyLocationOverlay
{
	 private Location mLocation;
     private final LinkedList<Runnable> mRunOnFirstFix = new LinkedList<Runnable>();

     private MapView mMapView = null;
     public boolean mMovetoLocation = false;
	 public MyLocationOverlayProxy(Context arg0, MapView arg1)
	 {
		super(arg0, arg1);
		this.mMapView = arg1;
	 }
	
	@Override
	public boolean runOnFirstFix(final Runnable runnable)
	{
		if (mLocation != null)
		{
			new Thread(runnable).start();
			return true;
		}
		else
		{
			mRunOnFirstFix.addLast(runnable);
			return false;
		}
    }

	@Override
	public void onLocationChanged(Location location)
	{
        mLocation = location;
        for(final Runnable runnable : mRunOnFirstFix) {
			new Thread(runnable).start();
		}
		mRunOnFirstFix.clear();
		super.onLocationChanged(location);
		
		if (mMovetoLocation) 
		{
			mMapView.getController().animateTo(getMyLocation());
		}
		
	}

	@Override
	public GeoPoint getMyLocation()
	{

		if(null == mLocation)
		{
			return null;
		}
		// 判断位置的合理性
		// 中国范围
		if (!MapUtil.isInChina( mLocation.getLongitude(), mLocation.getLatitude() ) )
		{
			return null;
		}
		return super.getMyLocation();
	}
	
	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1) 
	{
		mMovetoLocation = false;
		return super.onTap(arg0, arg1);
		
	}

}
