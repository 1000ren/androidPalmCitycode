package com.busx.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
import com.busx.entities.BusRouteReqDetail;
import com.busx.entities.BusRouteUserInfo;
import com.busx.entities.BusRouteUserRecDetailList;
import com.busx.entities.POIItem;
import com.busx.protocol.path.GetRouteBusResultUserCommentRequest;
import com.busx.protocol.path.GetRouteBusResultUserCommentResponse;
import com.busx.utils.Constants;


public class RouteBusResultDetailUserRecActivity extends BaseActivity
{
	private TextView mRouteSumInfo;
	private TextView mRouteSubInfo;
	private ListView mListView;
//	private ImageButton mBtnRouteFavor;
	private TextView mBtnViewMap;
	private TextView mCommendButton;
	private TextView mImageButtonReason;
	private LinearLayout mLinearLayoutApprove;
	private LinearLayout mLinearLayoutOpposition;
	private LinearLayout mLinearLayoutReason;
	
	private POIItem mStartPoiItem;
	private POIItem mDestPoiItem;
	private BusRouteUserRecDetailList busRouteUserRecDetailList;
	
	//赞同：1，反对：0
	private int evaluate;
	private boolean isOnClick = true;
	
//	private boolean mFavoriteFlag = false;
	private String mTitleName;
	private StringBuffer mStringBuf = null;
	
	private int approve = 0;
	private int opposition = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_routeresult_bus_detail_user_rec);
		
		// get data
		busRouteUserRecDetailList = (BusRouteUserRecDetailList) getDataFromIntent( Constants.EXTRA_BUSROUTEUSERREC );
		mStartPoiItem = (POIItem) getDataFromIntent( Constants.EXTRA_STARTPOI );
		mDestPoiItem = (POIItem) getDataFromIntent( Constants.EXTRA_DESTPOI );
		approve = busRouteUserRecDetailList.mBusRouteUserInfo.size();

		// init ctrl
		mRouteSumInfo = (TextView)findViewById( R.id.TextViewSumInfo );
		mRouteSubInfo = (TextView)findViewById( R.id.TextViewSubInfo );
		
		mLinearLayoutApprove = (LinearLayout) findViewById( R.id.LinearLayoutApprove );
		mLinearLayoutOpposition = (LinearLayout) findViewById( R.id.LinearLayoutOpposition);
		mLinearLayoutReason = (LinearLayout) findViewById( R.id.LinearLayoutReason);
		
		
		mTitleName = mStartPoiItem.name + " → " + mDestPoiItem.name;
		mRouteSumInfo.setText( mTitleName );
		
		String strToll = String.format( "%.1f元", busRouteUserRecDetailList.busRouteReq.cost/100.0 );
		String strDesc = "花费" + strToll +"/换乘"+busRouteUserRecDetailList.busRouteReq.exnum+"次";

		mRouteSubInfo.setText( strDesc );

		mListView = (ListView)findViewById( R.id.ListViewRouteBusDetail );
		RouteBusResultDetailUserRecAdapter adapter = new RouteBusResultDetailUserRecAdapter( this, 
				busRouteUserRecDetailList.busRouteReq.exdetail,mStartPoiItem.name,mDestPoiItem.name );
		mListView.setAdapter(adapter);
		mBtnViewMap = (TextView)findViewById( R.id.ImageButtonViewMap );
		mCommendButton = (TextView) findViewById(R.id.ImageButtonCommend);
		mImageButtonReason = (TextView) findViewById(R.id.ImageButtonReason);
		int reasonNum = null == busRouteUserRecDetailList.mBusRouteUserInfo ? 0 : busRouteUserRecDetailList.mBusRouteUserInfo.size();
		mImageButtonReason.setText("推荐理由("+reasonNum+")");
		getRidingRoute();
		
		if(null != busRouteUserRecDetailList.mBusRouteUserInfo && busRouteUserRecDetailList.mBusRouteUserInfo.size()>0)
		{
			for(BusRouteUserInfo busRouteUserInfo :busRouteUserRecDetailList.mBusRouteUserInfo)
			{
				approve += busRouteUserInfo.approve;
				opposition += busRouteUserInfo.opposition;
			}
		}
		
		//赞和不攒
		mBtnViewMap.setText("顶("+approve+")");
		mCommendButton.setText("踩("+opposition+")");
		
		mLinearLayoutApprove.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				if(isOnClick)
				{
					// 赞
					evaluate = 1;
					
					//设置客户端界面显示
					mBtnViewMap.setText("顶("+(approve +=1)+")");
					disabled();
					
					//提交服务器
					sendComment();
					
					//存到本地记录
					saveUserCommentMap();
				}
				
			}
		});
		
		mLinearLayoutOpposition.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(isOnClick)
				{
					//不攒
					evaluate = 0;
					
					//设置客户端界面显示
					mCommendButton.setText("踩("+(opposition +=1)+")");
					disabled();
					
					//提交服务器
					sendComment();
					
					//存到本地记录
					saveUserCommentMap();
				}
			}
		});
		
		mLinearLayoutReason.setOnClickListener(new OnClickListener(){
			public void onClick(View v)
			{
				Intent intent = new Intent(RouteBusResultDetailUserRecActivity.this,RouteBusResultDetailUserRecReasonActivity.class);
				intent.putExtra(Constants.EXTRA_BUSROUTEUSERREC, busRouteUserRecDetailList);
				startActivity(intent);
			}
		});
		
		//判断赞同和不赞同按钮是否可点击
		if(null != Constants.USER_COMMENT_LINE && Constants.USER_COMMENT_LINE.size()>0)
		{
			String uid = Constants.USER_COMMENT_LINE.get(busRouteUserRecDetailList.mBusRouteUserInfo.get(0).pathcomid);
			if(null != uid && uid.equals(mCommonApplication.mUserLoginInfo.uid))
			{
				disabled();
			}
			
		}

	}
	
	
	/**
	 * 赞和不赞按钮失效
	 */
	private void disabled()
	{
		mBtnViewMap.setTextColor(getResources().getColor(R.color.help_button_view));
		mCommendButton.setTextColor(getResources().getColor(R.color.help_button_view));
		isOnClick = false;
	}
	
	/**
	  * 存入收藏
	  * @param typeId
	  * @param entityId
	  * @param entityName
	  */
//	private void saveFavorite()
//	{
//		FavoritesData favorite = getFavoritesData();
//		SaveManager.saveData(Constants.TABLE_FAVORITES, favorite, 0);
//				
//	}
	
	/**
	  * 删除收藏
	  * @param typeId
	  * @param entityId
	  */
//	 private void delFavorite()
//	 {
//		 FavoritesData favorite = getFavoritesData();
//		 SaveManager.deleteData(Constants.TABLE_FAVORITES, favorite);
//	 }
	
	/**
	 * 查看是否存在与收藏夹中
	 * @return
	 */
//	private boolean isFavoriteFlag()
//	{
//		FavoritesData favorite = getFavoritesData();
//		return SaveManager.isExistData(Constants.TABLE_FAVORITES,favorite);
//	}
	
	/**
	 * 获取乘车的线路
	 */
	private void getRidingRoute()
	{
		mStringBuf = new StringBuffer();
		if(null != busRouteUserRecDetailList && null != busRouteUserRecDetailList.busRouteReq.exdetail && busRouteUserRecDetailList.busRouteReq.exdetail.size()>0)
		{
			for (int i=0,len=busRouteUserRecDetailList.busRouteReq.exdetail.size(); i<len; i++)
			{
				BusRouteReqDetail busRouteReqDetail = busRouteUserRecDetailList.busRouteReq.exdetail.get(i);
				if ( busRouteReqDetail == null )
				{
					continue;
				}
				if ( i != 0 )
				{
					mStringBuf.append( "→" );
				}
				mStringBuf.append( busRouteReqDetail.linename );
			}
		}
	}
	
	/**
	 * 收藏时所需要的FavoritesData对象
	 * @param entityName
	 * @return
	 */
//	private FavoritesData getFavoritesData()
//	{
//		FavoritesData favorite = new FavoritesData();
//		favorite.mTypeId = String.valueOf(Constants.MAPMODE_FAVORITE_BUSEXCHG);
//		favorite.mEntityId = mbusRouteUserRecDetail.recid;
//		favorite.mEntityName = mTitleName;
//		favorite.mEntityRidingRoute = mStringBuf == null ? "用户推荐" : "用户推荐:"+mStringBuf.toString();
//		favorite.mStartPOIItem = mStartPoiItem.packageJson().toString();
//		favorite.mDestPOIItem = mDestPoiItem.packageJson().toString();
//		return favorite;
//	}
	
	private void saveUserCommentMap()
	{
		if(null == Constants.USER_COMMENT_LINE)
		{
			Constants.USER_COMMENT_LINE = new HashMap<String,String>();
		}
		Constants.USER_COMMENT_LINE.put(busRouteUserRecDetailList.mBusRouteUserInfo.get(0).pathcomid, mCommonApplication.mUserLoginInfo.uid);
		
	}
	
	/**
	 * 向服务器发送客户端的评论(赞或不赞)
	 */
	private void sendComment()
	{
		ClientSession.getInstance().setDefStateReceiver(null);
		ClientSession.getInstance().asynGetResponse(
				new GetRouteBusResultUserCommentRequest(
						mCommonApplication.mUserLoginInfo.sid,
						busRouteUserRecDetailList.mBusRouteUserInfo.get(0).pathcomid,
						evaluate
						), 
				new IResponseListener() {
			
			@Override
			public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,
					ControlRunnable arg2) 
			{
				GetRouteBusResultUserCommentResponse grcr = (GetRouteBusResultUserCommentResponse) arg0;
				handler.sendEmptyMessage(grcr.status);
			}
		});
		ClientSession.getInstance().setDefStateReceiver(mStateAlert);
	}
	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==0)
			{
				mBtnViewMap.setClickable(false);
				mCommendButton.setClickable(false);
				showToast(R.string.comment_success);
			}
			else
			{
				showToast(R.string.comment_failure);
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
}
