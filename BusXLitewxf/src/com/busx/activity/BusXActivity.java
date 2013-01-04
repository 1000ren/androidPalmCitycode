package com.busx.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cm.framework.net.ClientSession;
import cm.framework.net.ControlRunnable;
import cm.framework.net.IResponseListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.busx.CommonApplication;
import com.busx.R;
import com.busx.activity.map.BusLineOverlay;
import com.busx.activity.map.BusRouteOverlay;
import com.busx.activity.map.BusStationOverlay;
import com.busx.activity.map.POIOverlay;
import com.busx.activity.view.CompassView;
import com.busx.common.StateAlert;
import com.busx.database.SaveManager;
import com.busx.entities.BusStation;
import com.busx.entities.City;
import com.busx.entities.GPoint;
import com.busx.entities.POIItem;
import com.busx.entities.RoutePathShp;
import com.busx.location.MyLocationOverlayProxy;
import com.busx.protocol.ProtocolDef;
import com.busx.protocol.city.GetAdmincodeRequest;
import com.busx.protocol.city.GetAdmincodeResponse;
import com.busx.protocol.trafficEvent.GetTrafficEventRequest;
import com.busx.protocol.trafficEvent.GetTrafficEventResponse;
import com.busx.utils.ClientAgent;
import com.busx.utils.Constants;
import com.busx.utils.DOMManager;
import com.busx.utils.NetUtil;
import com.busx.utils.Utils;


@SuppressWarnings("deprecation")
public class BusXActivity extends MapActivity 
{
	private DOMManager domManager=new DOMManager();
	
	private Context mContext = null;
	protected StateAlert mStateAlert = null;
	public CommonApplication mCommonApplication = null;

	public MapView mMapView = null;
	private MapController mMapController = null;
	private MyLocationOverlayProxy mLocationOverlay = null;
	private POIOverlay mPoiOverlay = null;
	private BusStationOverlay mBusStationOverlay = null;
	private BusLineOverlay mBusLineOverlay = null;
	private BusRouteOverlay mBusRouteOverlay = null;
	public View mPoiPopView = null;

	private TextView mSearchTextView;
	private ImageButton mRouteButton;
	private ImageButton mNearSearchButton;
	private ImageButton mLocButton;
	private ImageButton mListButton;
	private CompassView mCompassView;
	private SensorManager mSensorManager;
	private int mMapDisplayMode = Constants.MAPMODE_DEFAULT;
	private ClientAgent mClientAgent = null;
	private boolean[] mLayersSelected = {false};
	private String []mLayers = {"卫星图"};
	private ProgressDialog mProgressDialog = null;
	private Class<?> mActivityClass = null;
	private int mFirstLocation = 0;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        Command.init(mContext);

        requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView(R.layout.activity_busx);

        mStateAlert = new StateAlert(this);
        mStateAlert.setTitle( mContext.getResources().getString(R.string.app_name) );
        mCommonApplication = (CommonApplication)getApplicationContext();
		
        // init ctrl
        initCtrl();
        mClientAgent = new ClientAgent(BusXActivity.this,mStateAlert);
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() 
	{
		super.onStart();
	}


    @Override
	protected void onNewIntent(Intent arg0)
    {
    	// 针对 launchMode 为 singleTask时有效
		super.onNewIntent(arg0);
		Log.i( "BUSX", "onNewIntent" );
	}

	public void initCtrl()
    {
    	// init ctrl
        mMapView = (MapView) findViewById(R.id.mapView);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setVectorMap(true);
		mMapController = mMapView.getController();
		String[] lonlat = mCommonApplication.mCity.lonLats.split(",");
		GeoPoint point = new GeoPoint((int) (Double.parseDouble(lonlat[1])* 1E6), (int) (Double.parseDouble(lonlat[0])* 1E6));
		mMapController.setCenter(point);
		mMapController.setZoom( Constants.MAPSCALE_DEFAULT );
		mCommonApplication.mMapView = mMapView;

		// location
		mLocationOverlay = new MyLocationOverlayProxy(this, mMapView);
		mMapView.getOverlays().add(mLocationOverlay);
		mCommonApplication.mLocationOverlay = mLocationOverlay;
		// sensor
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		// TOP Bar
		mSearchTextView = (TextView) findViewById( R.id.TextViewSearch );
		mRouteButton = (ImageButton) findViewById( R.id.direct_nav );
		mNearSearchButton = (ImageButton) findViewById( R.id.direct_nearsearch );
		mLocButton = (ImageButton) findViewById( R.id.direct_loc );
		mListButton = (ImageButton) findViewById( R.id.ImageButtonList );
		mListButton.setVisibility( View.INVISIBLE );

		mCompassView = (CompassView) findViewById(R.id.compassView);

		mSearchTextView.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				mSearchTextView.setClickable(false);
				new Thread(new Runnable() 
				{
					@Override
					public void run() 
					{
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						startSearch();
					}
				}).start();
				
				
			}
		});

		mRouteButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				mRouteButton.setClickable(false);
				new Thread(new Runnable() 
				{
					@Override
					public void run() {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						startRoute( (POIItem)null );
					}
				}).start();
				
			}
		});

		mNearSearchButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				if (null==mCommonApplication.mLocationOverlay.getMyLocation()) 
				{
					NetUtil.openGPSSettings(mContext);
				}
				else 
				{
					
					mCommonApplication.mGeoPoint = mCommonApplication.mLocationOverlay.getMyLocation();
					mNearSearchButton.setClickable(false);
					new Thread(new Runnable() 
					{
						@Override
						public void run() {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							startNearby("myself");
						}
					}).start();
					
				}
				
            }
		});

		mLocButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mLocationOverlay.mMovetoLocation = true;
				if (null != mLocationOverlay && null != mLocationOverlay.getMyLocation())
				{
					getCityInfo();
				}
				else
				{
					NetUtil.openGPSSettings(mContext);
				}
			}
		});

		mListButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				mListButton.setClickable(false);
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
						listData();
					}
				}).start();
				
			}
		});
		
		//读取 all cities
		getCitiesData();
		initPoiPopView();
		
    }
	
	/**
	 * 从数据库中读取所有支持换乘查询数据的城市信息
	 */
	private void getCitiesData()
	{
		City[] city = null;
		int num = SaveManager.getCount(Constants.TABLE_CITY, null);
		if( num > 0)
		{
			city = new City[num];
			for(int i=0;i<num;i++)
			{
				city[i] = new City();
			}
			SaveManager.readAllData(Constants.TABLE_CITY, city);
		}
		mCommonApplication.mAllCities = city;
	}
	
	
	private void listData()
	{
		//查看是否是从收藏夹页面定位的
		if(mCommonApplication.mIsFavorite)
		{
			Intent intent = new Intent( BusXActivity.this, FavoriteActivity.class );
	        startActivity( intent );
		}
		else
		{
			int pageNum = mCommonApplication.mPageNum;
			mCommonApplication.mPageNum = 1;
			switch ( mCommonApplication.mMapDisplayMode )
			{
				case Constants.MAPMODE_DEFAULT:
				{
					//
					mListButton.setClickable(true);
					break;
				}
				case Constants.MAPMODE_VIEWPOI:
				case Constants.MAPMODE_VIEWPOIS:
				{
					Intent intent= new Intent();
					intent.setClass( BusXActivity.this, SearchResultListActivity.class );
					intent.putExtra( Constants.EXTRA_SEARCHMODE, mCommonApplication.mSearchMode );
					intent.putExtra( Constants.EXTRA_KEYWORD, mCommonApplication.mSearchKeyword );
					intent.putExtra( Constants.EXTRA_POIRES, mCommonApplication.mPoiRes );
					intent.putExtra(Constants.EXTRA_MTOTLE, mCommonApplication.mTotle);
					intent.putExtra(Constants.EXTRA_PAGENUM, pageNum);
					startActivity(intent);
					break;
				}
				case Constants.MAPMODE_VIEWSTATION:
				case Constants.MAPMODE_VIEWSTATIONS:
				{
					Intent intent= new Intent();
					intent.setClass( BusXActivity.this, SearchResultListActivity.class );
					intent.putExtra( Constants.EXTRA_SEARCHMODE, mCommonApplication.mSearchMode );
					intent.putExtra( Constants.EXTRA_KEYWORD, mCommonApplication.mSearchKeyword );
					intent.putExtra( Constants.EXTRA_BUSSTATIONRES, mCommonApplication.mBusStationRes );
					intent.putExtra(Constants.EXTRA_MTOTLE, mCommonApplication.mTotle);
					intent.putExtra(Constants.EXTRA_PAGENUM, pageNum);
					startActivity(intent);
					break;
				}
				case Constants.MAPMODE_VIEWBUSLINE:
				{
					
					Intent intent= new Intent();
					intent.setClass( BusXActivity.this, SearchResultListActivity.class );
					intent.putExtra( Constants.EXTRA_SEARCHMODE, mCommonApplication.mSearchMode );
					intent.putExtra( Constants.EXTRA_KEYWORD, mCommonApplication.mSearchKeyword );
					intent.putExtra( Constants.EXTRA_BUSLINERES, mCommonApplication.mBusLineRes );
					intent.putExtra(Constants.EXTRA_MTOTLE, mCommonApplication.mTotle);
					intent.putExtra( Constants.EXTRA_POIRES, mCommonApplication.mPoiRes );
					intent.putExtra(Constants.EXTRA_PAGENUM, pageNum);
					startActivity(intent);
					break;
				}
				case Constants.MAPMODE_BUSEXCHG:
				{
					Intent intent= new Intent();
					intent.setClass( BusXActivity.this, RouteBusResultActivity.class );
					intent.putExtra( Constants.EXTRA_BUSXRES, mCommonApplication.mBusXRes );
					intent.putExtra( Constants.EXTRA_STARTPOI, mCommonApplication.mStartPoiItem );
					intent.putExtra( Constants.EXTRA_DESTPOI, mCommonApplication.mDestPoiItem );
					intent.putExtra(Constants.EXTRA_MTOTLE, mCommonApplication.mTotle);
					startActivity(intent);
					break;
				}
				default:
				{
					mListButton.setClickable(true);
					break;
				}
			}
		}
	}

    public void initData()
    {
    	mMapDisplayMode = mCommonApplication.mMapDisplayMode;

    	switch ( mMapDisplayMode )
		{
			case Constants.MAPMODE_DEFAULT:
			{
				break;
			}
			case Constants.MAPMODE_VIEWPOI:
			{
				mListButton.setVisibility( View.VISIBLE );
				mHandler.sendEmptyMessage( Constants.MAPMODE_VIEWPOI );
				break;
			}
			case Constants.MAPMODE_VIEWPOIS:
			{
				mListButton.setVisibility( View.VISIBLE );
				mHandler.sendEmptyMessage( Constants.MAPMODE_VIEWPOIS );
				break;
			}
			case Constants.MAPMODE_VIEWSTATION:
			{
				mListButton.setVisibility( View.VISIBLE );
				mHandler.sendEmptyMessage( Constants.MAPMODE_VIEWSTATION );
				break;
			}
			case Constants.MAPMODE_VIEWSTATIONS:
			{
				mListButton.setVisibility( View.VISIBLE );
				mHandler.sendEmptyMessage( Constants.MAPMODE_VIEWSTATIONS );
				break;
			}
			case Constants.MAPMODE_VIEWBUSLINE:
			{
				mListButton.setVisibility( View.VISIBLE );
				mHandler.sendEmptyMessage( Constants.MAPMODE_VIEWBUSLINE );
				break;
			}
			case Constants.MAPMODE_BUSEXCHG:
			{
				mListButton.setVisibility( View.VISIBLE );
				mHandler.sendEmptyMessage( Constants.MAPMODE_BUSEXCHG );
				break;
			}
			case Constants.MAPMODE_OTHERCITYCENTER:
			{
				mListButton.setVisibility( View.GONE );
				break;
			}
			default:
			{
				break;
			}
		}
        if ( mMapDisplayMode == Constants.MAPMODE_DEFAULT )
		{
			mLocationOverlay.runOnFirstFix(new Runnable() 
			{
	            public void run() {
	            	mHandler.sendMessage(Message.obtain(mHandler, Constants.FIRST_LOCATION));
	            }
	        });
		}
    }

    public void initPoiPopView()
    {
    	mPoiPopView = super.getLayoutInflater().inflate(R.layout.popup_poi, null);
    	mMapView.addView(mPoiPopView, new MapView.LayoutParams(
    	MapView.LayoutParams.WRAP_CONTENT,
    	MapView.LayoutParams.WRAP_CONTENT, null,
    	MapView.LayoutParams.BOTTOM_CENTER));
    	mPoiPopView.setVisibility(View.GONE);
    	mPoiPopView.getBackground().setAlpha(200);
    	mPoiPopView.setOnClickListener( new OnClickListener()
    	{
			@Override
			public void onClick(View v)
			{
				Intent intent= new Intent();
				intent.setClass( BusXActivity.this, SearchResultDetailActivity.class );
				intent.putExtra( Constants.EXTRA_SEARCHMODE, mCommonApplication.mSearchMode );
				switch ( mCommonApplication.mSearchMode )
				{
					case Constants.SEARCH_POI:
					case Constants.SEARCH_NEARBY:
					{
						intent.putExtra( Constants.EXTRA_POIITEM, mCommonApplication.mPoiItem );
						break;
					}
					case Constants.SEARCH_BUSSTATION:
					{
						intent.putExtra( Constants.EXTRA_BUSSTATION, mCommonApplication.mBusStation );
						break;
					}
					default:
					{
						break;
					}
				}
				startActivity(intent);
			}
		});
    }

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mStateAlert.cancel();
		mSensorManager.unregisterListener(mListtenr);
		Log.i( "BUSX", "BusXActivity onDestroy" );
	}

	@Override
	protected void onPause() {
		this.mLocationOverlay.disableMyLocation();
		super.onPause();
	}

	@Override
	protected void onRestart()
	{
		super.onRestart();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		this.mLocationOverlay.enableMyLocation();
		ClientSession.getInstance().setDefErrorReceiver(mStateAlert);
		ClientSession.getInstance().setDefStateReceiver(mStateAlert);
		if(mCommonApplication.mIsBack)
		{
			mCommonApplication.mIsBack = false;
		}
		else
		{
			clearOverlay();
	        // init data
	        initData();
		}
		mSensorManager.registerListener(mListtenr, SensorManager.SENSOR_ORIENTATION, SensorManager.SENSOR_DELAY_UI);
		
		mRouteButton.setClickable(true);
		mNearSearchButton.setClickable(true);
		mListButton.setClickable(true);
		mSearchTextView.setClickable(true);

		Log.i( "BUSX", "BusXActivity onResume" );
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		ClientSession.getInstance().setDefErrorReceiver(mStateAlert);
		ClientSession.getInstance().setDefStateReceiver(mStateAlert);
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
        	AppExit();
    		
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_HOME
                && event.getRepeatCount() == 0)
        {
            System.exit(0);
        }
        return super.onKeyDown(keyCode, event);
    }
	@Override
    public boolean onPrepareOptionsMenu(Menu menu)
	{
        boolean result = super.onPrepareOptionsMenu(menu);

        menu.clear();
        //搜索
    	Command.addMenuItem(menu, Command.MENU_SEARCH);
    	//线路
        Command.addMenuItem(menu, Command.MENU_ROUTE);
        //周边
        Command.addMenuItem(menu, Command.MENU_NEARBY);
        //出行提示
        Command.addMenuItem(menu, Command.MENU_TRANSFERINFO);
        //清空
        Command.addMenuItem(menu, Command.MENU_CLEAR);
        /**更多**/
        //天气
        Command.addMenuItem(menu, Command.MENU_WEATHER);
        //切换城市
        Command.addMenuItem(menu, Command.MENU_CHANGECITY);
        //收藏
        Command.addMenuItem(menu, Command.MENU_FAVOR);
        //图层
        Command.addMenuItem(menu, Command.MENU_LAYER);
        //检查更新
        Command.addMenuItem(menu, Command.MENU_APPUPDATE);
        //关于我们
        Command.addMenuItem(menu, Command.MENU_ABOUT);
        //退出
        Command.addMenuItem(menu, Command.MENU_EXIT);

        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int iSelectedCMD = item.getItemId();

        switch (iSelectedCMD) 
        {
        	case Command.MENU_SEARCH:
	        {
	        	startSearch();
	        	break;
	        }
        	case Command.MENU_ROUTE:
        	{
        		startRoute( (POIItem)null );
        		break;
        	}
        	case Command.MENU_NEARBY:
        	{
				if (null==mCommonApplication.mLocationOverlay.getMyLocation()) 
				{
					NetUtil.openGPSSettings(mContext);
				}
				else 
				{
					mCommonApplication.mGeoPoint = mCommonApplication.mLocationOverlay.getMyLocation();
					startNearby("myself");
				}
				break;
        	}
        	case Command.MENU_CLEAR:
        	{
        		ClearAll();
        		break;
        	}
        	case Command.MENU_LAYER:
        	{
        		startLayerMgr();
        		break;
        	}
        	case Command.MENU_FAVOR:
        	{
        		startFavor();
        		break;
        	}
        	case Command.MENU_APPUPDATE:
        	{
        		Intent intent = new Intent( BusXActivity.this, PackageInstallerActivity.class );
        		startActivity( intent );
        		break;
        	}
        	case Command.MENU_ABOUT:
        	{
        		Intent intent = new Intent( BusXActivity.this, AboutUsActivity.class );
                startActivity( intent );
        		break;
        	}
        	case Command.MENU_EXIT:
        	{
        		AppExit();
        		break;
        	}
        	case Command.MENU_WEATHER:
        	{
        		startWeather();
        		break;
        	}
        	case Command.MENU_CHANGECITY:
        	{
        		startChangeCity();
        		break;
        	}
        	case Command.MENU_TRANSFERINFO:
        	{
        		startTransferInfo();
        		break;
        	}
        	default:
        	{
        		break;
        	}
        }
        return true;
    }
    
    

    @Override
	public boolean onTouchEvent(MotionEvent event)
    {
		mMapController.stopAnimation( true );
		return super.onTouchEvent(event);
	}

	/**
	 * 退出程序
	 */
    public void AppExit()
    {
        new AlertDialog.Builder(mContext).setTitle( R.string.app_name ).setMessage(
        		mContext.getResources().getString( R.string.app_exit )).setPositiveButton( R.string.ok,
        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 {
                	//记住本次城市
                	 SharedPreferences sharedPreferences= mContext.getSharedPreferences("mCitycode", Context.MODE_PRIVATE);//
                	 Editor editor = sharedPreferences.edit();
                	 editor.putString("adminname", mCommonApplication.mCity.adminname);
                	 editor.putString("admincode", mCommonApplication.mCity.admincode);
                	 editor.putString("adminnamep", mCommonApplication.mCity.adminnamep);
                	 editor.putString("provincecode", mCommonApplication.mCity.provincecode);
                	 editor.putString("lonLats", mCommonApplication.mCity.lonLats);
                	 editor.commit();	
					// 统计软件使用时长
					String endtime = Utils.getNowTime();
					long usetime = Long.parseLong(endtime)
							- Long.parseLong(mCommonApplication.mbeginTime);

					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("begin_time",
							mCommonApplication.mbeginTime);
					paramMap.put("end_time", endtime);
					paramMap.put("use_time", usetime + "");

					Intent intent = new Intent();
	               	String busxUseTtime = mClientAgent.getBusxUseTtime("busx_use_time", paramMap);
	               	String sid = mCommonApplication.mUserLoginInfo.sid;
	               	intent.putExtra("busxUseTtime", busxUseTtime);
	               	intent.putExtra("sid", sid);
	               	intent.setAction("com.busx.service.ClientAgentService");
	               	startService(intent);
	               	
             		System.exit(0);
                }
            }
        }).setNegativeButton( R.string.cancel,
        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
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

    public void startSearch()
    {
    	Intent intent = new Intent( BusXActivity.this, SearchActivity.class );
        startActivity( intent );
    }

    public void startRoute( POIItem destPoiItem )
    {
    	GeoPoint gpt = mLocationOverlay.getMyLocation();
		Intent intent= new Intent();
		intent.setClass( BusXActivity.this, RouteActivity.class );
		if ( gpt != null )
		{
			POIItem poiItem = new POIItem();
			GPoint gPoint = new GPoint( gpt.getLongitudeE6() * 1.0 / 1E6, gpt.getLatitudeE6() * 1.0 / 1E6 );
			poiItem.gPoint = gPoint;
			poiItem.name = Constants.MAPPOS_POI_CUR_NAME;
			poiItem.id = Constants.MAPPOS_POI_DEFAULT_ID;
			intent.putExtra( Constants.EXTRA_STARTPOI, poiItem );
		}
		if ( destPoiItem != null )
		{
			intent.putExtra( Constants.EXTRA_DESTPOI, destPoiItem );
		}
		startActivity(intent);
    }
    
    public void startRoute( BusStation destBusStation )
    {
    	GeoPoint gpt = mLocationOverlay.getMyLocation();
		Intent intent= new Intent();
		intent.setClass( BusXActivity.this, RouteActivity.class );
		if ( gpt != null )
		{
			POIItem poiItem = new POIItem();
			GPoint gPoint = new GPoint( gpt.getLongitudeE6() * 1.0 / 1E6, gpt.getLatitudeE6() * 1.0 / 1E6 );
			poiItem.gPoint = gPoint;
			poiItem.name = Constants.MAPPOS_POI_CUR_NAME;
			poiItem.id = Constants.MAPPOS_POI_DEFAULT_ID;
			intent.putExtra( Constants.EXTRA_STARTPOI, poiItem );
		}
		if ( destBusStation != null )
		{
			POIItem poiItem = new POIItem();
			poiItem.gPoint = destBusStation.gPoint;
			poiItem.name = destBusStation.name;
			poiItem.id = Constants.MAPPOS_POI_DEFAULT_ID;
			intent.putExtra( Constants.EXTRA_DESTPOI, poiItem );
		}
		startActivity(intent);
    }

    public void startNearby(String type)
    {
    	
		Intent intent = new Intent( BusXActivity.this, NearbyKindActivity.class );
		if(type.equals("myself"))
		{
			intent.putExtra(Constants.EXTRA_SEARCHNEARBY, type);
		}
		else
		{
			intent.putExtra(Constants.EXTRA_SEARCHNEARBY, "arround");
		}
		mMapController.animateTo(mCommonApplication.mGeoPoint);
		startActivity(intent);
    }

    public void ClearAll()
    {
    	if ( mPoiOverlay != null )
    	{
    		mMapView.getOverlays().remove( mPoiOverlay );
    		mPoiOverlay = null;
    	}
    	if ( mBusRouteOverlay != null )
    	{
    		mMapView.getOverlays().remove( mBusRouteOverlay );
        	mBusRouteOverlay = null;
    	}
    	if ( mBusStationOverlay != null )
    	{
    		mMapView.getOverlays().remove( mBusStationOverlay );
    		mBusStationOverlay = null;
		}
    	if ( mBusLineOverlay != null )
    	{
    		mMapView.getOverlays().remove( mBusLineOverlay );
    		mBusLineOverlay = null;
    	}
    	mCommonApplication.mPoiItem = null;
    	mCommonApplication.mPoiRes = null;
    	mCommonApplication.mBusRoute = null;
    	mCommonApplication.mBusXRes = null;
    	mCommonApplication.mBusStation = null;
    	mCommonApplication.mBusStationRes = null;
    	mCommonApplication.mBusLine = null;
    	mCommonApplication.mBusLineRes = null;
    	mListButton.setVisibility(View.GONE);
    	mPoiPopView.setVisibility(View.GONE);
		mMapView.invalidate();
		mCommonApplication.mMapDisplayMode = Constants.MAPMODE_OTHERCITYCENTER;

    }
    
    /**
     * 清除上一次的查询结果
     */
    public void clearOverlay()
    {
    	if ( mPoiOverlay != null )
    	{
    		mMapView.getOverlays().remove( mPoiOverlay );
    		mPoiOverlay = null;
    	}
    	if ( mBusRouteOverlay != null )
    	{
    		mMapView.getOverlays().remove( mBusRouteOverlay );
        	mBusRouteOverlay = null;
    	}
    	if ( mBusStationOverlay != null )
    	{
    		mMapView.getOverlays().remove( mBusStationOverlay );
    		mBusStationOverlay = null;
		}
    	if ( mBusLineOverlay != null )
    	{
    		mMapView.getOverlays().remove( mBusLineOverlay );
    		mBusLineOverlay = null;
    	}
    	mPoiPopView.setVisibility(View.GONE);
    	mMapView.invalidate();
    }

    public void startLayerMgr()
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(BusXActivity.this);
		builder.setTitle("图层");
                
		builder.setMultiChoiceItems(mLayers, mLayersSelected, new DialogInterface.OnMultiChoiceClickListener() 
		{  
	          @Override  
	          public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) 
	          {
	        	  mLayersSelected[which]=isChecked;
	          }  
	     });
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialogInterface, int which) 
            {
            	if (mLayersSelected[0]) 
            	{
            		mMapView.setVectorMap(false);
			    	mMapView.setSatellite(true);
				}
            	else 
            	{
            		mMapView.setSatellite(false); 
			    	mMapView.setVectorMap(true);
				}
            }  
        });  
		builder.create();
		builder.show();
    }
    
    public void startFavor()
    {
    	Intent intent = new Intent( BusXActivity.this, FavoriteActivity.class );
        startActivity( intent );
        
    }

    public void showPoiPopView( final GeoPoint pt )
    {
		mMapView.updateViewLayout(mPoiPopView, new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, pt, MapView.LayoutParams.BOTTOM_CENTER));
		mPoiPopView.setVisibility(View.VISIBLE);

		TextView poiName = (TextView) findViewById(R.id.PoiName);
		TextView poiAddr = (TextView) findViewById(R.id.PoiAddress);
		if ( mCommonApplication.mSearchMode == Constants.SEARCH_POI|| mCommonApplication.mSearchMode == Constants.SEARCH_NEARBY)
		{
			poiName.setText( mCommonApplication.mPoiItem.name );
			poiAddr.setText( mCommonApplication.mPoiItem.addr );
		}
		else if ( mCommonApplication.mSearchMode == Constants.SEARCH_BUSSTATION) 
		{
			poiName.setText( mCommonApplication.mBusStation.name );
			poiAddr.setText( mCommonApplication.mBusStation.buslinename );
		}
		ImageView imgViewLeft = (ImageView) findViewById(R.id.ImageButtonLeft);
		imgViewLeft.setOnClickListener( new View.OnClickListener()
		{
			public void onClick(View v)
			{
				mCommonApplication.mGeoPoint = pt;
				mHandler.sendEmptyMessage(Constants.FUNC_NEARBYSEARCH);
			}
		});
    }

    public void showPoi()
    {
    	clearOverlay();
    	if ( mCommonApplication.mPoiRes != null && mCommonApplication.mPoiRes.mPoiList.size() > 0 )
    	{
    		Resources resources = getResources();
    		Drawable marker = getResources().getDrawable(R.drawable.icon_marker_red);
        	marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker
    				.getIntrinsicHeight());
        	mPoiOverlay = new POIOverlay(marker, 
        			mContext, mCommonApplication.mPoiRes.mPoiList,resources,mCommonApplication.mSearchMode);
    		mMapView.getOverlays().add( mPoiOverlay );
    		
    		if ( mCommonApplication.mMapDisplayMode == Constants.MAPMODE_VIEWPOI || 
    				mCommonApplication.mMapDisplayMode == Constants.MAPMODE_VIEWPOIS )
    		{
    			GeoPoint pt = new GeoPoint( (int)(mCommonApplication.mPoiItem.gPoint.lat * 1E6), 
            			(int)(mCommonApplication.mPoiItem.gPoint.lon * 1E6) );
            	mMapController.animateTo( pt );
            	showPoiPopView( pt );
    		}
    	}
    }

    public void showBusStation()
    {
    	clearOverlay();
    	if ( mCommonApplication.mBusStationRes != null && mCommonApplication.mBusStationRes.mBusStationList.size() > 0 )
    	{
    		Resources resources = getResources();
    		Drawable marker = getResources().getDrawable(R.drawable.icon_marker_red);
        	marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        	mBusStationOverlay = new BusStationOverlay(marker,mContext,mCommonApplication.mBusStationRes.mBusStationList,resources);
    		mMapView.getOverlays().add( mBusStationOverlay );

    		if ( mCommonApplication.mMapDisplayMode == Constants.MAPMODE_VIEWSTATION || 
    				mCommonApplication.mMapDisplayMode == Constants.MAPMODE_VIEWSTATIONS )
    		{
    			GeoPoint pt = new GeoPoint( (int)(mCommonApplication.mBusStation.gPoint.lat * 1E6), 
            			(int)(mCommonApplication.mBusStation.gPoint.lon * 1E6) );
            	mMapController.animateTo( pt );
            	showPoiPopView( pt );
    		}
    	}
    }

    public void showBusLine()
    {
    	clearOverlay();
    	if ( mCommonApplication.mBusLine != null && mCommonApplication.mBusLine.gPoints.size() > 0 )
    	{
    		Resources resources = getResources();
    		Drawable marker = getResources().getDrawable(R.drawable.icon_marker_red);
        	marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker
    				.getIntrinsicHeight());
        	mBusLineOverlay = new BusLineOverlay(marker,mContext, mCommonApplication.mBusLine,resources);
    		mMapView.getOverlays().add( mBusLineOverlay );
    		setFitView(mCommonApplication.mBusLine.gPoints);
    	}
    }

    public void showBusRoute()
    {
    	clearOverlay();
    	if ( mCommonApplication.mBusRoute != null )
    	{
    		mBusRouteOverlay = new BusRouteOverlay( mContext, mCommonApplication.mBusRoute, mCommonApplication.mStartPoiItem, mCommonApplication.mDestPoiItem );
        	mMapView.getOverlays().add( mBusRouteOverlay );
        	
			List<GPoint> gPoints=new ArrayList<GPoint>();
			gPoints.add(mCommonApplication.mStartPoiItem.gPoint);
			gPoints.add(mCommonApplication.mDestPoiItem.gPoint);
			for (RoutePathShp routePathShp:mCommonApplication.mBusRoute.pathShpList) 
			{
				if (null!=routePathShp.pointList) 
				{
					gPoints.addAll(routePathShp.pointList);
				}
				
			}
			setFitView(gPoints);
		}
    }
    
    public void setFitView(List<GPoint> gPoints)
    {
    	List<GeoPoint> pts = new ArrayList<GeoPoint>();
		
		for (GPoint gPoint : gPoints) 
		{
			GeoPoint geoPoint = new GeoPoint( (int)(gPoint.lat*1E6), (int)(gPoint.lon*1E6) );
			pts.add(geoPoint);
		}
		mMapView.getController().setFitView(pts);//调整地图显示范围
		mMapView.invalidate();
    }

    /**
     * 查询天气
     */
    public void startWeather()
    {

    	mProgressDialog = ProgressDialog.show(BusXActivity.this, getString(R.string.app_name), "请稍候...", true);
    	
	      new Thread()
	      {
	         public void run()
	         {
	        	 try 
	        	 {
	     	    	//天气信息
	     	    	mCommonApplication.mWeatherData = domManager.getWeatherData(mCommonApplication.mCity.admincode);
	     	    	if ("-1".equals(mCommonApplication.mWeatherData .id)) 
	     	    	{
	     	    		mProgressDialog.dismiss();
	     	    		mHandler.sendMessage(mHandler.obtainMessage(Constants.TOAST, "暂不支持该城市数据"));
						return;
					}
	     	    	else if ("-2".equals(mCommonApplication.mWeatherData .id)) 
	     	    	{
	     	    		mProgressDialog.dismiss();
	     	    		mHandler.sendMessage(mHandler.obtainMessage(Constants.TOAST, "获取天气数据失败，请确认网络和SD卡"));
						return;
					}
	     	    	else 
	     	    	{
	     	    		mActivityClass = WeatherActivity.class;
		        		mHandler.sendEmptyMessage(Constants.STARTACTIVITY);
					}
	         		
	        	 
	     		} 
	         	catch (Exception e) 
	         	{
	         		mProgressDialog.dismiss();
	     		}
	        	
	         }
	      }.start();
	
    }
    /**
     * 切换城市
     */
    public void startChangeCity()
    {
    	Intent intent = new Intent( BusXActivity.this, ProvinceActivity.class );
        startActivity( intent );
    }
    /**
     * 出行信息
     */
    public void startTransferInfo()
    {
    	ClientSession.getInstance().asynGetResponse(new GetTrafficEventRequest(0, ProtocolDef.PAGE_SIZE, mCommonApplication.mUserLoginInfo.sid, mCommonApplication.mCity.admincode),
				new IResponseListener()
		{
					@Override
					public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2) 
					{
						GetTrafficEventResponse trafficEventResponse = (GetTrafficEventResponse) arg0;
						mCommonApplication.mTrafficEventList = trafficEventResponse.mTrafficEventList;
						Intent intent = new Intent();
						intent.setClass(BusXActivity.this, TrafficEventActivity.class);
						startActivity(intent);
					}
		});
    
    }
	
    public final SensorListener mListtenr = new SensorListener()
    {
		 
        @Override
        public void onSensorChanged(int sensor, float[] values)
        {
            synchronized (this)
            {
            	mCompassView.setValue(values);
                if (Math.abs(values[0] - 0.0f) < 1)
                    return;
                if (mCompassView != null) {
                	mCompassView.invalidate();
                }
            }
        }
 
        @Override
        public void onAccuracyChanged(int sensor, int accuracy) {}
    };
    
    /**
     * 获取当前城市和上次登陆城市信息，并对比判断
     */
    private void getCityInfo()
    {
    	mLocationOverlay.mMovetoLocation = true;
    	ClientSession.getInstance().setDefErrorReceiver(null);
		ClientSession.getInstance().setDefStateReceiver(null);
//    	GPoint gPoint = new GPoint(111.978149,40.848619);
		//根据当前定位的经纬度  获取城市信息
		GPoint gPoint = new GPoint( mLocationOverlay.getMyLocation().getLongitudeE6() * 1.0 / 1E6, mLocationOverlay.getMyLocation().getLatitudeE6() * 1.0 / 1E6 );
    	ClientSession.getInstance().asynGetResponse(
				new GetAdmincodeRequest(gPoint.lon+"",gPoint.lat+"",mCommonApplication.mUserLoginInfo.sid),
				new IResponseListener()
		{
					@Override
					public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2)
					{
						GetAdmincodeResponse admincodeResponse = (GetAdmincodeResponse) arg0;
						//城市发生变化
						if (!admincodeResponse.mLocationCity.admincode.equals(mCommonApplication.mCity.admincode)) 
						{
							//查看当前位置所在城市，是否有换乘数据支持
							if(null != mCommonApplication.mAllCities && mCommonApplication.mAllCities.length>0)
							{
								boolean isInData = false;
								for(City city : mCommonApplication.mAllCities)
								{
									if(city.admincode.equals(admincodeResponse.mLocationCity.admincode))
									{
										mCommonApplication.mCity = city;
										isInData = true;
										break;
									}
								}
								String str = "";
								if(isInData)
								{
									str = "自动为您切换到"+mCommonApplication.mCity.adminname;
								}
								else
								{
									str = "您在"+admincodeResponse.mLocationCity.adminname+",暂不提供公交查询";
								}
								Message msg = new Message();
								msg.what = Constants.TOAST;
								msg.obj = str;
								mHandler.sendMessage(msg);
							}
						} 
						mMapController.animateTo(mLocationOverlay.getMyLocation());
					}
		});
    }
    
    
	public Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch ( msg.what )
			{
				case Constants.FIRST_LOCATION:
				{
					if(null != mLocationOverlay && null != mLocationOverlay.getMyLocation())
					{
						if (mFirstLocation==0) 
						{
							getCityInfo();
							mFirstLocation++;
						}
						else 
						{
							mMapController.animateTo(mLocationOverlay.getMyLocation());
						}
					}
					break;
				}
				case Constants.MAPMODE_VIEWPOI:
				case Constants.MAPMODE_VIEWPOIS:
				{
					showPoi();
					break;
				}
				case Constants.MAPMODE_VIEWSTATION:
				case Constants.MAPMODE_VIEWSTATIONS:
				{
					showBusStation();
					break;
				}
				case Constants.MAPMODE_VIEWBUSLINE:
				{
					showBusLine();
					break;
				}
				case Constants.MAPMODE_BUSEXCHG:
				{
					showBusRoute();
					break;
				}
				case Constants.FUNC_BUSEXCHANGE:
				{
					if ( mCommonApplication.mPoiItem != null )
					{
						startRoute( mCommonApplication.mPoiItem );
					}
					else if ( mCommonApplication.mBusStation != null )
					{
						startRoute( mCommonApplication.mBusStation );
					}
					break;
				}
				case Constants.FUNC_NEARBYSEARCH:
				{
					startNearby("");
					break;
				}
				case Constants.FUNC_TEXT_AUTOCOMPLETE:
				{
					ClientSession.getInstance().setDefStateReceiver(mStateAlert);
					ClientSession.getInstance().setDefErrorReceiver(mStateAlert);
					break ;
				}
				case Constants.STARTACTIVITY:
				{
					mProgressDialog.dismiss();
					Intent intent = new Intent( BusXActivity.this, mActivityClass );
	     	        startActivity( intent );
	     	        break;
				}
				case Constants.TOAST:
				{
					Toast.makeText(BusXActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
					break;
				}
				case Constants.INGATHER_EVENT:
				{
					
				}
				default:
				{
					break;
				}
			}
		}
    };
    
}