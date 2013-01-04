package com.busx.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import cm.framework.net.ClientSession;
import cm.framework.net.ControlRunnable;
import cm.framework.net.IErrorListener;
import cm.framework.net.IResponseListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;
import cm.framework.protocol.ErrorResponse;

import com.busx.R;
import com.busx.activity.base.BaseActivity;
import com.busx.entities.BusRoute;
import com.busx.entities.BusRouteExchgComparator;
import com.busx.entities.BusRouteReq;
import com.busx.entities.BusRouteTimeComparator;
import com.busx.entities.BusRouteTollComparator;
import com.busx.entities.BusRouteUserInfo;
import com.busx.entities.BusRouteUserRec;
import com.busx.entities.BusRouteUserRecDetailList;
import com.busx.entities.BusRouteWalkComparator;
import com.busx.entities.BusXRes;
import com.busx.entities.POIItem;
import com.busx.entities.PedNaviGuide;
import com.busx.entities.RouteGuide;
import com.busx.entities.RoutePathInfo;
import com.busx.entities.RoutePedPathInfo;
import com.busx.protocol.path.GetRouteBusResultUserRecRequest;
import com.busx.protocol.path.GetRouteBusResultUserRecResponse;
import com.busx.utils.ActivityMgr;
import com.busx.utils.Constants;

public class RouteBusResultActivity extends BaseActivity
{
	private TextView mRouteSumInfo;
	private TextView mRouteSubInfo;
	private ListView mListView;
	private Button mRouteModeTab1;
	private Button mRouteModeTab2;
	private Button mRouteModeTab3;
	private Button mRouteModeTab4;
	private Button mRouteModeTab5;
	private RouteBusResultAdapter mAdapter;
	private RouteBusResultUserRecommendAdapter mRecommendAdapter;

	private POIItem mStartPoiItem;
	private POIItem mDestPoiItem;
	private BusXRes mBusXRes;
	private List<BusRouteUserRecDetailList> mBusRouteUserRecDetailList;
	
	//是否是用户推荐路线，默认不是
	private boolean mIsUserRec = false;
	
	//获得起点和终点的经纬度
	private String mStartlatlon;
	private String mDestlatlon;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_routeresult_bus);

		// get data 
		mBusXRes = (BusXRes) getDataFromIntent( Constants.EXTRA_BUSXRES );
		mStartPoiItem = (POIItem) getDataFromIntent( Constants.EXTRA_STARTPOI );
		mDestPoiItem = (POIItem) getDataFromIntent( Constants.EXTRA_DESTPOI );
		mBusXRes.orderMode = Constants.ORDER_MODE_DEFAULT;
		mCommonApplication.mBusXRes = mBusXRes;
		mCommonApplication.mStartPoiItem = mStartPoiItem;
		mCommonApplication.mDestPoiItem = mDestPoiItem;
		mStartlatlon = mStartPoiItem.gPoint.lat + "," + mStartPoiItem.gPoint.lon;
		mDestlatlon = mDestPoiItem.gPoint.lat + "," + mDestPoiItem.gPoint.lon;
		
		mCommonApplication.mMapDisplayMode = Constants.MAPMODE_BUSEXCHG;
		mCommonApplication.mStartPoiItem = mStartPoiItem;
		mCommonApplication.mDestPoiItem = mDestPoiItem;

		// generate guide info
		buildGuideInfo();

		// init ctrl
		mRouteSumInfo = (TextView)findViewById( R.id.RouteSumInfo );
		mRouteSubInfo = (TextView)findViewById( R.id.RouteSubInfo );

		mRouteSumInfo.setText( mStartPoiItem.name + " → " + mDestPoiItem.name );
		mRouteSubInfo.setText( "" );

		mListView = (ListView)findViewById( R.id.BusRouteListView );
		mAdapter = new RouteBusResultAdapter( this,	mBusXRes );
		mListView.setAdapter(mAdapter);
		
		mBusXRes.orderMode = Constants.ORDER_MODE_EXCHANGE;
		RouteModeSort();
		mBusXRes.orderMode = Constants.ORDER_MODE_DEFAULT ;
		RouteModeSort();
		
		mListView.setOnItemClickListener( new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				if(mIsUserRec)
				{
					BusRouteUserRecDetailList busRouteUserRecDetailList = mBusRouteUserRecDetailList.get(position);
					
					ActivityMgr.getActivityManager().pushActivity(RouteBusResultActivity.this);
					Intent intent= new Intent();
					intent.setClass( RouteBusResultActivity.this, RouteBusResultDetailUserRecActivity.class );
					intent.putExtra( Constants.EXTRA_BUSROUTEUSERREC, busRouteUserRecDetailList );
					intent.putExtra( Constants.EXTRA_STARTPOI, mStartPoiItem );
					intent.putExtra( Constants.EXTRA_DESTPOI, mDestPoiItem );
					startActivity(intent);
				}
				else
				{
					BusRoute busRoute = mBusXRes.busRouteList.get( position );
					ActivityMgr.getActivityManager().pushActivity(RouteBusResultActivity.this);
					Intent intent= new Intent();
					intent.setClass( RouteBusResultActivity.this, RouteBusResultDetailActivity.class );
					intent.putExtra( Constants.EXTRA_BUSROUTE, busRoute );
					intent.putExtra( Constants.EXTRA_STARTPOI, mStartPoiItem );
					intent.putExtra( Constants.EXTRA_DESTPOI, mDestPoiItem );

					TextView itemTitleTextView=(TextView)view.findViewById(R.id.ItemTitle);
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("tpl_content", itemTitleTextView.getText().toString());
					mClientAgent.getEventClientinfo("transfer_program_list", paramMap);
					
					startActivity(intent);
				}
				
			}
		});

		mRouteModeTab1 = (Button)findViewById( R.id.btn_routemode_tab1 );
		mRouteModeTab2 = (Button)findViewById( R.id.btn_routemode_tab2 );
		mRouteModeTab3 = (Button)findViewById( R.id.btn_routemode_tab3 );
		mRouteModeTab4 = (Button)findViewById( R.id.btn_routemode_tab4 );
		mRouteModeTab5 = (Button)findViewById( R.id.btn_routemode_tab5 );

		mRouteModeTab1.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(View v) {

				mRouteModeTab1.setTextColor( getResources().getColor(R.drawable.tab_selected) );
				mRouteModeTab2.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab3.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab4.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab5.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				
				mRouteModeTab1.setBackgroundResource( R.drawable.btn_nav_tab_normal );
				mRouteModeTab2.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab3.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab4.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab5.setBackgroundResource( R.drawable.btn_nav_tab );
				
				mBusXRes.orderMode = Constants.ORDER_MODE_DEFAULT;
				RouteModeSort();
			}
		});

		mRouteModeTab2.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(View v) {

				mRouteModeTab1.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab2.setTextColor( getResources().getColor(R.drawable.tab_selected) );
				mRouteModeTab3.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab4.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab5.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				
				mRouteModeTab1.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab2.setBackgroundResource( R.drawable.btn_nav_tab_normal );
				mRouteModeTab3.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab4.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab5.setBackgroundResource( R.drawable.btn_nav_tab );
				
				mBusXRes.orderMode = Constants.ORDER_MODE_EXCHANGE;
				RouteModeSort();
			}
		});

		mRouteModeTab3.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				mRouteModeTab1.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab2.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab3.setTextColor( getResources().getColor(R.drawable.tab_selected) );
				mRouteModeTab4.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab5.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				
				mRouteModeTab1.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab2.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab3.setBackgroundResource( R.drawable.btn_nav_tab_normal );
				mRouteModeTab4.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab5.setBackgroundResource( R.drawable.btn_nav_tab );
				
				mBusXRes.orderMode = Constants.ORDER_MODE_WALK;
				RouteModeSort();
			}
		});
		
		mRouteModeTab4.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				mRouteModeTab1.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab2.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab3.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab4.setTextColor( getResources().getColor(R.drawable.tab_selected) );
				mRouteModeTab5.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );

				mRouteModeTab1.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab2.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab3.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab4.setBackgroundResource( R.drawable.btn_nav_tab_normal );
				mRouteModeTab5.setBackgroundResource( R.drawable.btn_nav_tab );
				
				mBusXRes.orderMode = Constants.ORDER_MODE_TOLL;
				RouteModeSort();
			}
		});
		
		mRouteModeTab5.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//把用户推荐路线设为是
				mIsUserRec = true;
				
				mRouteModeTab1.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab2.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab3.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab4.setTextColor( getResources().getColor(R.drawable.tab_not_selected) );
				mRouteModeTab5.setTextColor( getResources().getColor(R.drawable.tab_selected) );
				
				mRouteModeTab1.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab2.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab3.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab4.setBackgroundResource( R.drawable.btn_nav_tab );
				mRouteModeTab5.setBackgroundResource( R.drawable.btn_nav_tab_normal );
				
				mBusXRes.orderMode = Constants.ORDER_MODE_USER_RECOMMEND;
				
				mListView.setAdapter(mRecommendAdapter);
				if(null != mStartlatlon && null != mDestlatlon)
				{
					UserRec(mStartlatlon,mDestlatlon);
				}
				else
				{
					mHandler.sendEmptyMessage(2);
				}
				
			}
		});
		mHandler.sendEmptyMessage(Constants.INGATHER_EVENT);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		ActivityMgr.getActivityManager().clear(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK )
        {
			mCommonApplication.mIsBack = true;
        }
		return super.onKeyDown(keyCode, event);
	}

	public String getPedNaviDirectDesc( int dir )
	{
		String strDir = "";
		switch (dir )
		{
			case 0:
			{
				strDir = "直行";
				break;
			}
			case 1:
			{
				strDir = "右前转";
				break;
			}
			case 2:
			{
				strDir = "左前转";
				break;
			}
			case 3:
			{
				strDir = "右转";
				break;
			}
			case 4:
			{
				strDir = "左转";
				break;
			}
			case 5:
			{
				strDir = "右后转";
				break;
			}
			case 6:
			{
				strDir = "左后转";
				break;
			}
			case 7:
			{
				strDir = "调头";
				break;
			}
			default:
			{
				break;
			}
		}
		return strDir;
	}

	public void buildWalkGuide( RouteGuide routeGuide, RoutePedPathInfo routePedPathInfo )
	{
		if ( routePedPathInfo.guidecount > 0 && routePedPathInfo.naviguidelist.size() > 0 )
		{
			routeGuide.walkGuide = "【步行路线】：";
			for (int k=0,count=routePedPathInfo.naviguidelist.size(); k<count; k++)
			{
				PedNaviGuide pedNaviGuide = routePedPathInfo.naviguidelist.get(k);
				if ( pedNaviGuide == null )
				{
					continue;
				}
				String strDir = getPedNaviDirectDesc( pedNaviGuide.dir );
				String strGuide = "";
				if ( pedNaviGuide.dir == 0 )
				{
					if ( pedNaviGuide.roadname != null && pedNaviGuide.roadname.length() > 0 )
					{
						strGuide = "在" + pedNaviGuide.roadname + "直行" + String.valueOf(pedNaviGuide.dist) 
								+ "米";
					}
					else
					{
						strGuide = "直行" + String.valueOf(pedNaviGuide.dist) 
								+ "米";
					}
				}
				else
				{
					strGuide = "步行" + String.valueOf(pedNaviGuide.dist) 
							+ "米" + strDir;
					if ( pedNaviGuide.roadname != null && pedNaviGuide.roadname.length() > 0 )
					{
						strGuide += "至" + pedNaviGuide.roadname;
					}
				}
				routeGuide.pedNaviGuide = new ArrayList<PedNaviGuide>();
				routeGuide.pedNaviGuide = routePedPathInfo.naviguidelist;
				routeGuide.walkGuide += strGuide + ";";
			}
		}
	}
	
	public void buildGuideInfo()
	{
		// 生成GuideInfo
		for ( int i=0, len=mBusXRes.busRouteList.size(); i<len; i++ )
		{
			BusRoute busRoute = mBusXRes.busRouteList.get(i);
			if ( busRoute == null )
			{
				continue;
			}
			busRoute.guideList = new ArrayList<RouteGuide>();

			// 起点
			RouteGuide routeGuide = new RouteGuide();
			routeGuide.type = 0;
			routeGuide.time = 0;
			routeGuide.distance = 0;
			routeGuide.fromName = mStartPoiItem.name;
			routeGuide.desc = mStartPoiItem.name;
			routeGuide.gPoint = mStartPoiItem.gPoint;
			routeGuide.mapDesc = mStartPoiItem.name;
			busRoute.guideList.add( routeGuide );

			int j = 0;
			RoutePathInfo routePathInfo = null;
			RoutePedPathInfo routePedPathInfo = null;
//			RoutePathShp routePathShp = null;
			for (int size=busRoute.pathInfoList.size(); j<size; j++)
			{
				routePathInfo = busRoute.pathInfoList.get(j);
				routePedPathInfo = busRoute.pedPathInfoList.get(j);
				if ( routePathInfo == null || routePedPathInfo == null )
				{
					continue;
				}
				if ( j == 0 )
				{
					routeGuide = new RouteGuide();
					routeGuide.type = 4;
					routeGuide.desc = "步行" + routePedPathInfo.kyori + "米至" + routePathInfo.fromname;
					routeGuide.gPoint = mStartPoiItem.gPoint;
					routeGuide.time = routePedPathInfo.jikan;
					routeGuide.distance = routePedPathInfo.kyori;
					routeGuide.mapDesc = routeGuide.desc;
					routeGuide.fromName = mStartPoiItem.name;
					routeGuide.toName = routePathInfo.fromname;
					// 步行引导信息
					buildWalkGuide( routeGuide, routePedPathInfo );
					busRoute.guideList.add( routeGuide );
				}
				else
				{
					// 步行信息
					routeGuide = new RouteGuide();
					routeGuide.type = 4;
					routeGuide.desc = "步行" + routePedPathInfo.kyori + "米至" + routePathInfo.fromname;
					routeGuide.time = routePedPathInfo.jikan;
					routeGuide.distance = routePedPathInfo.kyori;
					routeGuide.toName = routePathInfo.fromname;
					//routeGuide.gPoint = ;
					routeGuide.mapDesc = routeGuide.desc;
					// 步行引导信息
					buildWalkGuide( routeGuide, routePedPathInfo );
					busRoute.guideList.add( routeGuide );
				}

				routeGuide = new RouteGuide();
				routeGuide.type = routePathInfo.type;
				routeGuide.time = routePathInfo.jikan;
				routeGuide.distance = routePathInfo.kyori;
				routeGuide.gPoint = routePathInfo.startGPoint;
				routeGuide.fromName = routePathInfo.fromname;
				routeGuide.toName = routePathInfo.toname;
				routeGuide.rosenName = routePathInfo.rosenname;
				routeGuide.passStops = routePathInfo.passstops;
				routeGuide.lineId = routePathInfo.rucode;
				routeGuide.fromId = routePathInfo.fucode;
				routeGuide.toId = routePathInfo.tucode;
				routeGuide.exit = routePathInfo.exit;
				String strDesc = null;
				//坐地铁
				if ( routePathInfo.type == 2 )
				{
					if ( routePathInfo.entry != null && routePathInfo.entry.length() > 0 )
					{
						strDesc = "从" + routePathInfo.entry + "进站，乘坐" + routePathInfo.rosenname + "，经过" + 
								routePathInfo.passstops + "站，到达" + routePathInfo.toname;
					}
					else
					{
						strDesc = "乘坐" + routePathInfo.rosenname + "，经过" + 
								routePathInfo.passstops + "站，到达" + routePathInfo.toname;
					}
					if ( routePathInfo.exit != null && routePathInfo.exit.length() > 0 )
					{
						strDesc += "，从" + routePathInfo.exit + "出站";
					}
				}
				//坐公交
				else
				{
					strDesc = "乘坐" + routePathInfo.rosenname + "，经过" + 
							routePathInfo.pathstops + "站，到达" + routePathInfo.toname;
				}
				routeGuide.desc = strDesc;
				routeGuide.mapDesc = routeGuide.desc;
				busRoute.guideList.add( routeGuide );
			}

			routePedPathInfo = busRoute.pedPathInfoList.get(j);
			routeGuide = new RouteGuide();
			routeGuide.type = 4;
			routeGuide.time = routePedPathInfo.jikan;
			routeGuide.distance = routePedPathInfo.kyori;
			routeGuide.toName = mDestPoiItem.name;
			routeGuide.desc = "步行" + routePedPathInfo.kyori + "米至" + mDestPoiItem.name;
			routeGuide.gPoint = mDestPoiItem.gPoint;
			routeGuide.mapDesc = routeGuide.desc;
			// 步行引导信息
			buildWalkGuide( routeGuide, routePedPathInfo );
			busRoute.guideList.add( routeGuide );
			
			//终点
			routeGuide = new RouteGuide();
			routeGuide.type = 9;
			routeGuide.time = 0;
			routeGuide.distance = 0;
			routeGuide.toName = mDestPoiItem.name;
			routeGuide.desc = mDestPoiItem.name;
			routeGuide.gPoint = mDestPoiItem.gPoint;
			routeGuide.mapDesc = mDestPoiItem.name;
			busRoute.guideList.add(routeGuide );
		}
	}

	public void RouteModeSort()
	{
		//把用户推荐路线设为否
		mIsUserRec = false;
		
		// 根据模式排序
		if ( mBusXRes.orderMode == Constants.ORDER_MODE_DEFAULT )
		{
			// 推荐（时间少）
			BusRouteTimeComparator comparator = new BusRouteTimeComparator();
			Collections.sort( mBusXRes.busRouteList, comparator );
		}
		else if ( mBusXRes.orderMode == Constants.ORDER_MODE_EXCHANGE )
		{
			// 少换乘
			BusRouteExchgComparator comparator = new BusRouteExchgComparator();
			Collections.sort( mBusXRes.busRouteList, comparator );
		}
		else if ( mBusXRes.orderMode == Constants.ORDER_MODE_WALK )
		{
			// 少步行
			BusRouteWalkComparator comparator = new BusRouteWalkComparator();
			Collections.sort( mBusXRes.busRouteList, comparator );
		}
		else if ( mBusXRes.orderMode == Constants.ORDER_MODE_TOLL )
		{
			// 最经济
			BusRouteTollComparator comparator = new BusRouteTollComparator();
			Collections.sort( mBusXRes.busRouteList, comparator );
		}
		
		mAdapter.notifyDataSetChanged();
		mListView.setAdapter(mAdapter);
	}
	
	/**
	 * 从服务器获得用户推荐路线
	 */
	public void UserRec(String startlatlon,String destlatlon)
	{
		//取消错误弹出对话框
		ClientSession.getInstance().setDefErrorReceiver(null);
		ClientSession.getInstance().asynGetResponse(
				new GetRouteBusResultUserRecRequest( mCommonApplication.mUserLoginInfo.sid, startlatlon, destlatlon ),
				new IResponseListener() {
					@Override
					public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,
							ControlRunnable arg2) {
						if(mIsUserRec)
						{
							GetRouteBusResultUserRecResponse pathListResponse = (GetRouteBusResultUserRecResponse) arg0;
							mCommonApplication.mUserLoginInfo.copySID( pathListResponse.mUserLoginInfo );
							//合并相同的记录
							BusRouteUserRec busRouteUserRec  = pathListResponse.mBusRouteUserRec;
							if(null != busRouteUserRec.busRouteUserRecDetail && busRouteUserRec.busRouteUserRecDetail.size()>0)
							{
								mBusRouteUserRecDetailList = new ArrayList<BusRouteUserRecDetailList>();
								
								
								//去重复，放入busRouteReqList
								List<BusRouteReq> busRouteReqList = new ArrayList<BusRouteReq>();
								for(int i=0;i<busRouteUserRec.busRouteUserRecDetail.size();i++)
								{
									//循环判断重复值,没有重复值则放入busRouteReqList
									if(i == 0)
									{
										busRouteReqList.add(busRouteUserRec.busRouteUserRecDetail.get(i).busRouteReq);
									}
									else if(null != busRouteReqList && busRouteReqList.size()>0)
									{
										boolean isExist = false;
										for(int j=0;j<busRouteReqList.size();j++)
										{
											BusRouteReq brr = busRouteReqList.get(j);
											if(brr.equals(busRouteUserRec.busRouteUserRecDetail.get(i).busRouteReq))
											{
												isExist = true;
											}
										}
										if(!isExist)
										{
											busRouteReqList.add(busRouteUserRec.busRouteUserRecDetail.get(i).busRouteReq);
										}
									}
								}
								
								if(null != busRouteReqList && busRouteReqList.size()>0)
								{
									for(int i=0;i<busRouteReqList.size();i++)
									{
										List<BusRouteUserInfo> BusRouteUserInfo = new ArrayList<BusRouteUserInfo>();
										for(int j=0;j<busRouteUserRec.busRouteUserRecDetail.size();j++)
										{
											if(busRouteReqList.get(i).equals(busRouteUserRec.busRouteUserRecDetail.get(j).busRouteReq))
											{
												BusRouteUserInfo.add(busRouteUserRec.busRouteUserRecDetail.get(j).mBusRouteUserInfo);
											}
										}
										BusRouteUserRecDetailList busRouteUserRecDetailList = new BusRouteUserRecDetailList();
										busRouteUserRecDetailList.busRouteReq = busRouteReqList.get(i);
										busRouteUserRecDetailList.mBusRouteUserInfo = BusRouteUserInfo;
										int app = 0;
										int opp = 0;
										if(null != BusRouteUserInfo && BusRouteUserInfo.size()>0)
										{
											for(int num = 0; num<BusRouteUserInfo.size();num++)
											{
												app += BusRouteUserInfo.get(num).approve;
												opp += BusRouteUserInfo.get(num).opposition;
											}
											app += BusRouteUserInfo.size();
										}
										busRouteUserRecDetailList.app = app;
										busRouteUserRecDetailList.opp = opp;
										mBusRouteUserRecDetailList.add(busRouteUserRecDetailList);
									}
								}
								
								
							}
							
							//按照赞同和不赞同数排序
							if(null != mBusRouteUserRecDetailList && mBusRouteUserRecDetailList.size()>0)
							{
								for(int i=0;i<mBusRouteUserRecDetailList.size();i++)
								{
									if(mBusRouteUserRecDetailList.size()-1 != i)
									{
										if(mBusRouteUserRecDetailList.get(i).app < mBusRouteUserRecDetailList.get(i+1).app ||
											(mBusRouteUserRecDetailList.get(i).app == mBusRouteUserRecDetailList.get(i+1).app &&
											mBusRouteUserRecDetailList.get(i).opp>mBusRouteUserRecDetailList.get(i+1).opp))
										{
											BusRouteUserRecDetailList busRouteUserRecDetailList = mBusRouteUserRecDetailList.get(i);
											mBusRouteUserRecDetailList.set(i, mBusRouteUserRecDetailList.get(i+1));
											mBusRouteUserRecDetailList.set(i+1, busRouteUserRecDetailList);
											i=-1;
										}
										
									}
								}
							}
							mRecommendAdapter = new RouteBusResultUserRecommendAdapter(mContext,mBusRouteUserRecDetailList);
							
							if(null == mBusRouteUserRecDetailList || mBusRouteUserRecDetailList.size()<1)
							{
								mHandler.sendEmptyMessage(2);
							}
							else
							{
								mHandler.sendEmptyMessage(1);
							}
						}
						
					}
				},new IErrorListener() {
					
					@Override
					public void onError(ErrorResponse arg0, BaseHttpRequest arg1,
							ControlRunnable arg2) {
						if(null != arg0 && null != arg0.getErrorDesc() && mIsUserRec)
						{
							Message msg = new Message();
							msg.what = 3;
							msg.obj = arg0.getErrorDesc();
							mHandler.sendMessage(msg);
						}
					}
				});
		//恢复错误弹出对话框
		ClientSession.getInstance().setDefErrorReceiver(mStateAlert);
	}
	
	//
	Handler mHandler=new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch ( msg.what )
			{
				case Constants.INGATHER_EVENT:
				{
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("home_begin_content", mStartPoiItem.name); 
					paramMap.put("home_end_content", mDestPoiItem.name);
					paramMap.put("home_arr_time_content", (String)getDataFromIntent( Constants.EXTRA_TIMETYPE ));
					paramMap.put("home_use_time_content", (String)getDataFromIntent( Constants.EXTRA_TIME ));
					mClientAgent.getEventClientinfo("transfer_home", paramMap);
					break;
				}
				case 1:
					mListView.setAdapter(mRecommendAdapter);
					break;
				case 2:
					showToast(R.string.err_comment);
					break;
				case 3:
					String str = (String) msg.obj;
					showToast(str);
					break;
				default:
				{
					break;
				}
			}
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		if(mBusXRes.orderMode == Constants.ORDER_MODE_USER_RECOMMEND)
		{
			if(null != mStartlatlon && null != mDestlatlon)
			{
				UserRec(mStartlatlon,mDestlatlon);
			}
			else
			{
				mHandler.sendEmptyMessage(2);
			}
		}
	}
	
	
}

