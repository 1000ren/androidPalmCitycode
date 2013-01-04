package com.busx;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.res.AssetManager;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.poisearch.PoiPagedResult;
import com.busx.entities.AppUpdateInfo;
import com.busx.entities.BusLine;
import com.busx.entities.BusLineRes;
import com.busx.entities.BusRoute;
import com.busx.entities.BusStation;
import com.busx.entities.BusStationRes;
import com.busx.entities.BusXRes;
import com.busx.entities.City;
import com.busx.entities.LayerItem;
import com.busx.entities.NearbyKind;
import com.busx.entities.NearbyKindItemRes;
import com.busx.entities.POIItem;
import com.busx.entities.POIRes;
import com.busx.entities.RoutePosOptItem;
import com.busx.entities.TerminalInfo;
import com.busx.entities.TrafficEvent;
import com.busx.entities.UserLoginInfo;
import com.busx.entities.WeatherData;
import com.busx.location.MyLocationOverlayProxy;
import com.busx.utils.Constants;
import com.busx.utils.DOMManager;

/**
 * @author liujingzhou
 *
 */
public class CommonApplication extends Application
{
	// user login info
	public UserLoginInfo mUserLoginInfo = null;
	// app update info
	public AppUpdateInfo mAppUpdateInfo = null;
	// terminal info
	public TerminalInfo mTerminalInfo = new TerminalInfo();

	// 图层设置
	public List<LayerItem> mLayerItemList = null;

	// 位置选择项
	public List<RoutePosOptItem> mRoutePosOptList = null;
	
	//加载所有的 周边分类
	public List<NearbyKind> mNearbyKinds=null;

	// 当前地图显示模式
	public int mMapDisplayMode = Constants.MAPMODE_DEFAULT;

	// 地图对象
	public MapView mMapView = null;
	// 当前位置
	public MyLocationOverlayProxy mLocationOverlay = null;

	public GeoPoint mGeoPoint = null;
	
	// 当前查询条件
	public int mSearchMode = Constants.SEARCH_POI;
	public String mSearchKeyword = null;

	// 当前显示的POI
	public POIItem mPoiItem = null;
	// 当前查询的POIS
	public POIRes mPoiRes = null;
	public PoiPagedResult mPoiPagedResult = null;
	//用户收藏查询，地点还是线路，0:地点、1:线路
	public int mFavoritetype = 0;
	
	//是否是收藏夹上的点在地图定位的
	public boolean mIsFavorite = false;
	
	//判断是否是返回键
	public boolean mIsBack = false;
	
	// 当前显示的站点
	public BusStation mBusStation = null;
	// 当前查询的站点
	public BusStationRes mBusStationRes = null;
	//当前查询的记录条数
	public Integer mTotle = null;
	//当前页数，默认为第一页
	public Integer mPageNum = 1;

	// 当前显示的路线
	public BusLine mBusLine = null;
	// 当前查询的线路
	public BusLineRes mBusLineRes = null;

	// 当前显示的换乘线路
	public POIItem mStartPoiItem = null;
	public POIItem mDestPoiItem = null;
	public BusXRes mBusXRes = null;
	public BusRoute mBusRoute = null;
	
	//天气信息
	public WeatherData mWeatherData = null;
	//尾号限行
	public String mCarNumData ="";
	//出行信息
	public List<TrafficEvent> mTrafficEventList = null;
	//出行信息
	public TrafficEvent mTrafficEvent = null;
	//城市
	public City mCity = null;
	public City[] mAllCities=null;
	//
	//开始时间（统计软件使用时长时使用）
	public String mbeginTime = ""; 
	// 当前周边列表
	public NearbyKindItemRes mNearbyKindItemRes = null;

	@Override
	public void onCreate()
	{
		super.onCreate();

		// init login info
		initLoginInfo();

		// init data
		initLayers();
		initRoutePosOpts();
		initNearbyKinds();
	}

	public void initLoginInfo()
	{
		mUserLoginInfo = new UserLoginInfo();		
	}

	public void initLayers()
	{
		mLayerItemList = new ArrayList<LayerItem>();

		LayerItem layerItem = new LayerItem();
		layerItem.id = 1;
		layerItem.name = "收藏的地点";
		layerItem.icon = R.drawable.icon_class_favor;
		mLayerItemList.add( layerItem );

		/*
		LayerItem layerItem = new LayerItem();
		layerItem.id = 2;
		layerItem.name = "路况";
		layerItem.icon = R.drawable.icon_class_its;
		layerItem.open = false;
		mLayerItemList.add( layerItem );
		*/
	}

	public void initRoutePosOpts()
	{
		mRoutePosOptList = new ArrayList<RoutePosOptItem>();

		RoutePosOptItem optItem = new RoutePosOptItem();
		optItem.id = 1;
		optItem.name = "使用我的位置";
		optItem.icon = R.drawable.icon_myloc;
		mRoutePosOptList.add( optItem );

		
		optItem = new RoutePosOptItem();
		optItem.id = 3;
		optItem.name = "在地图上选取";
		optItem.icon = R.drawable.icon_mapselected;
		mRoutePosOptList.add( optItem );
		

	}
	
	public void initNearbyKinds()
	{
		AssetManager assetManager = getAssets(); 
		
		try {
			mNearbyKinds=new DOMManager().getNearby_kinds(assetManager);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	
	
	
}
