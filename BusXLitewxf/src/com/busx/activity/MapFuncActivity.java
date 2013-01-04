package com.busx.activity;

import java.io.Serializable;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Toast;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.busx.CommonApplication;
import com.busx.R;
import com.busx.activity.map.MapPointOverlay;
import com.busx.common.StateAlert;
import com.busx.entities.GPoint;
import com.busx.utils.Constants;

public class MapFuncActivity extends MapActivity 
{
	protected StateAlert mStateAlert = null;
	public CommonApplication mCommonApplication = null;

	public MapView mMapView;
	private MapController mMapController;
	private MapPointOverlay mPointOverlay;

	private boolean mbStartFlag = true;

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature( Window.FEATURE_NO_TITLE );
    	setContentView(R.layout.activity_mapfunc);
        

        mStateAlert = new StateAlert(this);
        mCommonApplication = (CommonApplication)getApplicationContext();

        // init ctrl
        initCtrl();

        // init data
        if ( getDataFromIntent( Constants.EXTRA_MAPPOINTFLAG ) != null )
        {
        	mbStartFlag = (Boolean) getDataFromIntent( Constants.EXTRA_MAPPOINTFLAG );
		}
        Toast.makeText(getApplicationContext(), "点击地图选点", Toast.LENGTH_SHORT).show();

        mPointOverlay = new MapPointOverlay( this, mbStartFlag );
        mMapView.getOverlays().add(mPointOverlay);
    }

	public void initCtrl()
    {
    	// init ctrl
        mMapView = (MapView) findViewById(R.id.mapFuncView);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setVectorMap(true);
		mMapController = mMapView.getController();
		if (null!= mCommonApplication.mCity.lonLats&&!"".equals(mCommonApplication.mCity.lonLats)) 
		{
			String[] lonlat = mCommonApplication.mCity.lonLats.split(",");
			GeoPoint point = new GeoPoint((int) (Double.parseDouble(lonlat[1])* 1E6), (int) (Double.parseDouble(lonlat[0])* 1E6));
			mMapController.setCenter(point);
		}
		else if (null!=mCommonApplication.mLocationOverlay.getMyLocation())
		{
			mMapController.setCenter(mCommonApplication.mLocationOverlay.getMyLocation());
		}
		mMapController.setZoom( Constants.MAPSCALE_DEFAULT );
    }

	public Serializable getDataFromIntent(String name)
    {
		Intent it = getIntent();
		if (it != null)
		{
			return getIntent().getSerializableExtra(name);
		}
		else
		{
			return null;
		}
	}
	
	public Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			GeoPoint geoPt = (GeoPoint)msg.obj;
			GPoint gpt = new GPoint( geoPt.getLongitudeE6()*1.0/1E6, 
					geoPt.getLatitudeE6()*1.0/1E6 );
			switch ( msg.what )
			{
				case Constants.FUNC_MAPPOINT:
				{
					Intent intent= new Intent();
					intent.setClass( MapFuncActivity.this, RouteActivity.class );
					intent.putExtra( Constants.EXTRA_MAPPOINT, gpt );
					setResult( RESULT_OK, intent );
					finish();
					break;
				}
				default:
				{
					break;
				}
			}
		}
    };
	
	
	
}
