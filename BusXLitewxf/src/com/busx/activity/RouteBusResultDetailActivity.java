package com.busx.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cm.framework.net.ClientSession;
import cm.framework.net.ControlRunnable;
import cm.framework.net.IResponseListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

import com.busx.R;
import com.busx.activity.base.BaseActivity;
import com.busx.common.WaitDialog;
import com.busx.data.FavoritesData;
import com.busx.database.SaveManager;
import com.busx.entities.BusRoute;
import com.busx.entities.BusRouteReq;
import com.busx.entities.BusRouteReqDetail;
import com.busx.entities.POIItem;
import com.busx.entities.RoutePathInfo;
import com.busx.protocol.path.GetPathGuideListRequest;
import com.busx.protocol.path.GetPathGuideListResponse;
import com.busx.protocol.path.GetRouteBusResultRecommendRequest;
import com.busx.protocol.path.GetRouteBusResultRecommendResponse;
import com.busx.utils.ActivityMgr;
import com.busx.utils.Constants;
import com.busx.utils.NetUtil;
import com.busx.utils.Utils;

public class RouteBusResultDetailActivity extends BaseActivity
{
	private TextView mRouteSumInfo;
	private TextView mRouteSubInfo;
	private ListView mListView;
	private ImageButton mBtnRouteFavor;
	private ImageButton mBtnViewMap;
	private ImageButton mCommendButton;
	
	private POIItem mStartPoiItem;
	private POIItem mDestPoiItem;
	private BusRoute mBusRoute;
	private Builder mBuilder;
	private Dialog mBuilderDialog;
	private EditText mEditText;
	private TextView mTextView;
	private String mTitleName;
	
	//起始站点id
	private String mStartId;
	private String mEndId;
	
	private boolean mFavoriteFlag = false;
	
	private StringBuffer mStringBuf = null;
	
	//推荐理由字数
	private int mTextLeng = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_routeresult_bus_detail);
		
		// get data
		mBusRoute = (BusRoute) getDataFromIntent( Constants.EXTRA_BUSROUTE );
		mStartPoiItem = (POIItem) getDataFromIntent( Constants.EXTRA_STARTPOI );
		mDestPoiItem = (POIItem) getDataFromIntent( Constants.EXTRA_DESTPOI );

		// init ctrl
		mRouteSumInfo = (TextView)findViewById( R.id.TextViewSumInfo );
		mRouteSubInfo = (TextView)findViewById( R.id.TextViewSubInfo );

		mTitleName = mStartPoiItem.name + " → " + mDestPoiItem.name;
		mRouteSumInfo.setText( mTitleName );
		
		mBuilder = new AlertDialog.Builder(this);
		
		String strDist;
		if ( mBusRoute.distance > 1000 )
		{
			double dist = mBusRoute.distance / 1000.0;
			strDist = Utils.formatDoubleNum(dist)+"公里";
		}
		else
		{
			strDist = String.format( "%d米", mBusRoute.distance );
		}
		String strToll = String.format( "%.1f元", mBusRoute.toll/100.0 );
		String strInfo = mBusRoute.spendtime + "分钟/" + strDist + "/" + strToll;
		mRouteSubInfo.setText( strInfo );

		mListView = (ListView)findViewById( R.id.ListViewRouteBusDetail );
		RouteBusResultDetailAdapter adapter = new RouteBusResultDetailAdapter( this, 
				mBusRoute );
		mListView.setAdapter(adapter);
		mBtnRouteFavor = (ImageButton)findViewById( R.id.ImageButtonFavor );
		mBtnViewMap = (ImageButton)findViewById( R.id.ImageButtonViewMap );
		mCommendButton = (ImageButton) findViewById(R.id.ImageButtonCommend);
		
		getRidingRoute();
		
		//设置收藏夹
		mFavoriteFlag = isFavoriteFlag();
		if(mFavoriteFlag)
		{
			mBtnRouteFavor.setImageResource(R.drawable.icon_select_fav);
		}
		else
		{
			mBtnRouteFavor.setImageResource(R.drawable.icon_poi_favor);
		}
		mBtnRouteFavor.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mFavoriteFlag) 
				{
					//删除收藏
					delFavorite();
					mBtnRouteFavor.setImageResource(R.drawable.icon_poi_favor);
					mFavoriteFlag = false;
					showToast(R.string.delete_fav);
				}
				else 
				{
					//加入收藏
					
					 int favoriteCount = getFavoriteCount();
					 if (favoriteCount>100||favoriteCount==100) 
					 {
						 showToast(R.string.fav_full);
					 }
					 else 
					 {
						saveFavorite();
						mBtnRouteFavor.setImageResource(R.drawable.icon_select_fav);
						mFavoriteFlag = true;
						showToast(R.string.add_fav);
					 }
				}
			}
		});
		
		mBtnViewMap.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				// 查看地图
				mCommonApplication.mBusRoute = mBusRoute;
				if (mBusRoute.pathShpList.size()==0) 
				{
					ClientSession.getInstance().setDefStateReceiver(mStateAlert);
					ClientSession.getInstance().setDefErrorReceiver(mStateAlert);
					ClientSession.getInstance().asynGetResponse(
							new GetPathGuideListRequest( mCommonApplication.mUserLoginInfo.sid, mStartPoiItem.gPoint.lat+"," +mStartPoiItem.gPoint.lon, mDestPoiItem.gPoint.lat+"," +mDestPoiItem.gPoint.lon, mCommonApplication.mBusRoute.shapekey, mCommonApplication.mCity.admincode),
							new IResponseListener() {
								@Override
								public void onResponse(BaseHttpResponse arg0,BaseHttpRequest arg1, ControlRunnable arg2) 
								{
									GetPathGuideListResponse pathListResponse = (GetPathGuideListResponse) arg0;
									
									mCommonApplication.mBusRoute.pathShpList = pathListResponse.routePathShpList;
									mBusRoute.pathShpList = pathListResponse.routePathShpList;
									ActivityMgr.getActivityManager().popAllActivity();
									finish();
								}
					});
				}
				else 
				{
					ActivityMgr.getActivityManager().popAllActivity();
					finish();
				}
			}
		});
		
		mCommendButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//弹出建议填写窗口
				mBuilder.setTitle(R.string.commend_reason);
				LinearLayout ll = (LinearLayout) getLayoutInflater().
						inflate(R.layout.activity_routeresult_bus_detail_recommend, null);
				mEditText = (EditText) ll.findViewById(R.id.EditTextRecommend);
				//监听提示
				mEditText.addTextChangedListener(
						new TextWatcher() {
					
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						
					}
					
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						
					}
					
					@Override
					public void afterTextChanged(Editable s) {
						
						mTextLeng = mEditText.length();
						mTextView.setText(mTextLeng+"/140   ");
						if(mTextLeng>140)
						{
							mTextView.setTextColor(Color.RED);
						}
						else
						{
							mTextView.setTextColor(Color.GRAY);
						}
					}
				});
				mTextView = (TextView) ll.findViewById(R.id.TextViewNum);
				mBuilder.setView(ll);
				mBuilder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
					}});
				mBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mBuilderDialog.dismiss();
					}
				});
				mBuilder.create();
				mBuilderDialog = mBuilder.show();
				//重写，否则会关闭提示框
				((AlertDialog) mBuilderDialog).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(
			            new View.OnClickListener() {
			                @Override
			                public void onClick(View v) {
			                	v.setClickable(false);
			                	mHandler.sendEmptyMessage(Constants.FUNC_WAITDIALOG);
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
										submitRecommend(mEditText.getText().toString());
									}
								}).start();
			                	
			                }
			            });
			
			}
		});
	}
	
	/**
	 * 获取乘车的线路
	 */
	private void getRidingRoute()
	{
		mStringBuf = new StringBuffer();
		if(null != mBusRoute && null != mBusRoute.pathInfoList && mBusRoute.pathInfoList.size()>0)
		{
			for (int i=0,len=mBusRoute.pathInfoList.size(); i<len; i++)
			{
				RoutePathInfo routePathInfo = mBusRoute.pathInfoList.get(i);
				if ( routePathInfo == null )
				{
					continue;
				}
				if ( i != 0 )
				{
					mStringBuf.append( "→" );
				}
				mStringBuf.append( routePathInfo.rosenname );
			}
		}
	}
	
	/**
	 * 查看是否存在与收藏夹中
	 * @return
	 */
	private boolean isFavoriteFlag()
	{
		FavoritesData favorite = getFavoritesData();
		return SaveManager.isExistData(Constants.TABLE_FAVORITES,favorite);
	}
	
	/**
	 * 收藏时所需要的FavoritesData对象
	 * @param entityName
	 * @return
	 */
	private FavoritesData getFavoritesData()
	{
		FavoritesData favorite = new FavoritesData();
		favorite.mTypeId = String.valueOf(Constants.MAPMODE_BUSEXCHG);
		favorite.mCityCode = mCommonApplication.mCity.admincode;
		favorite.mEntityId = "0";//暂时固定为零
		favorite.mEntityName = mTitleName;
		favorite.mEntityRidingRoute = mStringBuf == null ? "" : mStringBuf.toString();
		if (mBusRoute.guideList.size()>0) 
		{
			favorite.mBusroute = mBusRoute.packageJson().toString();
		}
		favorite.mStartPOIItem = mStartPoiItem.packageJson().toString();
		favorite.mDestPOIItem = mDestPoiItem.packageJson().toString();
		favorite.isRouteSearch = true;
		
		return favorite;
	}
	
	/**
	  * 存入收藏
	  * @param typeId
	  * @param entityId
	  * @param entityName
	  */
	private void saveFavorite()
	{
		final FavoritesData favorite = getFavoritesData();
			ClientSession.getInstance().asynGetResponse(
					new GetPathGuideListRequest( mCommonApplication.mUserLoginInfo.sid, mStartPoiItem.gPoint.lat+"," +mStartPoiItem.gPoint.lon, mDestPoiItem.gPoint.lat+"," +mDestPoiItem.gPoint.lon, mBusRoute.shapekey, mCommonApplication.mCity.admincode),
					new IResponseListener() {
						@Override
						public void onResponse(BaseHttpResponse arg0,BaseHttpRequest arg1, ControlRunnable arg2) 
						{
							GetPathGuideListResponse pathListResponse = (GetPathGuideListResponse) arg0;
							mBusRoute.pathShpList = pathListResponse.routePathShpList;
							favorite.mBusroute = mBusRoute.packageJson().toString();
							SaveManager.saveData(Constants.TABLE_FAVORITES, favorite, 0);
						}
			});
	}
	
	/**
	  * 删除收藏
	  * @param typeId
	  * @param entityId
	  */
	 private void delFavorite()
	 {
		 FavoritesData favorite = getFavoritesData();
		 SaveManager.deleteData(Constants.TABLE_FAVORITES, favorite);
	 }
	
	/**
	 * 根据接口整理需要上传到服务器的数据
	 * @param content
	 * @return
	 */
	private String getbusRouteReqDate(String content)
	{
		//按照接口整理需要上传的数据
		BusRouteReq busRouteReq = new BusRouteReq();
		busRouteReq.time = mBusRoute.spendtime;
		busRouteReq.cost = mBusRoute.toll;
		busRouteReq.exnum = mBusRoute.rosencount;
		List<BusRouteReqDetail> exdetail = new ArrayList<BusRouteReqDetail>();
		
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		try 
		{
			if (null != mBusRoute.pathInfoList && mBusRoute.pathInfoList.size() > 0) 
			{
				for (int i = 0; i < mBusRoute.pathInfoList.size(); i++) 
				{
					if(i==0)
					{
						mStartId = mBusRoute.pathInfoList.get(i).fucode;
					}
					mEndId = mBusRoute.pathInfoList.get(i).tucode;
					JSONObject jo1 = new JSONObject();
					jo1.put("linename", mBusRoute.pathInfoList.get(i).rosenname);
					jo1.put("startstop", mBusRoute.pathInfoList.get(i).fromname);
					jo1.put("endstop", mBusRoute.pathInfoList.get(i).toname);
					jo1.put("type", mBusRoute.pathInfoList.get(i).type);
					if(mBusRoute.pathInfoList.get(i).type==2)
					{
						jo1.put("num", mBusRoute.pathInfoList.get(i).passstops);
					}
					else
					{
						jo1.put("num", mBusRoute.pathInfoList.get(i).pathstops);
					}
					
					ja.put(jo1);
				}
			}
			
			busRouteReq.exdetail = exdetail;
			jo.put("time", busRouteReq.time);
			jo.put("cost", busRouteReq.cost);
			jo.put("reason", content);
			jo.put("exnum", busRouteReq.exnum);
			jo.put("exdetail", ja);
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return jo.toString();
	}
	
	/**
	 * 向服务器提交用户推荐
	 * @return
	 */
	private void submitRecommend(String content)
	{
		if(mTextLeng>140)
		{
			mHandler.sendEmptyMessage(2);
			return;
		}
		if (null != content) 
		{
			content = content.replace("\n", "/n");
		}
		if(!NetUtil.isConnectingToInternet(mContext))
		 {
      		 Message msg = new Message();
      		 msg.what = Constants.ERROR;
      		 msg.obj = "网络错误,建议您检查网络连接后再试";
      		 mHandler.sendMessage(msg);
      		 return;
		 }
		String busRouteReqDate = getbusRouteReqDate(content);
		String startlatlon = mStartPoiItem.gPoint.lat+","+mStartPoiItem.gPoint.lon;
		String endlatlon = mDestPoiItem.gPoint.lat+","+mDestPoiItem.gPoint.lon;
		ClientSession.getInstance().asynGetResponse(new GetRouteBusResultRecommendRequest(
				mCommonApplication.mUserLoginInfo.sid,mCommonApplication.mUserLoginInfo.uid,
				mStartId,mEndId,busRouteReqDate,startlatlon,endlatlon), new IResponseListener() {
					
					@Override
					public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,
							ControlRunnable arg2) 
					{
						mBuilderDialog.dismiss();
						GetRouteBusResultRecommendResponse grg = (GetRouteBusResultRecommendResponse) arg0;
						int isSuccess = grg.status;
						if(isSuccess==0)
						{
							mHandler.sendEmptyMessage(0);
						}
						else
						{
							mHandler.sendEmptyMessage(1);
						}
					}
				});
		
	}
	
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what)
			{
				case 0:
				{
					mHandler.sendEmptyMessage(Constants.FUNC_HIDDENWAITDIALOG);
					showToast(R.string.commend_success);
					break;
				}
				case 1:
				{
					mHandler.sendEmptyMessage(Constants.FUNC_HIDDENWAITDIALOG);
					showToast(R.string.commend_failure);
					break;
				}
				case 2:
				{
					mHandler.sendEmptyMessage(Constants.FUNC_HIDDENWAITDIALOG);
					showToast(R.string.commend_long);
					break;
				}
				case Constants.ERROR:
				{
					mHandler.sendEmptyMessage(Constants.FUNC_HIDDENWAITDIALOG);
					String errStr = (String) msg.obj;
					mStateAlert.showAlert(errStr);
					break;
				}
				case Constants.FUNC_WAITDIALOG:
				{
					mStateAlert.dlgWait = new WaitDialog(mContext);
					mStateAlert.showWaitDialog("请稍等..");
					break;
				}
				case Constants.FUNC_HIDDENWAITDIALOG:
				{
					if(null != mStateAlert)
					{
						mStateAlert.hidenWaitDialog();
					}
					break;
				}
				default:
				{
					break;
				}
			}
		}
	};

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
        }
        return super.onKeyDown(keyCode, event);
    }
	
	private int getFavoriteCount()
	{
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select * from "+Constants.TABLE_FAVORITES);
		sql.append(" where 1=1 and " + FavoritesData.TYPEID + " in( ");
		sql.append(Constants.MAPMODE_VIEWBUSLINE+","+Constants.MAPMODE_BUSEXCHG+","+Constants.MAPMODE_FAVORITE_BUSEXCHG);
		sql.append(")");
		int favoritesDataCount = SaveManager.getConditionCount(sql.toString());
		return favoritesDataCount;
	}

}
