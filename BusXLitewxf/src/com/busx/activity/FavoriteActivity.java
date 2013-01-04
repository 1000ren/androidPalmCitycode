package com.busx.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import cm.framework.net.ClientSession;
import cm.framework.net.ControlRunnable;
import cm.framework.net.IErrorListener;
import cm.framework.net.IResponseListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;
import cm.framework.protocol.ErrorResponse;

import com.busx.R;
import com.busx.activity.base.BaseActivity;
import com.busx.data.FavoritesData;
import com.busx.database.SaveManager;
import com.busx.entities.BusRoute;
import com.busx.entities.BusRouteUserRec;
import com.busx.entities.BusRouteUserRecDetail;
import com.busx.entities.BusStation;
import com.busx.entities.City;
import com.busx.entities.GPoint;
import com.busx.entities.POIItem;
import com.busx.entities.POIRes;
import com.busx.protocol.path.GetRouteBusResultUserRecRequest;
import com.busx.protocol.path.GetRouteBusResultUserRecResponse;
import com.busx.protocol.poi.GetBusStationListRequest;
import com.busx.protocol.poi.GetBusStationListResponse;
import com.busx.protocol.poi.GetBusStopListRequest;
import com.busx.protocol.poi.GetBusStopListResponse;
import com.busx.utils.ActivityMgr;
import com.busx.utils.Constants;
import com.busx.utils.NetUtil;

public class FavoriteActivity extends BaseActivity
{
	private ListView mListView = null;
	private Button mFavoriteaddrTab = null;
	private ImageButton mFavoriteaddr = null;
	private Button mFavoritearouteTab = null;
	private ImageButton mFavoritearoute = null;
	private FavoriteAdapter mFavoriteAdapter = null;
	private List<FavoritesData> mList = null;
	private int mSearchMode;
	private Button mBtnCancelAll = null;
	private Button mBtnSelAll = null;
	private Button mBtnDelAll = null;
	private RelativeLayout mlayout=null;
	public HashMap<Integer,Boolean> mMap = null;
	private Map<String,String> mCityMap;
	
	private boolean isOnClick = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_favorite);
		
		mCityMap = new HashMap<String,String>();
		if(null != mCommonApplication.mAllCities && mCommonApplication.mAllCities.length>0)
		{
			for(City city :mCommonApplication.mAllCities)
			{
				mCityMap.put(city.admincode, city.adminname);
			}
		}
		
		mListView = (ListView)findViewById(R.id.ListView_favorite);
		mFavoriteaddr = (ImageButton)findViewById(R.id.btn_favorite_addr);
		mFavoriteaddrTab = (Button)findViewById(R.id.btn_favorite_tab1);
		mFavoritearoute = (ImageButton)findViewById(R.id.btn_favorite_route);
		mFavoritearouteTab = (Button)findViewById(R.id.btn_favorite_tab2);
		mBtnCancelAll = (Button)findViewById(R.id.btnCancelAll);
		mBtnSelAll = (Button)findViewById(R.id.btnSelAll);
		mBtnDelAll = (Button)findViewById(R.id.btnDelAll);
		//根据线路或者地点，取得数据库值
		mList = getListByTypeId(mCommonApplication.mFavoritetype);
		mFavoriteAdapter = new FavoriteAdapter(FavoriteActivity.this, mList,mMap,mCityMap);
		mListView.setAdapter(mFavoriteAdapter);
		mlayout=(RelativeLayout)findViewById(R.id.fav_bat_del);
		if (null!=mList&&mList.size()>0) 
		{
			mlayout.setVisibility(View.VISIBLE);
		}
		else 
		{
			mlayout.setVisibility(View.GONE);
		}
		mListView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				if(!NetUtil.isConnectingToInternet(mContext))
				 {
		     		 Message msg = new Message();
		     		 msg.what = Constants.FUNC_ALERT;
		     		 msg.obj = "网络错误,建议您检查网络连接后再试";
		     		 mHandler.sendMessage(msg);
		     		 return;
				 }
				//查看详细
				FavoritesData fd = mList.get(arg2);
				searchResult(fd,false);
			}
		});
		mFavoriteaddr.setOnClickListener(new favoriteaddr());
		mFavoriteaddrTab.setOnClickListener(new favoriteaddr());
		mFavoritearoute.setOnClickListener(new favoriteroute());
		mFavoritearouteTab.setOnClickListener(new favoriteroute());
		mBtnCancelAll.setOnClickListener(new BtnCancelAll());
		mBtnSelAll.setOnClickListener(new BtnSelAll());
		mBtnDelAll.setOnClickListener(new BtnDelAll());
		//设置选项卡
		setBackgroundResource(mCommonApplication.mFavoritetype);
		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessage(Constants.ERROR);
	}


	//用户收藏的地点
	class favoriteaddr implements OnClickListener
	{
		@Override
		public void onClick(View v) 
		{
			if (mCommonApplication.mFavoritetype == 1) 
			{
				mCommonApplication.mFavoritetype = 0;
				setBackgroundResource(mCommonApplication.mFavoritetype);
			}
			
		}
	}

	//用户收藏的线路
	class favoriteroute implements OnClickListener
	{
		@Override
		public void onClick(View v) 
		{
			if (mCommonApplication.mFavoritetype == 0) 
			{
				mCommonApplication.mFavoritetype = 1;
				setBackgroundResource(mCommonApplication.mFavoritetype);
			}
			
		}
	}
	
	/**
	 * 监听全部取消按钮
	 * @author wxf
	 *
	 */
	class BtnCancelAll implements OnClickListener
	{

		@Override
		public void onClick(View v) 
		{
			if(null != mList && mList.size()>0)
			{
				setAdaptChecked(false);
			}
		}
		
	}
	
	/**
	 * 监听全部选中按钮
	 * @author wxf
	 *
	 */
	class BtnSelAll implements OnClickListener
	{

		@Override
		public void onClick(View v) 
		{
			if(null != mList && mList.size()>0)
			{
				
				setAdaptChecked(true);
			}
		}
		
	}
	
	/**
	 * 监听删除按钮
	 * @author wxf
	 *
	 */
	class BtnDelAll implements OnClickListener
	{

		@Override
		public void onClick(View v) 
		{
			if(null != mList && mList.size()>0 && null != mMap && mMap.size()>0)
			{
				//存放要删除的对象
				List<FavoritesData> list = new ArrayList<FavoritesData>();
				//存放要删除的对象的位置
				List<Integer> numList = new ArrayList<Integer>();
				for(int i=0;i<mList.size();i++)
				{
					if(mMap.get(i))
					{
						list.add(mList.get(i));
						numList.add(i);
					}
				}
				//删除mMap和mList当中选中的数据
				if(null!= list && list.size()>0 && null != numList && numList.size()>0)
				{
					//数据库删除
					delData(list);
					//从小到大排序
					Collections.sort(numList);
					//倒序循环，删除
					for(int i=numList.size()-1;i>=0;i--)
					{
						int num = numList.get(i);
						mList.remove(num);
					}
					mFavoriteAdapter.setListData(mList);
					setAdaptChecked(false);
					showToast("删除成功");
					return;
				}
			}
			showToast("没有选中任何记录！");
		}
		
	}
	
	/**
	 * 删除指定数据
	 * @param list
	 */
	private void delData(List<FavoritesData> list)
	{
		for(int i=0;i<list.size();i++)
		{
			FavoritesData favoritesData = list.get(i);
			String sql = "delete from " + Constants.TABLE_FAVORITES +
					" where sid ="+favoritesData.mSid;
			SaveManager.updData(sql);
		}
	}
	
	/**
	 * 设置多选框选中与否
	 * @param isChecked
	 */
	private void setAdaptChecked(boolean isChecked)
	{
		setHashMap(isChecked);
		mFavoriteAdapter.setIsChecked(mMap);
		mFavoriteAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 设置多选框的HashMap
	 */
	private void setHashMap(boolean isChecked)
	{
		mMap = new HashMap<Integer,Boolean>();
		if(null != mList && mList.size()>0)
		{
			for(int i=0;i<mList.size();i++)
			{
				mMap.put(i, isChecked);
			}
		}
		
	}
	
	/**
	 * 设置选项卡
	 * @param mFavoritetype
	 */
	private void setBackgroundResource(int mFavoritetype)
	{
		mList = getListByTypeId(mFavoritetype); 
		mFavoriteAdapter.setListData(mList);
		mFavoriteAdapter.notifyDataSetChanged();
		if(mFavoritetype==1)
		{
			mFavoriteaddr.setBackgroundResource(R.drawable.btn_favor_no);
			mFavoritearoute.setBackgroundResource(R.drawable.btn_favor_selected);
		}
		else
		{
			mFavoriteaddr.setBackgroundResource(R.drawable.btn_favor_selected);
			mFavoritearoute.setBackgroundResource(R.drawable.btn_favor_no);
		}
		setAdaptChecked(false);
		if (null!=mList&&mList.size()>0) 
		{
			mlayout.setVisibility(View.VISIBLE);
		}
		else 
		{
			mlayout.setVisibility(View.GONE);
		}
		
	}
	
	/**
	 * 从数据库中查询用户保存到收藏夹的点或线信息
	 * @param poinOrLine 0:地点，1:线路
	 * @return
	 */
	public List<FavoritesData> getListByTypeId(int poinOrLine)
	{

		List<FavoritesData> list = null;
		FavoritesData[] favoriteData = null;
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
		
		if(favoritesDataCount > 0)
		{
			favoriteData = new FavoritesData[favoritesDataCount];
			for(int i=0;i<favoriteData.length;++i)
			{
				favoriteData[i] = new FavoritesData();
			}
			SaveManager.readConditionData(sql.toString(),favoriteData);
		}
		if(null != favoriteData && favoriteData.length>0)
		{
			list = new ArrayList<FavoritesData>();
			for(FavoritesData fd : favoriteData)
			{
				list.add(fd);
			}
		}
		
		return list;
	}
	
	/**
	 * 城市不同的情况下，进行提示
	 */
	private void cityChange(FavoritesData favorit)
	{
		if(!favorit.mCityCode.equals(mCommonApplication.mCity.admincode))
		{
			Message message = new Message();
			message.what = Constants.TOAST;
			message.obj = "您正在查看 "+ mCityMap.get(favorit.mCityCode) +" 的信息";
			mHandler.sendMessage(message);
		}
	}

	
	/**
	 * 根据收藏夹存储的站点或线路的ID，查询服务器的详细信息
	 * @param FavoritesData 实体类
	 * @param isDetail 查看地图看是查看详情
	 */
	public void searchResult(final FavoritesData favorit,final boolean isDetail)
	{
		if(isOnClick)
		{
			isOnClick = false;
			mCommonApplication.mIsFavorite = true;
			ActivityMgr.getActivityManager().pushActivity(FavoriteActivity.this);
			ClientSession.getInstance().setDefErrorReceiver(null);
	 		ClientSession.getInstance().setDefStateReceiver(null);
			int mapMode = Integer.parseInt(favorit.mTypeId.trim());
			mCommonApplication.mMapDisplayMode = mapMode;
			getMapDisplayMode(mapMode);
			mCommonApplication.mSearchMode = mSearchMode;
			
			if (mapMode==Constants.MAPMODE_VIEWPOI)
			{
	
				POIItem poiItem = getPoiItemByFavorite(favorit);
				mCommonApplication.mPoiItem = poiItem;
				mCommonApplication.mPoiRes = new POIRes();
				mCommonApplication.mPoiRes.mPoiList=new ArrayList<POIItem>();
				mCommonApplication.mPoiRes.mPoiList.add(poiItem);
				if(isDetail)
				{
					Intent intent =new Intent();
					intent.putExtra( Constants.EXTRA_POIITEM, mCommonApplication.mPoiItem);
					intent.putExtra( Constants.EXTRA_SEARCHMODE, Constants.SEARCH_POI );
					intent.setClass(mContext, SearchResultDetailActivity.class);
					mContext.startActivity(intent);
				}
				else
				{
					ActivityMgr.getActivityManager().popAllActivity();
					finish();
				}
				cityChange(favorit);
				
			}
			else if (mapMode==Constants.MAPMODE_VIEWSTATION)
			{
				ClientSession.getInstance().asynGetResponse(new GetBusStationListRequest( favorit.mEntityId.trim(),mCommonApplication.mUserLoginInfo.sid ,favorit.mCityCode ),
						new IResponseListener()
				{
							@Override
							public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2) 
							{
								GetBusStationListResponse busStationListResponse = (GetBusStationListResponse) arg0;
								mCommonApplication.mUserLoginInfo.copySID( busStationListResponse.mUserLoginInfo );
								mCommonApplication.mBusStationRes = busStationListResponse.mBusStationRes;
								BusStation busStation = busStationListResponse.mBusStationRes.mBusStationList.get(0);
								mCommonApplication.mBusStation = busStation;
								
								if(isDetail)
								{
									Intent intent =new Intent();
									intent.putExtra( Constants.EXTRA_BUSSTATION, mCommonApplication.mBusStation);
									intent.putExtra( Constants.EXTRA_SEARCHMODE, Constants.SEARCH_BUSSTATION );
									intent.setClass(mContext, SearchResultDetailActivity.class);
									mContext.startActivity(intent);
								}
								else
								{
									ActivityMgr.getActivityManager().popAllActivity();
									finish();
								}
								cityChange(favorit);
							}
				},new IErrorListener() {
					
					@Override
					public void onError(ErrorResponse arg0, BaseHttpRequest arg1,
							ControlRunnable arg2) {
						if(null != arg0)
						{
							Message msg = new Message();
							msg.obj = arg0.getErrorDesc();
							msg.what = Constants.FUNC_ALERT;
							mHandler.sendMessage(msg);
						}
						
					}
				});
			}
			else if (mapMode==Constants.MAPMODE_VIEWBUSLINE)
			{
				ClientSession.getInstance().asynGetResponse(new GetBusStopListRequest( 
						mCommonApplication.mUserLoginInfo.sid, favorit.mEntityId.trim() ,favorit.mCityCode),
						new IResponseListener()
				{
							@Override
							public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2)
							{
								GetBusStopListResponse busstopListResponse = (GetBusStopListResponse) arg0;
								mCommonApplication.mUserLoginInfo.copySID( busstopListResponse.mUserLoginInfo );
								mCommonApplication.mBusLine=busstopListResponse.mBusLine ;
								if(isDetail)
								{
									Intent intent =new Intent();
									intent.putExtra( Constants.EXTRA_BUSLINE, mCommonApplication.mBusLine);
									intent.putExtra( Constants.EXTRA_SEARCHMODE, Constants.SEARCH_BUSLINE );
									intent.setClass(mContext, SearchResultDetailActivity.class);
									mContext.startActivity(intent);
								}
								else
								{
									ActivityMgr.getActivityManager().popAllActivity();
									finish();
								}
								cityChange(favorit);
							}
				},new IErrorListener() {
					
					@Override
					public void onError(ErrorResponse arg0, BaseHttpRequest arg1,
							ControlRunnable arg2) {
						if(null != arg0)
						{
							Message msg = new Message();
							msg.obj = arg0.getErrorDesc();
							msg.what = Constants.FUNC_ALERT;
							mHandler.sendMessage(msg);
						}
						
					}
				});
			}
			else if (mapMode == Constants.MAPMODE_BUSEXCHG)
			{
				JSONObject object = new JSONObject();
				BusRoute busRoute = new BusRoute();
				POIItem startPoiItem = new POIItem();
				POIItem destPoiItem = new POIItem();
				
				try
				{
					object = new JSONObject(favorit.mBusroute);
					busRoute = busRoute.setJSONObjectToObject(object);
					object = new JSONObject(favorit.mStartPOIItem);
					startPoiItem = startPoiItem.setJSONObjectToObject(object);
					object = new JSONObject(favorit.mDestPOIItem);
					destPoiItem = destPoiItem.setJSONObjectToObject(object);
				}
				catch(JSONException e)
		    	{
		    		Log.d("packageJson",e.getMessage());
		    	}
				
				if(isDetail)
				{
					Intent intent= new Intent();
					intent.setClass( mContext, RouteBusResultDetailActivity.class );
					intent.putExtra( Constants.EXTRA_BUSROUTE, busRoute );
					intent.putExtra( Constants.EXTRA_STARTPOI, startPoiItem );
					intent.putExtra( Constants.EXTRA_DESTPOI, destPoiItem );
					mContext.startActivity(intent);
				}
				else
				{
					mCommonApplication.mBusRoute = busRoute;
					mCommonApplication.mMapDisplayMode = Constants.MAPMODE_BUSEXCHG;
					mCommonApplication.mStartPoiItem = startPoiItem;
					mCommonApplication.mDestPoiItem = destPoiItem;
					ActivityMgr.getActivityManager().popAllActivity();
					finish();
				}
				cityChange(favorit);
				
			}
			else if(mapMode == Constants.MAPMODE_FAVORITE_BUSEXCHG)
			{
				final FavoritesData favoritFinal = favorit;
				ClientSession.getInstance().asynGetResponse(
						new GetRouteBusResultUserRecRequest( mCommonApplication.mUserLoginInfo.sid, favoritFinal.mEntityId ),
						new IResponseListener() {
							@Override
							public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,
									ControlRunnable arg2) {
	
								GetRouteBusResultUserRecResponse pathListResponse = (GetRouteBusResultUserRecResponse) arg0;
								mCommonApplication.mUserLoginInfo.copySID( pathListResponse.mUserLoginInfo );
								BusRouteUserRec mBusRouteUserRec = pathListResponse.mBusRouteUserRec;
								if(null != mBusRouteUserRec.busRouteUserRecDetail&& mBusRouteUserRec.busRouteUserRecDetail.size()>0)
								{
									JSONObject object = new JSONObject();
									POIItem startPoiItem = new POIItem();
									POIItem destPoiItem = new POIItem();
									
									try
									{
										object = new JSONObject(favoritFinal.mStartPOIItem);
										startPoiItem = startPoiItem.setJSONObjectToObject(object);
										object = new JSONObject(favoritFinal.mDestPOIItem);
										destPoiItem = destPoiItem.setJSONObjectToObject(object);
									}
									catch(JSONException e)
							    	{
							    		Log.d("packageJson",e.getMessage());
							    	}
									if(isDetail)
									{
										BusRouteUserRecDetail busRouteUserRecDetail = mBusRouteUserRec.busRouteUserRecDetail.get(0);
										
										Intent intent= new Intent();
										intent.setClass( mContext, RouteBusResultDetailUserRecActivity.class );
										intent.putExtra( Constants.EXTRA_BUSROUTEUSERREC, busRouteUserRecDetail );
										intent.putExtra( Constants.EXTRA_STARTPOI, startPoiItem );
										intent.putExtra( Constants.EXTRA_DESTPOI, destPoiItem );
										startActivity(intent);
									}
									else
									{
										
									}
									cityChange(favorit);
								}
								
								
							}
						},new IErrorListener() {
							
							@Override
							public void onError(ErrorResponse arg0, BaseHttpRequest arg1,
									ControlRunnable arg2) {
								if(null != arg0)
								{
									Message msg = new Message();
									msg.obj = arg0.getErrorDesc();
									msg.what = Constants.FUNC_ALERT;
									mHandler.sendMessage(msg);
								}
								
							}
						});
			}
		}
	}

	@Override
	protected void onStop() {
		ClientSession.getInstance().setDefErrorReceiver(mStateAlert);
 		ClientSession.getInstance().setDefStateReceiver(mStateAlert);
		super.onStop();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			mCommonApplication.mIsBack = true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		setBackgroundResource(mCommonApplication.mFavoritetype);
	}

	/**
	  * 根据mMapDisplayMode修改searchMode
	  * @param searchMode
	  */
	private void getMapDisplayMode(int mMapDisplayMode) 
	{
		switch (mMapDisplayMode) 
		{
			case Constants.MAPMODE_VIEWPOI:
				mSearchMode = Constants.SEARCH_POI;
				break;
			case Constants.MAPMODE_VIEWSTATION:
				mSearchMode = Constants.SEARCH_BUSSTATION;
				break;
			case Constants.MAPMODE_VIEWBUSLINE:
				mSearchMode = Constants.SEARCH_BUSLINE;
				break;
			default:
				break;
		}

	}
	
	public POIItem getPoiItemByFavorite(FavoritesData favorit)
	{
		POIItem poiItem = new POIItem();
		try 
		{
			JSONObject poiJsonObject = new JSONObject(favorit.mBusroute);

			poiItem.id = poiJsonObject.getString( "id" );
			poiItem.addr = poiJsonObject.getString( "addr" );
			poiItem.name = poiJsonObject.getString( "name" );
			double lon = poiJsonObject.getDouble( "lon" );
			double lat = poiJsonObject.getDouble( "lat" );
			poiItem.gPoint = new GPoint(lon, lat);
			if (poiJsonObject.has("tel")) 
			{
				poiItem.tel = poiJsonObject.getString("tel");
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	
		return poiItem;
	}
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch ( msg.what )
			{
				case Constants.FUNC_ALERT:
				{
					mStateAlert.showAlert((String) msg.obj);
				}
				case Constants.TOAST:
				{
					showToast(msg.obj+"");
				}
				case Constants.ERROR:
				{
					isOnClick = true;
					break;
				}
			}
		}
	};
}
