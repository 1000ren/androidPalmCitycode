package com.busx.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cm.framework.net.ClientSession;
import cm.framework.net.ControlRunnable;
import cm.framework.net.IResponseListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.core.PoiItem;
import com.amap.mapapi.poisearch.PoiPagedResult;
import com.busx.R;
import com.busx.activity.base.BaseActivity;
import com.busx.entities.BusLineRes;
import com.busx.entities.BusStationRes;
import com.busx.entities.GPoint;
import com.busx.entities.POIItem;
import com.busx.entities.POIRes;
import com.busx.protocol.ProtocolDef;
import com.busx.protocol.poi.GetBusLineListRequest;
import com.busx.protocol.poi.GetBusLineListResponse;
import com.busx.protocol.poi.GetBusStationListRequest;
import com.busx.protocol.poi.GetBusStationListResponse;
import com.busx.protocol.poi.GetBusStopListRequest;
import com.busx.protocol.poi.GetBusStopListResponse;
import com.busx.utils.ActivityMgr;
import com.busx.utils.Constants;
import com.busx.utils.Utils;

public class SearchResultListActivity extends BaseActivity
{
	private TextView mResTitle;
	private ListView mListView;
	private SearchResultListAdapter mAdapter;
	private LinearLayout mRelativeLayout;
	private TextView mItemPageText;
	private ImageView mItemPageImage;

	private POIRes mPoiRes=null;
	private BusStationRes mBusstationRes=null;
	private BusLineRes mBusLineRes=null;

	private String mKeyword;
	private int mSearchMode = Constants.SEARCH_POI;

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private float mOldValues = 0.0f;
	private float mTotalEventValue = 0.0f;
	//翻页，第一页还是第二页，默认第一页。
	private int mPageNum = 1;
	private int mTotle = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_reslist);

		mKeyword = (String) getDataFromIntent( Constants.EXTRA_KEYWORD );
		mTotle = (Integer) getDataFromIntent( Constants.EXTRA_MTOTLE )==null?10:(Integer) getDataFromIntent( Constants.EXTRA_MTOTLE );
		mPageNum = (Integer) getDataFromIntent( Constants.EXTRA_PAGENUM )==null?1:(Integer) getDataFromIntent( Constants.EXTRA_PAGENUM );
		
		mRelativeLayout = (LinearLayout) findViewById(R.id.RelativeLayout_poi_tst_bottom);
		mItemPageText= (TextView) findViewById(R.id.ItemPageText);
		mItemPageImage= (ImageView) findViewById(R.id.ItemPageImage);
		mItemPageText.setText(R.string.page_down);
		mItemPageImage.setImageResource(R.drawable.btn_icon_nextpage);
		if(mTotle>10)
		{
			mRelativeLayout.setVisibility(View.VISIBLE);
		}
		else
		{
			mRelativeLayout.setVisibility(View.GONE);
		}
		
		// get data
		mSearchMode = (Integer) getDataFromIntent( Constants.EXTRA_SEARCHMODE );
		if ( mSearchMode==Constants.SEARCH_POI || 
				mSearchMode==Constants.SEARCH_NEARBY )
		{
			mPoiRes = (POIRes) getDataFromIntent( Constants.EXTRA_POIRES );
			mCommonApplication.mPoiRes = mPoiRes;
			if(null != mPoiRes.mPoiList && mPoiRes.mPoiList.size()>0)
			{
				mCommonApplication.mPoiItem = mPoiRes.mPoiList.get(0);
			}
			mCommonApplication.mBusStationRes = null;
			mCommonApplication.mBusStation = null;
			
			if ( mSearchMode == Constants.SEARCH_NEARBY )
			{
				mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
				mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
				// 计算角度
				GeoPoint geoPoint = mCommonApplication.mMapView.getMapCenter();
				for (int i=0,len=mPoiRes.mPoiList.size(); i<len; i++)
				{
					POIItem poiItem = mPoiRes.mPoiList.get(i);
					if ( poiItem == null )
					{
						continue;
					}
					poiItem.angle = Utils.computeAzimuth(geoPoint.getLatitudeE6()*1.0/1E6, 
							geoPoint.getLongitudeE6()*1.0/1E6, 
							poiItem.gPoint.lat, poiItem.gPoint.lon);
				}
			}
		}
		else if ( mSearchMode==Constants.SEARCH_BUSSTATION )
		{
			mBusstationRes = (BusStationRes) getDataFromIntent( Constants.EXTRA_BUSSTATIONRES );
			mCommonApplication.mBusStationRes = mBusstationRes;
			if(null != mBusstationRes.mBusStationList && mBusstationRes.mBusStationList.size()>0)
			{
				mCommonApplication.mBusStation = mBusstationRes.mBusStationList.get(0);
			}
			
			mCommonApplication.mPoiRes = null;
			mCommonApplication.mPoiItem = null;
		}
		else if ( mSearchMode==Constants.SEARCH_BUSLINE )
		{
			mBusLineRes = (BusLineRes) getDataFromIntent( Constants.EXTRA_BUSLINERES );
			mCommonApplication.mBusLineRes = mBusLineRes;
			if(null != mBusLineRes.mBusLineList && mBusLineRes.mBusLineList.size()>0)
			{
				mCommonApplication.mBusLine = mBusLineRes.mBusLineList.get(0);
			}
			
		}
		
		mCommonApplication.mSearchMode = mSearchMode;
		mCommonApplication.mSearchKeyword = mKeyword;

		// init ctrl
		mResTitle = (TextView) findViewById( R.id.ItemPOIRst );
		mResTitle.setText( "搜索 "+ mKeyword +" 结果" );

		mListView=(ListView) findViewById(R.id.ListView_SearchRes);
		mAdapter = new SearchResultListAdapter( this, mPoiRes, mBusstationRes, mBusLineRes, mSearchMode);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Map<String, String> paramMap = new HashMap<String, String>();
				mCommonApplication.mTotle = mTotle;
				mCommonApplication.mPageNum = mPageNum;
				if (mSearchMode!=Constants.SEARCH_BUSLINE)
				{
					if (mSearchMode==Constants.SEARCH_POI || mSearchMode==Constants.SEARCH_NEARBY )
					{
						mCommonApplication.mMapDisplayMode = Constants.MAPMODE_VIEWPOI;
						mCommonApplication.mPoiItem = mPoiRes.mPoiList.get(position);
					}
					else if (mSearchMode==Constants.SEARCH_BUSSTATION)
					{
						mCommonApplication.mMapDisplayMode = Constants.MAPMODE_VIEWSTATION;
						mCommonApplication.mBusStation = mBusstationRes.mBusStationList.get(position);
						
						paramMap.put("bss_content", mCommonApplication.mBusStation.name);
						mClientAgent.getEventClientinfo("bus_search_station_detail", paramMap);
					}
					ActivityMgr.getActivityManager().popAllActivity();
		        	finish();
				}
				else 
				{
					mCommonApplication.mMapDisplayMode = Constants.MAPMODE_VIEWBUSLINE;
					mCommonApplication.mBusLine = mBusLineRes.mBusLineList.get(position);
					if (mCommonApplication.mBusLine.busStops.size()>0)
					{
						ActivityMgr.getActivityManager().popAllActivity();
			        	finish();
					}
					else
					{
						ClientSession.getInstance().asynGetResponse(new GetBusStopListRequest( 
								mCommonApplication.mUserLoginInfo.sid, mCommonApplication.mBusLine.lineid , mCommonApplication.mCity.admincode),
								new IResponseListener()
						{
									@Override
									public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2)
									{
										GetBusStopListResponse busstopListResponse = (GetBusStopListResponse) arg0;
										mCommonApplication.mUserLoginInfo.copySID( busstopListResponse.mUserLoginInfo );
										mCommonApplication.mBusLine.copy( busstopListResponse.mBusLine );
										ActivityMgr.getActivityManager().popAllActivity();
							        	finish();
									}
						});
					}
					paramMap.put("bss_content", mCommonApplication.mBusLine.linename);
					mClientAgent.getEventClientinfo("bus_search_station_detail", paramMap);
				}
			}
			
		});
		
		mRelativeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mPageNum == 1)
				{
					mPageNum = 2;
				}
				else if(mPageNum == 2)
				{
					mPageNum = 1;
				}
				SearchResult();
				
			}
		});
		
		modifyData();

	}
	
	public List<POIItem> getPOIItemByPoiPagedResult(PoiPagedResult poiPagedResult,int curPage) throws Exception
	 {
		 List<POIItem> list = new ArrayList<POIItem>();
		try 
		{
			if (poiPagedResult != null) 
			{
				List<PoiItem> poiItems = poiPagedResult.getPage(curPage);
				
				if (poiItems != null && poiItems.size() > 0) 
				{
					for (PoiItem poiItem : poiItems) 
					{
						POIItem  poiItem_new = new POIItem();
						poiItem_new.id = poiItem.getPoiId();
						poiItem_new.name = poiItem.toString();
						poiItem_new.addr = poiItem.getSnippet();
						poiItem_new.gPoint = new GPoint(poiItem.getPoint().getLongitudeE6()/1E6,poiItem.getPoint().getLatitudeE6()/1E6);
						poiItem_new.admincode = poiItem.getTypeCode();
						poiItem_new.tel = poiItem.getTel();	
						list.add(poiItem_new);
					}
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
  	return list;
  }
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what)
			{
				case Constants.PAGE_FLIP_DOME:
				{
					//跳到第二页，在第二页当中跳转按钮应提示的是向上翻页
					mItemPageText.setText(R.string.page_down);
					mItemPageImage.setImageResource(R.drawable.btn_icon_nextpage);
					break;
				}
				case Constants.PAGE_FLIP_UP:
				{
					mItemPageText.setText(R.string.page_up);
					mItemPageImage.setImageResource(R.drawable.btn_icon_prepage);
					break;
				}
			}
			mAdapter.setDate(mPoiRes, mBusstationRes,mBusLineRes, mSearchMode);
			mListView.setAdapter(mAdapter);
		}
	};

	@Override
	protected void onResume() 
	{
		ActivityMgr.getActivityManager().clear(this);
		super.onResume();
		if ( mSearchMode==Constants.SEARCH_NEARBY )
		{
			mSensorManager.registerListener(mListener, mSensor,
					SensorManager.SENSOR_DELAY_UI); 
		}
		
	}

	@Override
	protected void onStop() 
	{
		if ( mSearchMode==Constants.SEARCH_NEARBY )
		{
			mSensorManager.unregisterListener(mListener);
		}
		super.onStop();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			mCommonApplication.mIsBack = true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private final SensorEventListener mListener = new SensorEventListener()
	{
		@Override
		public void onSensorChanged(SensorEvent event)
		{
			// 方向传感器
			if ( event.sensor.getType() == Sensor.TYPE_ORIENTATION )
			{
				mPoiRes.mValues = event.values;
				if ( event.values != null )
				{
					mTotalEventValue+=Math.abs(Math.abs(event.values[0])-Math.abs(mOldValues));
					if (mTotalEventValue>30.0f) 
					{
						mTotalEventValue= 0.0f;
						mAdapter.notifyDataSetChanged();
					}
					mOldValues = event.values[0];
				}
			}
		}
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
		}
	};
	
	//翻页查询
	public void SearchResult()
	{

		mCommonApplication.mSearchMode = mSearchMode;
		mCommonApplication.mSearchKeyword = mKeyword;
		
		if (mSearchMode==Constants.SEARCH_POI||mSearchMode==Constants.SEARCH_NEARBY)
		{
			try 
			{
				List<POIItem> poiItems = getPOIItemByPoiPagedResult(mCommonApplication.mPoiPagedResult, mPageNum);
				mPoiRes.mPoiList = poiItems;
				modifyData();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
		}
		else if (mSearchMode==Constants.SEARCH_BUSSTATION)
		{
			ClientSession.getInstance().asynGetResponse(new GetBusStationListRequest( 
					mCommonApplication.mUserLoginInfo.sid, mKeyword, mPageNum, ProtocolDef.PAGE_SIZE , mCommonApplication.mCity.admincode),
					new IResponseListener()
			{
						@Override
						public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2) 
						{
							Map<String, String> paramMap = new HashMap<String, String>();
							paramMap.put("bss_content", mKeyword);
							mClientAgent.getEventClientinfo("bus_search_station", paramMap);
							GetBusStationListResponse busStationListResponse = (GetBusStationListResponse) arg0;
							mCommonApplication.mUserLoginInfo.copySID( busStationListResponse.mUserLoginInfo );
							mBusstationRes = busStationListResponse.mBusStationRes;
							mTotle = busStationListResponse.mTotal;
							modifyData();
						}
			});
		}
		else if (mSearchMode==Constants.SEARCH_BUSLINE)
		{
			ClientSession.getInstance().asynGetResponse(new GetBusLineListRequest( 
					mCommonApplication.mUserLoginInfo.sid, mKeyword, mPageNum, ProtocolDef.PAGE_SIZE , mCommonApplication.mCity.admincode),
					new IResponseListener() 
			{
						@Override
						public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2)
						{
							Map<String, String> paramMap = new HashMap<String, String>();
							paramMap.put("bsl_content", mKeyword);
							mClientAgent.getEventClientinfo("bus_search_line", paramMap);
							GetBusLineListResponse busLineListResponse = (GetBusLineListResponse) arg0;
							mCommonApplication.mUserLoginInfo.copySID( busLineListResponse.mUserLoginInfo );
							mBusLineRes = busLineListResponse.mBusLineRes ;
							mTotle = busLineListResponse.mTotal;
							modifyData();
						}
			});
		}
		
		
	}
	
	/**
	 * 翻页时的页面的处理
	 */
	public void modifyData()
	{
		int message = 0;
		if(mPageNum==2)
		{
			message = Constants.PAGE_FLIP_UP;
		}
		else
		{
			message = Constants.PAGE_FLIP_DOME;
		}
		mHandler.sendEmptyMessage(message);
	}
	
}
