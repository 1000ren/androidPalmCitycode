package com.busx.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
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

import com.amap.mapapi.core.AMapException;
import com.amap.mapapi.core.PoiItem;
import com.amap.mapapi.poisearch.PoiPagedResult;
import com.amap.mapapi.poisearch.PoiSearch;
import com.amap.mapapi.poisearch.PoiSearch.SearchBound;
import com.amap.mapapi.poisearch.PoiTypeDef;
import com.busx.R;
import com.busx.activity.base.BaseActivity;
import com.busx.entities.GPoint;
import com.busx.entities.NearbyKind;
import com.busx.entities.NearbyKindItem;
import com.busx.entities.POIItem;
import com.busx.entities.POIRes;
import com.busx.protocol.ProtocolDef;
import com.busx.utils.ActivityMgr;
import com.busx.utils.Constants;
import com.busx.utils.NetUtil;

public class NearbyKindActivity extends BaseActivity  
{
	private List<NearbyKind> mNearbyKinds = null;
	private NearbyKind mNearbyKind = null;
	private String[] distance;
	private ListView mListView;
	private String keyword;
	private Button mSpinner_distance;
	private String mdistance = Constants.NEARBYSEARCH_DEFAULT_SCALE;
	private int mchoice = 0;
	private NearbyKindAdapter mNearbyKindAdapter = null;
	private PoiPagedResult mResult;
	private NearbyKindItem mNearbyKindItem;
	private Context mContext;
	//第一级菜单是否可点击
	private boolean isOnClick = true;
	//选距离菜单是否可点击
	private boolean isDistanceOnClick = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_nearby_kind);
		mContext = this;
    	mClientAgent.getEventClientinfo("around_search");
    	String nearByTitle = (String) (getDataFromIntent( Constants.EXTRA_SEARCHNEARBY ) == null ? "":getDataFromIntent( Constants.EXTRA_SEARCHNEARBY ));
		
		distance=getResources().getStringArray(R.array.distance);
		mListView=(ListView)findViewById(R.id.ListView_Nearby);
		TextView titleTextView = (TextView) findViewById(R.id.title_text);
		if(nearByTitle.equals("myself"))
		{
			titleTextView.setText( this.getResources().getString( R.string.nearby_me ) );
		}
		else
		{
			titleTextView.setText( this.getResources().getString( R.string.nearby_arround ) );
		}
		
		mNearbyKinds=new ArrayList<NearbyKind>();
		mNearbyKinds.addAll(mCommonApplication.mNearbyKinds);
		mNearbyKindAdapter = new NearbyKindAdapter(this, mNearbyKinds);
		mListView.setAdapter(mNearbyKindAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
					long arg3) 
			{
				if(isOnClick)
				{
					isOnClick = false;
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
							firstItem(arg2);
						}
					}).start();
				}
			}
		});
		
		//搜索范围
		int k=0;
		for (String str:distance) 
		{
			if (str.equals(mdistance)) 
			{
				mchoice=k;
				k++;
			}
		}
		
		mSpinner_distance=(Button)findViewById(R.id.spinner_distance);
		mSpinner_distance.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(!isDistanceOnClick)
				{
					return;
				}
				isDistanceOnClick = false;
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
						mHandler.sendEmptyMessage(1);
					}
				}).start();
				
			}
		});
		removeList();
	}
	
	/**
	 * 点击第一级分类触发的事件
	 */
	private void firstItem(int arg2)
	{
		mNearbyKind=mNearbyKinds.get(arg2);
		mHandler.sendEmptyMessage(3);
		mHandler.sendEmptyMessage(2);
	}
	
	/**
	 * 创建选择菜单
	 */
	private void creatDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(NearbyKindActivity.this);
		builder.setTitle(mNearbyKind.name);
		
		builder.setItems(mNearbyKind.kinditems, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mNearbyKindItem=mNearbyKind.viewKindItems.get(which);
				keyword=mNearbyKindItem.name;
				if(!NetUtil.isConnectingToInternet(mContext))
				{
					mHandler.sendEmptyMessage(5);
					return ;
				}
				mHandler.sendEmptyMessage(Constants.FUNC_WAITDIALOG);
				mHandler.sendEmptyMessage(4);
			}
		});
		builder.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				 if(keyCode==KeyEvent.KEYCODE_BACK)
				 {
					 isOnClick = true;
				 }
				 return false;
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	@Override
	protected void onResume()
	{
		ActivityMgr.getActivityManager().clear(this);
		super.onResume();
		isOnClick = true;
		isDistanceOnClick = true;
		mHandler.sendEmptyMessage(Constants.FUNC_HIDDENWAITDIALOG);
	}
	@Override
	protected void onStart() 
	{
		super.onStart();
	}
	private void startIntent(NearbyKindItem nearbyKindItem)
	{
		if(NetUtil.isConnectingToInternet(mContext))
		{
			try 
			{
				PoiSearch poiSearch = new PoiSearch(NearbyKindActivity.this,new PoiSearch.Query(nearbyKindItem.name, PoiTypeDef.All, mCommonApplication.mCity.admincode)); // 设置搜索字符串，城市区号
				poiSearch.setBound(new SearchBound(mCommonApplication.mGeoPoint, Integer.parseInt(mdistance.substring(0, mdistance.length()-1))));
				poiSearch.setPageSize(ProtocolDef.PAGE_SIZE);
				mResult= null;
				mResult = poiSearch.searchPOI();
				if (null!=mResult&&mResult.getPageCount()>0) 
				{
					int curCount = (mResult.getPageCount()-1)*ProtocolDef.PAGE_SIZE+mResult.getPage(mResult.getPageCount()).size();
					nearbyKindItem.num = curCount;
					nearbyKindItem.mPoiPagedResult = mResult;
				}
			} 
			catch (AMapException e) 
			{
				e.printStackTrace();
			}
			
			if(nearbyKindItem.num<1)
			{
				showToast("没有搜到'"+nearbyKindItem.name+"',请尝试扩大搜索范围");
				mHandler.sendEmptyMessage(2);
				mHandler.sendEmptyMessage(Constants.FUNC_HIDDENWAITDIALOG);
				return ;
			}
	
			ActivityMgr.getActivityManager().pushActivity( NearbyKindActivity.this );
			Intent intent= new Intent();
			intent.setClass( mContext, SearchResultListActivity.class );
			intent.putExtra( Constants.EXTRA_SEARCHMODE, Constants.SEARCH_NEARBY );
			intent.putExtra( Constants.EXTRA_KEYWORD, keyword);
			intent.putExtra( Constants.EXTRA_TOTAL, nearbyKindItem.num );
			intent.putExtra( Constants.EXTRA_MTOTLE , nearbyKindItem.num);
			if (nearbyKindItem.mPoiPagedResult != null) 
			{
				try 
				{
					List<POIItem> poiItems =getPOIItemByPoiPagedResult(nearbyKindItem.mPoiPagedResult);
					POIRes  poiRes= new POIRes();
					poiRes.mPoiList = poiItems;
					mCommonApplication.mPoiRes = poiRes;
					mCommonApplication.mPoiPagedResult = nearbyKindItem.mPoiPagedResult;
					intent.putExtra( Constants.EXTRA_POIRES,poiRes );
					startActivity(intent);
					mCommonApplication.mIsFavorite = false;
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			
		 }
		else
		{
			mHandler.sendEmptyMessage(5);
		}
	}

	private void removeList()
	{
		for (NearbyKind nearbyKind : mNearbyKinds) 
		{
			nearbyKind.viewKindItems.clear();
			//统计每个一级分类下二级分类的数目
			for (NearbyKindItem nearbyKindItem : nearbyKind.nearbykinditems) 
			{
				nearbyKind.viewKindItems.add(nearbyKindItem);
			}
			nearbyKind.getKindiems();
		}
	}
	
	private List<POIItem> getPOIItemByPoiPagedResult(PoiPagedResult poiPagedResult) throws Exception
	 {
		 List<POIItem> list = new ArrayList<POIItem>();
		 
		try 
		{
			if (poiPagedResult != null) 
			{
				List<PoiItem> poiItems = poiPagedResult.getPage(1);
				
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
	
	private void createDistanceDialog()
	{
		new AlertDialog.Builder(NearbyKindActivity.this)  
		.setTitle("请选择")  
		.setIcon(android.R.drawable.ic_dialog_info)                  
		.setSingleChoiceItems(distance,mchoice,new DialogInterface.OnClickListener() 
		{  
		     public void onClick(DialogInterface dialog, int which) 
		     {
		    	 mdistance=distance[which];
		    	 mSpinner_distance.setText(mdistance);
		    	 mchoice=which;
		    	 isDistanceOnClick = true;
		    	 dialog.dismiss();
		     }  
		  })
		  .setOnKeyListener(new OnKeyListener() 
		  {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				 if(keyCode==KeyEvent.KEYCODE_BACK)
				 {
					 isDistanceOnClick = true;
				 }
				return false;
			}
		})
		.setNegativeButton("取消", onClickListener).show();  
	}
	
	private DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener(){

		@Override
		public void onClick(DialogInterface dialog, int which) 
		{
			isDistanceOnClick = true;
		}};
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case 1:
					createDistanceDialog();
					break;
				case 2:
					creatDialog();
					break;
				case 3:
					//统计-一级分类
					Map<String, String> paraMap=new HashMap<String, String>();
					paraMap.put("ass_content", mNearbyKind.name);
					mClientAgent.getEventClientinfo("around_search_sort",paraMap);
					break;
				case 4:
					//统计-二级分类
					Map<String, String> paraMap1=new HashMap<String, String>();
					paraMap1.put("asss_content", keyword);
					mClientAgent.getEventClientinfo("around_search_sort_second",paraMap1);
					startIntent(mNearbyKindItem);
					break;
				case 5:
					//提示网络连接错误
					mStateAlert.showAlert("网络错误,建议您检查网络连接后再试");
					mHandler.sendEmptyMessage(Constants.FUNC_HIDDENWAITDIALOG);
					break;
				case Constants.FUNC_WAITDIALOG:
					mStateAlert.showWaitDialog("请稍候..");
					break;
				case Constants.FUNC_HIDDENWAITDIALOG:
					if(null != mStateAlert)
					{
						mStateAlert.hidenWaitDialog();
					}
					break;
				default:
					break;
					
			}
			
		}
		
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			mCommonApplication.mIsBack = true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
