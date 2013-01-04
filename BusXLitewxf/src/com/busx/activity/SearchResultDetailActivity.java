package com.busx.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cm.framework.net.ClientSession;
import cm.framework.net.ControlRunnable;
import cm.framework.net.IResponseListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

import com.amap.mapapi.core.GeoPoint;
import com.busx.R;
import com.busx.activity.base.BaseActivity;
import com.busx.activity.view.DirectionCompassView;
import com.busx.data.FavoritesData;
import com.busx.database.SaveManager;
import com.busx.entities.BusLine;
import com.busx.entities.BusStation;
import com.busx.entities.GPoint;
import com.busx.entities.POIItem;
import com.busx.protocol.poi.GetBusStopListRequest;
import com.busx.protocol.poi.GetBusStopListResponse;
import com.busx.utils.ActivityMgr;
import com.busx.utils.Constants;

public class SearchResultDetailActivity extends BaseActivity
{
	private TextView mPoiNameTextView;
	private TextView mPoiAddrTextView;
	private TextView mPoiDisTextView;
	private ImageButton mFavorButton;
	private ImageButton mViewMapButton;
	private ImageButton mGoButton;
	private ImageButton mComeButton;
	private ImageButton mSearchNearButton;
	private ListView mOtherDetailListView;
	private DirectionCompassView mDirectionCompassView;

	private int mSearchMode;
	private String mMapDisplayMode;
	private POIItem mPoiItem = new POIItem();
	private BusStation mBusStation = new BusStation();
	private BusLine mBusLine = new BusLine();

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private float[] mValues;
	private String mRidingRoute;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_resdetail);

		mPoiNameTextView = (TextView) findViewById( R.id.ItemTitleDetail );
		mPoiAddrTextView = (TextView) findViewById( R.id.ItemTextDetail );
		mPoiDisTextView = (TextView) findViewById( R.id.ItemDisDetail );
		mFavorButton = (ImageButton) findViewById( R.id.ImageButtonFavor );
		mViewMapButton = (ImageButton) findViewById( R.id.ImageButtonViewMap );
		mGoButton = (ImageButton) findViewById( R.id.ImageButtonGo );
		mComeButton = (ImageButton) findViewById( R.id.ImageButtonCome );
		mSearchNearButton = (ImageButton) findViewById( R.id.ImageButtonSearchNear );
		mOtherDetailListView =(ListView) findViewById( R.id.PoiDetail_ListView ); 

		mSearchMode=(Integer) getDataFromIntent( Constants.EXTRA_SEARCHMODE );
		getMapDisplayMode(mSearchMode);
		// get data
		if (mSearchMode==Constants.SEARCH_POI || mSearchMode==Constants.SEARCH_NEARBY )
		{
			mPoiItem = (POIItem) getDataFromIntent( Constants.EXTRA_POIITEM );
			mPoiItem.favoriteflag = getIsFavorite(mMapDisplayMode, mPoiItem.id);
			if (mPoiItem.favoriteflag) 
			{
				mFavorButton.setBackgroundResource(R.drawable.icon_select_fav);
			}
			if ( mPoiItem != null )
			{
				mPoiNameTextView.setText( mPoiItem.name );
				mPoiAddrTextView.setText( mPoiItem.addr );
				if (null == mPoiItem.addr ||"".equals( mPoiItem.addr )) 
				{
					mPoiAddrTextView.setText(mPoiItem.adminname);
				}
				
			}
			if ( mSearchMode==Constants.SEARCH_NEARBY )
			{
				mPoiDisTextView.setText( mPoiItem.score );
				mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
				mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
				mDirectionCompassView = (DirectionCompassView) findViewById(R.id.Direction);
				mDirectionCompassView.setDirectionAngle(mPoiItem.angle);
				mDirectionCompassView.setVisibility(View.VISIBLE);
			}
		}
		else if (mSearchMode==Constants.SEARCH_BUSSTATION) 
		{
			mBusStation=(BusStation)getDataFromIntent( Constants.EXTRA_BUSSTATION );
			mBusStation.favoriteflag = getIsFavorite(mMapDisplayMode, mBusStation.stopid);
			if (mBusStation.favoriteflag) 
			{
				mFavorButton.setBackgroundResource(R.drawable.icon_select_fav);
			}
			if (mBusStation!=null)
			{
				mPoiNameTextView.setText( mBusStation.name );
				mPoiAddrTextView.setText( mBusStation.adminname );
			}
		}
		else if (mSearchMode==Constants.SEARCH_BUSLINE)
		{
			mBusLine=(BusLine)getDataFromIntent( Constants.EXTRA_BUSLINE );
			mBusLine.favoriteflag = getIsFavorite(mMapDisplayMode, mBusLine.lineid);
			if (mBusLine.favoriteflag) 
			{
				mFavorButton.setBackgroundResource(R.drawable.icon_select_fav);
			}
			if (mBusLine!=null)
			{
				mPoiNameTextView.setText( mBusLine.linename.substring(0,mBusLine.linename.indexOf("(")) );
				mPoiAddrTextView.setText( mBusLine.linename.substring(mBusLine.linename.indexOf("("),mBusLine.linename.indexOf(")")+1) );
			}
			mGoButton.setVisibility( View.GONE );
			mComeButton.setVisibility( View.GONE );
			mSearchNearButton.setVisibility( View.GONE );
		}

		//加入收藏夹
		mFavorButton.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				switch ( mSearchMode )
				{
					case Constants.SEARCH_POI:
					case Constants.SEARCH_NEARBY:
						{
							
							if (mPoiItem.favoriteflag) 
							{
								//删除收藏
								delFavorite(mMapDisplayMode,mPoiItem.id);
							}
							else 
							{
								//加入收藏
								saveFavorite_poi(mMapDisplayMode, mPoiItem);
							}
							
							break;
						}
					case Constants.SEARCH_BUSSTATION:
					{
						if (mBusStation.favoriteflag) 
						{
							//删除收藏
							delFavorite(mMapDisplayMode,mBusStation.stopid);
						}
						else 
						{
							//加入收藏
							saveFavorite(mMapDisplayMode,mBusStation.stopid, mBusStation.name);
						}
						break;
					}
					case Constants.SEARCH_BUSLINE:
					{
						if (mBusLine.favoriteflag) 
						{
							//删除收藏
							delFavorite(mMapDisplayMode,mBusLine.lineid);
						}
						else 
						{
							//加入收藏
							saveFavorite(mMapDisplayMode,mBusLine.lineid, mBusLine.linename);
							
						}
						break;
					}
				}
			}
		});
		
		mViewMapButton.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				switch ( mSearchMode )
				{
					case Constants.SEARCH_POI:
					case Constants.SEARCH_NEARBY:
					{
						mCommonApplication.mPoiItem = mPoiItem;
						mCommonApplication.mMapDisplayMode = Constants.MAPMODE_VIEWPOI;
						break;
					}
					case Constants.SEARCH_BUSSTATION:
					{
						mCommonApplication.mBusStation = mBusStation;
						mCommonApplication.mMapDisplayMode = Constants.MAPMODE_VIEWSTATION;
						break;
					}
					case Constants.SEARCH_BUSLINE:
					{
						mCommonApplication.mBusLine = mBusLine;
						mCommonApplication.mMapDisplayMode = Constants.MAPMODE_VIEWBUSLINE;
						break;
					}
					default:
					{
						break;
					}
				}
				ActivityMgr.getActivityManager().popAllActivity();
				finish();
			}
		});
		
		mGoButton.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ActivityMgr.getActivityManager().pushActivity(SearchResultDetailActivity.this);
				Intent intent = new Intent();
				intent.setClass( SearchResultDetailActivity.this, RouteActivity.class );
				
				GeoPoint gpt = mCommonApplication.mLocationOverlay.getMyLocation();
				if ( gpt != null )
				{
					POIItem poiItem = new POIItem();
					GPoint gPoint = new GPoint( gpt.getLongitudeE6() * 1.0 / 1E6, gpt.getLatitudeE6() * 1.0 / 1E6 );
					poiItem.gPoint = gPoint;
					poiItem.name = Constants.MAPPOS_POI_CUR_NAME;
					poiItem.id = Constants.MAPPOS_POI_DEFAULT_ID;
					intent.putExtra( Constants.EXTRA_STARTPOI, poiItem );
				}
				if ( mSearchMode == Constants.SEARCH_POI || 
						mSearchMode == Constants.SEARCH_NEARBY)
				{
					intent.putExtra( Constants.EXTRA_DESTPOI, mPoiItem );
				}
				else if ( mSearchMode == Constants.SEARCH_BUSSTATION )
				{
					intent.putExtra( Constants.EXTRA_DESTPOI, busStation2POI(mBusStation) );
				}
				startActivity( intent );
			}
		});

		mComeButton.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ActivityMgr.getActivityManager().pushActivity(SearchResultDetailActivity.this);
				Intent intent = new Intent();
				intent.setClass( SearchResultDetailActivity.this, RouteActivity.class );
				if ( mSearchMode == Constants.SEARCH_POI || 
						mSearchMode == Constants.SEARCH_NEARBY)
				{
					intent.putExtra( Constants.EXTRA_STARTPOI, mPoiItem );
				}
				else if ( mSearchMode == Constants.SEARCH_BUSSTATION )
				{
					intent.putExtra( Constants.EXTRA_STARTPOI, busStation2POI(mBusStation) );
				}
				startActivity( intent );
			}
		});
		
		mSearchNearButton.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				mStateAlert.showWaitDialog("请稍候..");
				new Thread(new Runnable() 
				{
					@Override
					public void run() 
					{
						try 
						{
							Thread.sleep(100);
						} 
						catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
						searchNear();
					}
				}).start();
				
			}
		});
		
		SearchResultDetailAdapter searchResultDetailAdapter=new SearchResultDetailAdapter(this, mBusStation.busline,mBusLine.busStops,this.getDetail(mPoiItem),mSearchMode);
		mOtherDetailListView.setAdapter(searchResultDetailAdapter);
		mOtherDetailListView.setOnItemClickListener( new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				if (mSearchMode == Constants.SEARCH_NEARBY || mSearchMode == Constants.SEARCH_POI) {
					if (mPoiItem.detail.get(arg2).equals(getString(R.string.share_to_friend))) 
					{
						//分享给好友
						Intent it = new Intent(Intent.ACTION_VIEW);
						String msg = mPoiItem.name+":";
						if (null != mPoiItem.addr&&!"".equals(mPoiItem.addr)) {
							msg += mPoiItem.addr;
						}
						else
						{
							msg += mPoiItem.adminname;
						}
						it.putExtra("sms_body", msg);
						it.setType("vnd.android-dir/mms-sms");
						startActivity(it);
					}
					else if (mPoiItem.detail.get(arg2).indexOf(getString(R.string.along_bus))>-1) 
					{
						//沿途公交
						AlertDialog.Builder builder = new AlertDialog.Builder(SearchResultDetailActivity.this);
						builder.setTitle(mPoiItem.name);
						builder.setItems(mPoiItem.buslinename_dialog, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								//在这里列出线路的所有站点

								ClientSession.getInstance().asynGetResponse(new GetBusStopListRequest( 
										mCommonApplication.mUserLoginInfo.sid, mPoiItem.busline.get(which).lineid , mCommonApplication.mCity.admincode),
										new IResponseListener() {
											@Override
											public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2)
											{
												GetBusStopListResponse busstopListResponse = (GetBusStopListResponse) arg0;
												mCommonApplication.mUserLoginInfo.copySID( busstopListResponse.mUserLoginInfo );
												ActivityMgr.getActivityManager().pushActivity(SearchResultDetailActivity.this);
												Intent intent= new Intent();
												intent.setClass( mContext, SearchResultDetailActivity.class);
												intent.putExtra( Constants.EXTRA_SEARCHMODE, Constants.SEARCH_BUSLINE );
												intent.putExtra( Constants.EXTRA_BUSLINE, busstopListResponse.mBusLine);
												mContext.startActivity(intent);
											}
								}); 
							}
						});
						AlertDialog alert = builder.create();
						alert.show();
					}
					else
					{
						//打电话
						String tel[] = mPoiItem.tel.split(";");
						Intent intent = new Intent("android.intent.action.DIAL",Uri.parse("tel:" + tel[0]));
						startActivity(intent);
					}
					
				}
			}
		});
		
		

	}
	
	/**
	 * 周边查询
	 */
	private void searchNear()
	{
		// 周边查询
		if(mSearchMode==Constants.SEARCH_BUSSTATION)
		{
			mCommonApplication.mGeoPoint = new GeoPoint( (int)(mBusStation.gPoint.lat * 1E6), (int)(mBusStation.gPoint.lon * 1E6)  );
		}
		else
		{
			mCommonApplication.mGeoPoint = new GeoPoint( (int)(mPoiItem.gPoint.lat * 1E6), (int)(mPoiItem.gPoint.lon * 1E6)  );
		}
		
		Intent intent = new Intent( SearchResultDetailActivity.this, NearbyKindActivity.class );
		intent.putExtra(Constants.EXTRA_SEARCHNEARBY, "arround");
		startActivity(intent);
		ActivityMgr.getActivityManager().popAllActivity();
    	finish();
	}
	
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
	protected void onPause()
	{
		super.onPause();
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

	protected POIItem busStation2POI( BusStation busStation )
	{
		POIItem poiItem = new POIItem();
		poiItem.gPoint = busStation.gPoint;
		poiItem.name = busStation.name;
		poiItem.id = Constants.MAPPOS_POI_DEFAULT_ID;
		return poiItem;
	}

	private final SensorEventListener mListener = new SensorEventListener()
	{
		@Override
		public void onSensorChanged(SensorEvent event)
		{
			if ( event.sensor.getType() == Sensor.TYPE_ORIENTATION )
			{
				mValues = event.values;
				if ( mValues != null )
				{
					mDirectionCompassView.setValues(mValues);
					mDirectionCompassView.invalidate();
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	};

	 public List<String> getDetail(POIItem poiItem){
		 poiItem.detail = new ArrayList<String>();
		 if (null!=poiItem.addr&&!"".equals(poiItem.addr)) 
		 {
			 poiItem.detail.add(this.getString(R.string.share_to_friend));
		 }
		 if (null!=poiItem.tel&&!"".equals(poiItem.tel)) 
		 {
			 poiItem.detail.add(poiItem.tel);
		 }
		 if (null!=poiItem.busline) 
		 {
			 //拆分公交
			 String busName=this.getString(R.string.along_bus);
			 busName+=poiItem.buslinename;
			 poiItem.detail.add(busName);
		 }
		 
	    	return poiItem.detail;
	    }
	 
	 /**
	  * 存入收藏
	  * @param typeId
	  * @param entityId
	  * @param entityName
	  */
	 private void saveFavorite(String typeId,String entityId,String entityName)
	 {
		 FavoritesData favorite = new FavoritesData();
		 favorite.mTypeId = typeId;
		 favorite.mCityCode = mCommonApplication.mCity.admincode;
		 favorite.mEntityId = entityId;
		 favorite.mEntityName = entityName;
		 favorite.mEntityRidingRoute = mRidingRoute;
		 int poinOrLine =(Integer.parseInt(typeId)==Constants.MAPMODE_VIEWPOI||Integer.parseInt(typeId)==Constants.MAPMODE_VIEWSTATION)?0:1;
		 int favoriteCount = getFavoriteCount(poinOrLine);
		 if (favoriteCount>100||favoriteCount==100) 
		 {
			 showToast(R.string.fav_full);
		 }
		 else 
		 {
			 SaveManager.saveData(Constants.TABLE_FAVORITES, favorite, 0);
			 mBusStation.favoriteflag = true;
			 mBusLine.favoriteflag = true;
			 mFavorButton.setBackgroundResource(R.drawable.icon_select_fav);
			 showToast(R.string.add_fav);
		 }
		
	 }
	 
	 private void saveFavorite_poi(String typeId,POIItem poiItem)
	 {
		 FavoritesData favorite = new FavoritesData();
		 favorite.mTypeId = typeId;
		 favorite.mCityCode = mCommonApplication.mCity.admincode;
		 favorite.mEntityId = poiItem.id;
		 favorite.mEntityName = poiItem.name;
		 favorite.mEntityRidingRoute = mRidingRoute;
		 favorite.mBusroute = poiItem.packageJson().toString();
		 int favoriteCount = getFavoriteCount(0);
		 if (favoriteCount>100||favoriteCount==100) 
		 {
			 showToast(R.string.fav_full);
		 }
		 else 
		 {
			 SaveManager.saveData(Constants.TABLE_FAVORITES, favorite, 0);
			 mPoiItem.favoriteflag = true;
			 mFavorButton.setBackgroundResource(R.drawable.icon_select_fav);
			 showToast(R.string.add_fav);
		 }
	 }
	 /**
	  * 删除收藏
	  * @param typeId
	  * @param entityId
	  */
	 private void delFavorite(String typeId,String entityId)
	 {
		 FavoritesData favorite = new FavoritesData();
		 favorite.mTypeId = typeId;
		 favorite.mCityCode = mCommonApplication.mCity.admincode;
		 favorite.mEntityId = entityId;
		 SaveManager.deleteData(Constants.TABLE_FAVORITES, favorite);
		 mBusStation.favoriteflag = false;
		 mBusLine.favoriteflag = false;
		 mPoiItem.favoriteflag = false;
		 mFavorButton.setBackgroundResource(R.drawable.icon_poi_favor);
		 showToast(R.string.delete_fav);
	 }
	 
	 /**
	  * 根据typeId和entityId查询在数据库中是否有该记录
	  * @param typeId
	  * @param entityId
	  * @return
	  */
	 private boolean getIsFavorite(String typeId,String entityId)
	 {
		 FavoritesData favorite = new FavoritesData();
		 favorite.mTypeId = typeId;
		 favorite.mCityCode = mCommonApplication.mCity.admincode;
		 favorite.mEntityId = entityId;
		 return SaveManager.isExistData(Constants.TABLE_FAVORITES, favorite);
		 
	 }
	 
	 /**
	  * 根据searchMode修改mMapDisplayMode
	  * @param searchMode
	  */
	private void getMapDisplayMode(int searchMode) 
	{
		switch (searchMode) 
		{
			case Constants.SEARCH_POI:
			case Constants.SEARCH_NEARBY:
				mMapDisplayMode = String.valueOf(Constants.MAPMODE_VIEWPOI);
				mRidingRoute = "地点";
				break;
			case Constants.SEARCH_BUSSTATION:
				mMapDisplayMode = String.valueOf(Constants.MAPMODE_VIEWSTATION);
				mRidingRoute = "公交站点";
				break;
			case Constants.SEARCH_BUSLINE:
				mMapDisplayMode = String.valueOf(Constants.MAPMODE_VIEWBUSLINE);
				mRidingRoute = "公交线路";
				break;
			default:
				break;
		}

	}
	
	private int getFavoriteCount(int poinOrLine)
	{
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select * from "+Constants.TABLE_FAVORITES);
		sql.append(" where 1=1 and " + FavoritesData.TYPEID + " in( ");
		if(poinOrLine == 0)
		{
			sql.append(Constants.MAPMODE_VIEWPOI +","+Constants.MAPMODE_VIEWSTATION);
		}
		else if(poinOrLine == 1)
		{
			sql.append(Constants.MAPMODE_VIEWBUSLINE+","+Constants.MAPMODE_BUSEXCHG+","+Constants.MAPMODE_FAVORITE_BUSEXCHG);
		}
		sql.append(")");
		
		int favoritesDataCount = SaveManager.getConditionCount(sql.toString());
		return favoritesDataCount;
	}
}
