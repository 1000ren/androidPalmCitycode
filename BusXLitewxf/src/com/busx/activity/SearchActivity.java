package com.busx.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import cm.framework.net.ClientSession;
import cm.framework.net.ControlRunnable;
import cm.framework.net.IErrorListener;
import cm.framework.net.IResponseListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;
import cm.framework.protocol.ErrorResponse;

import com.amap.mapapi.core.PoiItem;
import com.amap.mapapi.poisearch.PoiPagedResult;
import com.amap.mapapi.poisearch.PoiSearch;
import com.amap.mapapi.poisearch.PoiTypeDef;
import com.busx.R;
import com.busx.activity.base.BaseActivity;
import com.busx.entities.BusLine;
import com.busx.entities.BusStation;
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
import com.busx.utils.NetUtil;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;

public class SearchActivity extends BaseActivity implements RecognizerDialogListener
{

	private AutoCompleteTextView mSearchTextView;
	private ImageButton mVoiceButton;
	private Button mPoiButton;
	private Button mBusStationButton;
	private Button mBusLineButton;
	private ImageButton mSearchButton;

	private int mSearchMode = Constants.SEARCH_POI;
	private String mKeyword = null;
	private RecognizerDialog iatDialog;
	private Intent intent=null;
	private ArrayAdapter<String> mAdapter = null ;
	private List<String> mData = new ArrayList<String>();
	private List<POIItem> mAutoPoiList = new ArrayList<POIItem>();
	private List<BusStation> mAutoBusStationList = new ArrayList<BusStation>();
	private List<BusLine> mAutoBusLineList = new ArrayList<BusLine>();
	private boolean chageAdapterflag = true;
	private int mTotle;
	private boolean mIsSearchSplitData = true;
	private PoiPagedResult mPoiPagedResult = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTheme( R.style.translucent );
		setContentView(R.layout.activity_search);

		iatDialog = new RecognizerDialog(this, "appid=" + getString(R.string.app_id));
		iatDialog.setListener(this);

		mSearchTextView = (AutoCompleteTextView) findViewById(R.id.autotextview_search);
		mVoiceButton = (ImageButton) findViewById(R.id.imagebtn_voice_search);
		mPoiButton = (Button) findViewById(R.id.imagebtn_search_tab_poi);
		mBusStationButton = (Button) findViewById(R.id.imagebtn_search_tab_busstation);
		mBusLineButton = (Button) findViewById(R.id.imagebtn_search_tab_busline);
		mSearchButton = (ImageButton) findViewById(R.id.imagebtn_search);
		mSearchTextView.setThreshold(1);
		mSearchTextView.addTextChangedListener(new TextWatcher()
		{
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				
				mKeyword = mSearchTextView.getText().toString().trim();
				if (mKeyword.length()>1&&mKeyword.endsWith("。")) 
				{
					mKeyword = mKeyword.substring(0,mKeyword.lastIndexOf("。"));
					mSearchTextView.setText(mKeyword);
				}
				if (mKeyword.length()>80) 
				{
					mSearchTextView.setText(mKeyword.substring(0, 79));
					showToast("字数过长，请缩减");
					return;
				}
				if(mSearchMode != Constants.SEARCH_POI)
				{
					chageAdapterflag = true;
					for (String str : mData) 
					{
						if (mKeyword.equals(str)) 
						{
							chageAdapterflag= false;
							SearchResult();
							break;
						}
					}
					if (chageAdapterflag&& null != mKeyword && mKeyword.length() > 0 )
					{
						mHandler.sendEmptyMessage(mSearchMode);
					}
				}
			}
		});
		
		mVoiceButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showIatDialog();
			}
		});

		mPoiButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setSearchMode(Constants.SEARCH_POI);
				mPoiButton.setBackgroundResource(R.drawable.mode_poi_on);
				mBusStationButton.setBackgroundResource(R.drawable.mode_busstation_off);
				mBusLineButton.setBackgroundResource(R.drawable.mode_busline_off);
				mSearchTextView.setHint(R.string.search_poi_hint);
				mSearchMode = Constants.SEARCH_POI;
				if (null != mKeyword && mKeyword.length() > 0) {
					mHandler.sendEmptyMessage(mSearchMode);
				}
				
			}
		});
		
		mBusStationButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setSearchMode(Constants.SEARCH_BUSSTATION);
				mPoiButton.setBackgroundResource(R.drawable.mode_poi_off);
				mBusStationButton.setBackgroundResource(R.drawable.mode_busstation_on);
				mBusLineButton.setBackgroundResource(R.drawable.mode_busline_off);
				mSearchTextView.setHint(R.string.search_busstation_hint);
				mSearchMode = Constants.SEARCH_BUSSTATION;
				if (null != mKeyword && mKeyword.length() > 0) {
					mHandler.sendEmptyMessage(mSearchMode);
				}
			}
		});
		
		mBusLineButton.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {
				setSearchMode(Constants.SEARCH_BUSLINE);
				mPoiButton.setBackgroundResource(R.drawable.mode_poi_off);
				mBusStationButton.setBackgroundResource(R.drawable.mode_busstation_off);
				mBusLineButton.setBackgroundResource(R.drawable.mode_busline_on);
				mSearchTextView.setHint(R.string.search_busline_hint);
				mSearchMode = Constants.SEARCH_BUSLINE;
				if (null != mKeyword && mKeyword.length() > 0) {
					mHandler.sendEmptyMessage(mSearchMode);
				}
			}
		});

		mSearchButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_DISABLE);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try 
						{
							Thread.sleep(1000);
						} 
						catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
						mHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_INIT);
					}
				}).start();
			}
		});
	}
	
	/**
	 * 搜索前的查询
	 */
	private void searchInit()
	{
		mKeyword = mSearchTextView.getText().toString().trim();
		if ( mKeyword == null || mKeyword.length() <= 0 )
		{
			mHandler.sendMessage(mHandler.obtainMessage(Constants.TOASTERROR, "输入的查询内容不能为空或空格"));
			return;
		}
		SearchResult();
	}

	public int getSearchMode() {
		return mSearchMode;
	}

	public void setSearchMode(int mSearchMode) {
		this.mSearchMode = mSearchMode;
	}

	@Override
	protected void onResume()
	{
		ActivityMgr.getActivityManager().clear(this);
		super.onResume();
		mHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_ENABLE);
	}

	@Override
	public void onEnd(SpeechError arg0) {
		// 
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	@Override
	protected void onStart() {
		mIsSearchSplitData = true;
		super.onStart();
	}
	
	public void showIatDialog()
	{
		iatDialog.setEngine("sms", null, null);

		iatDialog.setSampleRate(RATE.rate16k);

		mSearchTextView.setText(null);

		iatDialog.show();
	}
	
	@Override
	public void onResults(ArrayList<RecognizerResult> results, boolean isLast)
	{
		StringBuilder builder = new StringBuilder();
		for (RecognizerResult recognizerResult : results)
		{
			builder.append(recognizerResult.text);
		}
		mSearchTextView.append(builder);
		mSearchTextView.setSelection(mSearchTextView.length());
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

	public void SearchResult()
	{
		if(!NetUtil.isConnectingToInternet(mContext))
		 {
     		 Message msg = new Message();
     		 msg.what = Constants.TOASTERROR;
     		 msg.obj = "网络错误,建议您检查网络连接后再试";
     		 mHandler.sendMessage(msg);
     		 return;
		 }
		mStateAlert.showWaitDialog("请稍候...");
		new Thread(
				new Runnable() 
				{
					@Override
					public void run() 
					{
						startSearch();
					}
				}
				).start();
		
	}
	
	private void startSearch()
	{

		ActivityMgr.getActivityManager().pushActivity(SearchActivity.this);
		mIsSearchSplitData = false;
		mCommonApplication.mSearchMode = mSearchMode;
		mCommonApplication.mSearchKeyword = mKeyword;
		
		
		intent= new Intent();
		intent.putExtra( Constants.EXTRA_SEARCHMODE, mSearchMode );
		intent.putExtra( Constants.EXTRA_KEYWORD, mKeyword );

		if (mSearchMode==Constants.SEARCH_POI)
		{
			processThread();
				
		}
		else if (mSearchMode==Constants.SEARCH_BUSSTATION)
		{
			ClientSession.getInstance().setDefStateReceiver(null);
			ClientSession.getInstance().setDefErrorReceiver(null);
			ClientSession.getInstance().asynGetResponse(new GetBusStationListRequest( 
					mCommonApplication.mUserLoginInfo.sid, mKeyword, 1, ProtocolDef.PAGE_SIZE , mCommonApplication.mCity.admincode),
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
							mTotle = busStationListResponse.mTotal;
							intent.putExtra(Constants.EXTRA_MTOTLE, mTotle);
							intent.putExtra( Constants.EXTRA_BUSSTATIONRES, busStationListResponse.mBusStationRes );
							if (null != busStationListResponse.mBusStationRes.mBusStationList && busStationListResponse.mBusStationRes.mBusStationList.size()==1) 
							{
								mCommonApplication.mBusStationRes = busStationListResponse.mBusStationRes;
								BusStation busStation = busStationListResponse.mBusStationRes.mBusStationList.get(0);
								mCommonApplication.mBusStation = busStation;
								intent.putExtra( Constants.EXTRA_BUSSTATION, busStation);
								intent.setClass( mContext, SearchResultDetailActivity.class);
								startActivity(intent);
							}
							else 
							{
								intent.setClass( mContext, SearchResultListActivity.class );
								startActivity(intent);
							}
							mCommonApplication.mIsFavorite = false;
						}
			});
		}
		else if (mSearchMode==Constants.SEARCH_BUSLINE)
		{
			ClientSession.getInstance().setDefStateReceiver(null);
			ClientSession.getInstance().setDefErrorReceiver(null);
			ClientSession.getInstance().asynGetResponse(new GetBusLineListRequest( 
					mCommonApplication.mUserLoginInfo.sid, mKeyword, 1, ProtocolDef.PAGE_SIZE , mCommonApplication.mCity.admincode),
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
							mTotle = busLineListResponse.mTotal;
							intent.putExtra(Constants.EXTRA_MTOTLE, mTotle);
							intent.putExtra( Constants.EXTRA_BUSLINERES, busLineListResponse.mBusLineRes );
							if (null != busLineListResponse.mBusLineRes.mBusLineList && busLineListResponse.mBusLineRes.mBusLineList.size()==1) 
							{
								final BusLine  busLine= busLineListResponse.mBusLineRes.mBusLineList.get(0);
								mCommonApplication.mBusLineRes = busLineListResponse.mBusLineRes;
								mCommonApplication.mBusLine = busLine;
								if ( busLine.busStops.size() <= 0 )
								{
									ClientSession.getInstance().asynGetResponse(new GetBusStopListRequest( 
											mCommonApplication.mUserLoginInfo.sid, busLine.lineid , mCommonApplication.mCity.admincode),
											new IResponseListener() {
												@Override
												public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2)
												{
													GetBusStopListResponse busstopListResponse = (GetBusStopListResponse) arg0;
													mCommonApplication.mUserLoginInfo.copySID( busstopListResponse.mUserLoginInfo );
													busLine.copy( busstopListResponse.mBusLine );

													Intent intent= new Intent();
													intent.setClass( mContext, SearchResultDetailActivity.class);
													intent.putExtra( Constants.EXTRA_SEARCHMODE, mSearchMode );
													intent.putExtra( Constants.EXTRA_BUSLINE, busLine );
													mContext.startActivity(intent);
													mCommonApplication.mIsFavorite = false;
												}
									},
									new IErrorListener(){

										@Override
										public void onError(ErrorResponse arg0,
												BaseHttpRequest arg1, ControlRunnable arg2) {
											if(null != arg0)
											{
												Message msg = new Message();
												msg.obj = arg0.getErrorDesc();
												msg.what = Constants.ERROR;
												mHandler.sendMessage(msg);
											}
										}}); 
								}
								else 
								{
									intent.putExtra( Constants.EXTRA_BUSLINE, busLine );
									startActivity(intent);
									mCommonApplication.mIsFavorite = false;
								}
								
							}
							else 
							{
								intent.setClass( mContext, SearchResultListActivity.class );
								startActivity(intent);
								mCommonApplication.mIsFavorite = false;
							}
						}
			},
			new IErrorListener(){

				@Override
				public void onError(ErrorResponse arg0,
						BaseHttpRequest arg1, ControlRunnable arg2) {
					if(null != arg0)
					{
						Message msg = new Message();
						msg.obj = arg0.getErrorDesc();
						msg.what = Constants.ERROR;
						mHandler.sendMessage(msg);
					}
				}});
		}
	
	}
	
	private void processThread()
	{
		
	      new Thread()
	      {
	         public void run()
	         {
	        		 try 
		        	 {
		        		 PoiSearch poiSearch = new PoiSearch(SearchActivity.this,new PoiSearch.Query(mKeyword, PoiTypeDef.All, mCommonApplication.mCity.admincode)); // 城市区号
		 				 poiSearch.setPageSize(ProtocolDef.PAGE_SIZE);
		 				 mPoiPagedResult = poiSearch.searchPOI();
		 				 mCommonApplication.mPoiPagedResult  = mPoiPagedResult;
		 				 mPoiPagedResult = null;
		 				
		 				 mCommonApplication.mPoiRes = new POIRes();
		 				 mCommonApplication.mPoiRes.mPoiList = new ArrayList<POIItem>();
		 				 mCommonApplication.mPoiRes.mPoiList = getPOIItemByPoiPagedResult(mCommonApplication.mPoiPagedResult, 1);
		 				 intent.putExtra( Constants.EXTRA_POIRES,mCommonApplication.mPoiRes );
		 				 if (mCommonApplication.mPoiPagedResult.getPageCount()>0) 
		 				 {
		 					mTotle = (mCommonApplication.mPoiPagedResult.getPageCount()-1)*ProtocolDef.PAGE_SIZE+mCommonApplication.mPoiPagedResult.getPage(mCommonApplication.mPoiPagedResult.getPageCount()).size();
			 				 
						 }
		 				 else 
		 				 {
		 					mHandler.sendMessage(mHandler.obtainMessage(Constants.FUNC_ALERT, "查询结果为零，请修改关键字"));
		 					return;
						 }
		 				 intent.putExtra(Constants.EXTRA_MTOTLE, mTotle);
		 				 mHandler.sendEmptyMessage(Constants.STARTACTIVITY);
		        	 } 
		        	 catch (Exception e) 
		        	 {
		        		 
		        		 Log.d(" poisearch ", e.getMessage());
		        	 }
	        		 finally
	        		 {
	        			 mHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_ENABLE);
	        		 }
        		 }
	      }.start();
	} 
	public void splitData()
	{
		mData.clear();
		if (mSearchMode == Constants.SEARCH_POI) 
		{
			for (POIItem poiItem : mAutoPoiList)
			{
				mData.add(poiItem.name);
			}
		}
		else if (mSearchMode == Constants.SEARCH_BUSSTATION) 
		{
			for (BusStation busStation : mAutoBusStationList)
			{
				mData.add(busStation.name);
			}
		}
		else if (mSearchMode == Constants.SEARCH_BUSLINE) 
		{
			for (BusLine busLine : mAutoBusLineList)
			{
				mData.add(busLine.linename);
			}
		}
		
		//去重复
		if(null != mData && mData.size()>0)
		{
			Set<String> set = new LinkedHashSet<String>();
			set.addAll(mData);
			mData.clear();
			mData.addAll(set);
		}
		
		// 采用mAdapter.notifyDataSetChanged() 没有效果 !!!
		mAdapter = new ArrayAdapter<String>(SearchActivity.this,R.layout.list_items_autocomplete, mData);
		mSearchTextView.setAdapter(mAdapter);
		mSearchTextView.showDropDown();
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
		public void handleMessage(Message msg)
		{
			switch ( msg.what )
			{
				case Constants.FUNC_ALERT:
				{
					mStateAlert.showAlert((String) msg.obj);
					mHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_ENABLE);
					break;
				}
				case Constants.SEARCH_POI:
				{
					try 
					{
						mAutoPoiList.clear();
						PoiSearch poiSearch = new PoiSearch(SearchActivity.this,new PoiSearch.Query(mKeyword, PoiTypeDef.All, mCommonApplication.mCity.admincode)); // 设置搜索字符串，"010为城市区号"
						mPoiPagedResult = poiSearch.searchPOI();
						mAutoPoiList.addAll(getPOIItemByPoiPagedResult(mPoiPagedResult, 1));
						mPoiPagedResult = null;
						mHandler.sendEmptyMessage(Constants.FUNC_TEXT_AUTOCOMPLETE);
					} 
					catch (Exception e) 
					{
					}
					break;
				}
				case Constants.SEARCH_BUSSTATION:
				{
					ClientSession.getInstance().setDefStateReceiver(null);
					ClientSession.getInstance().setDefErrorReceiver(null);
					ClientSession.getInstance().asynGetResponse(new GetBusStationListRequest( 
					mCommonApplication.mUserLoginInfo.sid, mKeyword, 1, ProtocolDef.PAGE_SIZE , mCommonApplication.mCity.admincode),
					new IResponseListener()
					{
						@Override
						public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2) 
						{
							GetBusStationListResponse busStationListResponse = (GetBusStationListResponse) arg0;
							mAutoBusStationList =  busStationListResponse.mBusStationRes.mBusStationList;
							mHandler.sendEmptyMessage(Constants.FUNC_TEXT_AUTOCOMPLETE);
						}
					},
					new IErrorListener()
					{
						@Override
						public void onError(ErrorResponse arg0,
								BaseHttpRequest arg1, ControlRunnable arg2)
						{
							if ( arg0 != null )
							{
								mHandler.sendEmptyMessage(Constants.FUNC_TEXT_AUTOCOMPLETE);
							}
						}
					});
					break;
				}
				case Constants.SEARCH_BUSLINE:
				{
					ClientSession.getInstance().setDefStateReceiver(null);
					ClientSession.getInstance().setDefErrorReceiver(null);
					ClientSession.getInstance().asynGetResponse(new GetBusLineListRequest( 
					mCommonApplication.mUserLoginInfo.sid, mKeyword, 1, ProtocolDef.PAGE_SIZE , mCommonApplication.mCity.admincode),
					new IResponseListener() 
					{
						@Override
						public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2)
						{
							GetBusLineListResponse busLineListResponse = (GetBusLineListResponse) arg0;
							mAutoBusLineList = busLineListResponse.mBusLineRes.mBusLineList;
							mHandler.sendEmptyMessage(Constants.FUNC_TEXT_AUTOCOMPLETE);
						}
					},
					new IErrorListener()
					{
						@Override
						public void onError(ErrorResponse arg0,
								BaseHttpRequest arg1, ControlRunnable arg2)
						{
							if ( arg0 != null )
							{
								mHandler.sendEmptyMessage(Constants.FUNC_TEXT_AUTOCOMPLETE);
							}
						}
					});
					break;
				
				}
				case Constants.FUNC_TEXT_AUTOCOMPLETE:
				{
					ClientSession.getInstance().setDefStateReceiver(mStateAlert);
					ClientSession.getInstance().setDefErrorReceiver(mStateAlert);
					if(mIsSearchSplitData)
					{
						splitData();
					}
					break ;
				}
				case Constants.STARTACTIVITY:
				{
					if (mTotle ==1) 
					{
						POIItem poiItem = mCommonApplication.mPoiRes.mPoiList.get(0);
						mCommonApplication.mPoiItem = poiItem;
						intent.putExtra( Constants.EXTRA_POIITEM, poiItem);
						intent.setClass( mContext, SearchResultDetailActivity.class );
					}
					else 
					{
						intent.setClass( mContext, SearchResultListActivity.class );
					}
					startActivity(intent);
					mCommonApplication.mIsFavorite = false;
					break;
				}
				case Constants.ERROR:
				{
					String errStr = (String) msg.obj;
					mStateAlert.showAlert(errStr);
					mStateAlert.hidenWaitDialog();
					mSearchButton.setClickable(true);
					mHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_ENABLE);
					break;
				}
				case Constants.TOASTERROR:
				{
					String errStr = (String) msg.obj;
					showToast(errStr);
					mHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_ENABLE);
					break;
				}
				case Constants.ROUTE_SEARCH_DISABLE:
				{
					mStateAlert.showWaitDialog("请稍候...");
					mSearchButton.setClickable(false);
					break;
				}
				case Constants.ROUTE_SEARCH_ENABLE:
				{
					if(null != mStateAlert)
					{
						mStateAlert.hidenWaitDialog();
					}
					mSearchButton.setClickable(true);
					break;
				}
				case Constants.ROUTE_SEARCH_INIT:
				{
					searchInit();
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
