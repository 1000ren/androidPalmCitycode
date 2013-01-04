package com.busx.activity.map;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.core.OverlayItem;
import com.amap.mapapi.map.ItemizedOverlay;
import com.amap.mapapi.map.MapView;
import com.busx.R;
import com.busx.activity.BusXActivity;
import com.busx.entities.POIItem;
import com.busx.utils.Constants;

public class POIOverlay extends ItemizedOverlay<OverlayItem>
{
	private List<OverlayItem> mPOIList = new ArrayList<OverlayItem>();
	private BusXActivity mContext;
	private TextView mPoiName;
	private TextView mPoiAddr;
	private Resources mResources;
	private int mSearchMode;

	
	public POIOverlay( Drawable marker, Context context, List<POIItem> poiItems ,Resources resources,int searchMode)
	{
		super(marker);
		this.mContext = (BusXActivity)context;
		this.mResources = resources;
		this.mSearchMode = searchMode;
		// init ctrl
		mPoiName = (TextView) mContext.findViewById(R.id.PoiName);
		mPoiAddr = (TextView) mContext.findViewById(R.id.PoiAddress);
		ImageView imgViewRight = (ImageView) mContext.findViewById(R.id.ImageButtonRight);
		imgViewRight.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				mContext.mHandler.sendEmptyMessage( Constants.FUNC_BUSEXCHANGE );
			}
		});

		for (int i=0, len=poiItems.size(); i<len; i++)
		{
			POIItem poiItem = poiItems.get(i);

			GeoPoint pt = new GeoPoint( (int)(poiItem.gPoint.lat * 1E6), 
					(int)(poiItem.gPoint.lon * 1E6) );
			OverlayItem overlayItem = new OverlayItem( pt, poiItem.name, poiItem.addr );
			mPOIList.add( overlayItem );
		}
		populate();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
        super.draw(canvas, mapView, shadow); 
        for(int i=0;i<this.size();i++)
        {
        	OverlayItem overLayItem = getItem(i);
			 Drawable tMarker = mResources.getDrawable(MapPublicMethod.getImageResource(i,mSearchMode));
			 overLayItem.setMarker(tMarker);
			 boundCenterBottom(tMarker);
        }
		
	}

	@Override
	protected OverlayItem createItem(int position)
	{
		return mPOIList.get(position);
	}

	@Override
	public int size()
	{
		return mPOIList.size();
	}

	@Override
	// 处理当点击事件
	protected boolean onTap(int position)
	{
		setFocus(mPOIList.get(position));

		mContext.mCommonApplication.mPoiItem = 
				mContext.mCommonApplication.mPoiRes.mPoiList.get(position);

		OverlayItem item = mPOIList.get(position);
		GeoPoint pt = item.getPoint();
		mContext.mMapView.updateViewLayout(mContext.mPoiPopView,
				new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT, pt,
						MapView.LayoutParams.BOTTOM_CENTER));
		mContext.mPoiPopView.setVisibility(View.VISIBLE);

		mPoiName.setText( item.getTitle() );
		mPoiAddr.setText( item.getSnippet() );

		return true;
	}

	@Override
	public boolean onTap(GeoPoint point, MapView mapView)
	{
		if ( !super.onTap(point, mapView) )
		{
			mContext.mPoiPopView.setVisibility( View.GONE );
		}
		return true;
	}
	
	

}
