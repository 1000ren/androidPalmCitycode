package com.busx.activity.map;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.Overlay;
import com.busx.R;
import com.busx.activity.MapFuncActivity;
import com.busx.utils.Constants;

public class MapPointOverlay extends Overlay
{
	private MapFuncActivity mContext = null;
    private LayoutInflater mInflater = null;
    private View mPopUpView = null;
    private boolean mbStartFlag = true;

    public MapPointOverlay(MapFuncActivity context, boolean bStart )
    {
    	this.mContext = context;
    	mbStartFlag = bStart;
    	mInflater = (LayoutInflater)context.getSystemService(
    	        Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
    {
		super.draw(canvas, mapView, shadow);
	}
    
    @Override
	public boolean onTap(final GeoPoint point, final MapView view)
    {
		if ( mPopUpView != null )
		{
			view.removeView( mPopUpView );
		}
		// Projection接口用于屏幕像素点坐标系统和地球表面经纬度点坐标系统之间的变换
		mPopUpView = mInflater.inflate(R.layout.popup_mappoint, null);
		TextView textView=(TextView) mPopUpView.findViewById(R.id.PointName);
		if ( mbStartFlag )
		{
			textView.setText("点击选择该点为起点");
		}
		else
		{
			textView.setText("点击选择该点为终点");
		}
		MapView.LayoutParams lp;
		lp = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT,point,0,0,
				MapView.LayoutParams.BOTTOM_CENTER);
		view.addView( mPopUpView, lp );

		mPopUpView.setOnClickListener(new OnClickListener()
		{	
			@Override
			public void onClick(View v)
			{
				Message msg = new Message();
				msg.what = Constants.FUNC_MAPPOINT;
				msg.obj = point;
				mContext.mHandler.sendMessage( msg );
				view.removeView(mPopUpView);
				mContext.mMapView.getOverlays().remove(this);
			}
		});

        return super.onTap(point, view);
	}
    
    
}
