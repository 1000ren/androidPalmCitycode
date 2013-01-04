package com.busx.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
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
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;
import cm.framework.net.ClientSession;
import cm.framework.net.ControlRunnable;
import cm.framework.net.IErrorListener;
import cm.framework.net.IResponseListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;
import cm.framework.protocol.ErrorResponse;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.core.PoiItem;
import com.amap.mapapi.poisearch.PoiPagedResult;
import com.amap.mapapi.poisearch.PoiSearch;
import com.amap.mapapi.poisearch.PoiTypeDef;
import com.busx.R;
import com.busx.activity.RoutePosOptDialog.OnOptListItemClick;
import com.busx.activity.RouteSearchPoiDialog.OnRSPListItemClick;
import com.busx.activity.base.BaseActivity;
import com.busx.entities.BusStation;
import com.busx.entities.GPoint;
import com.busx.entities.POIItem;
import com.busx.entities.RoutePosOptItem;
import com.busx.protocol.ProtocolDef;
import com.busx.protocol.path.GetPathListRequest;
import com.busx.protocol.path.GetPathListResponse;
import com.busx.protocol.poi.GetBusStationListRequest;
import com.busx.protocol.poi.GetBusStationListResponse;
import com.busx.utils.ActivityMgr;
import com.busx.utils.Constants;
import com.busx.utils.NetUtil;
import com.busx.utils.Utils;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;

public class RouteActivity extends BaseActivity 
						   implements RecognizerDialogListener 
{ 
	private AutoCompleteTextView mStartTextView;
	private AutoCompleteTextView mGoalTextView;
	private ImageButton mStartVoiceButton;
	private ImageButton mStartOptionButton;
	private ImageButton mGoalVoiceButton;
	private ImageButton mGoalOptionButton;
	private ImageButton mSearchButton;

	private ImageButton mChangeButton;
	private Button mStarttimeTextView;
	private Button mStarttimeButton;

	private boolean isSearch = true;
	/**
	 * 临时变量用来存储时间控件内的时间 
	 * 0-小时
	 * 1-分钟
	 * (24小时)
	 */
	private int[] gettime={};
	private Calendar c;
	// 0-设置出发时间 1-设置到达时间
	private int time_type = 0;

	private RecognizerDialog iatDialog;
	private int voiceInputFlag = 0;

	private String startPos;
	private String destPos;
	private POIItem mStartPoiItem = null;
	private POIItem mDestPoiItem = null;
	private List<POIItem> mStartPoiList;
	private List<POIItem> mDestPoiList;
	private List<POIItem> mAutoPoiList = new ArrayList<POIItem>();
	private final String[] setlist={"设定出发时间","设定到达时间"};
	private boolean mStartFlag = true;

	private String autoKeyWord="";
	private List<String> poidata=new ArrayList<String>();
	private ArrayAdapter<String> mAdapter=null;
	enum ePoiType{
		eStart,
		eGoal,		
	}
	
	ePoiType mePoiType = ePoiType.eStart;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTheme( R.style.translucent );
		setContentView(R.layout.activity_route);

		iatDialog = new RecognizerDialog(this, "appid=" + getString(R.string.app_id));
		iatDialog.setListener(this);

		mStartTextView = (AutoCompleteTextView) findViewById( R.id.autotextview_roadsearch_start );
		mStartVoiceButton = (ImageButton) findViewById( R.id.imagebtn_voice_start );
		mStartOptionButton = (ImageButton) findViewById( R.id.imagebtn_roadsearch_startoption );
		mGoalTextView = (AutoCompleteTextView) findViewById( R.id.autotextview_roadsearch_goal );
		mGoalVoiceButton = (ImageButton) findViewById( R.id.imagebtn_voice_goal );
		mGoalOptionButton = (ImageButton) findViewById( R.id.imagebtn_roadsearch_goaloption );
		mSearchButton = (ImageButton) findViewById( R.id.imagebtn_roadsearch_search );

		mStarttimeTextView=(Button)findViewById( R.id.textview_starttime );
		mStarttimeButton=(Button)findViewById( R.id.button_startTime );
		mChangeButton = (ImageButton)findViewById(R.id.imagebtn_roadsearch_exchange);

		mStartTextView.setThreshold(1);
		mGoalTextView.setThreshold(1);
		// get data
		if ( getDataFromIntent( Constants.EXTRA_STARTPOI ) != null )
		{
			mStartPoiItem = (POIItem) getDataFromIntent( Constants.EXTRA_STARTPOI );
			startPos = mStartPoiItem.name;
			mStartTextView.setText( startPos );
		}
		if ( getDataFromIntent( Constants.EXTRA_DESTPOI ) != null )
		{
			mDestPoiItem = (POIItem) getDataFromIntent( Constants.EXTRA_DESTPOI );
			destPos = mDestPoiItem.name;
			mGoalTextView.setText( destPos );
		}
		
		mStartTextView.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void afterTextChanged(Editable s) 
			{
				autoKeyWord = mStartTextView.getText().toString();
				if (autoKeyWord.endsWith("。")) {
					autoKeyWord=autoKeyWord.substring(0,autoKeyWord.lastIndexOf("。")).trim();
					mStartTextView.setText(autoKeyWord);
				}
				if (autoKeyWord.length()>80) 
				{
					autoKeyWord = autoKeyWord.substring(0, 79);
					mStartTextView.setText(autoKeyWord);
					showToast("字数过长，请缩减");
					return;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count){}
		
		
		});
		
		mStartTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (mStartTextView.getText().toString().equals(Constants.MAPPOS_POI_CUR_NAME)) {
					mStartTextView.selectAll();
				}
			}
		});
		
		mGoalTextView.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void afterTextChanged(Editable s) 
			{
				autoKeyWord = mGoalTextView.getText().toString();
				if (autoKeyWord.endsWith("。")) 
				{
					autoKeyWord=autoKeyWord.substring(0,autoKeyWord.lastIndexOf("。")).trim();
					mGoalTextView.setText(autoKeyWord);
				}
				if (autoKeyWord.length()>80) 
				{
					autoKeyWord = autoKeyWord.substring(0, 79);
					mGoalTextView.setText(autoKeyWord);
					showToast("字数过长，请缩减");
					return;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count){}
		
		});
		
		mGoalTextView.setOnFocusChangeListener(new OnFocusChangeListener() 
		{
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) 
			{
				if (mGoalTextView.getText().toString().equals(Constants.MAPPOS_POI_CUR_NAME)) 
				{
					mGoalTextView.selectAll();
				}
			}
		});
		
		mStartVoiceButton.setOnClickListener( new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				setVoiceInputFlag( 0 );
				showIatDialog();
			}
		});
		
		mStartOptionButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				routePosOpt( true );
			}
		});
		
		mGoalTextView.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 
			}
		});
		
		mGoalVoiceButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setVoiceInputFlag( 1 );
				showIatDialog();
			}
		});
		
		mGoalOptionButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				routePosOpt( false );
			}
		});
		
		mSearchButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				mRouteHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_DISABLE);
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
						mRouteHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_INIT);
					}
				}).start();
			}
		});

		mStarttimeTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSet();
			}
		});
		
		mStarttimeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showTime();
			}
		});
		
		mChangeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				startPos = mStartTextView.getText().toString().trim();
				destPos = mGoalTextView.getText().toString().trim();
				if ( (startPos == null || startPos.length() <= 0) && 
						(destPos == null || destPos.length() <= 0) )
				{
					return;
				}
				if ( mStartPoiItem == null && mDestPoiItem == null )
				{
					String str = startPos;
					startPos = destPos;
					destPos = str;
					mStartTextView.setText( startPos );
					mGoalTextView.setText( destPos );
					return;
				}
				POIItem poiItem = mStartPoiItem;
				mStartPoiItem = mDestPoiItem;
				mDestPoiItem = poiItem;
				String str = startPos;
				startPos = destPos;
				destPos = str;
				mStartTextView.setText( startPos );
				mGoalTextView.setText( destPos );
			}
		});

		getText();


	}

	public int getVoiceInputFlag() {
		return voiceInputFlag;
	}

	public void setVoiceInputFlag(int voiceInputFlag) {
		this.voiceInputFlag = voiceInputFlag;
	}

	@Override
	public void onEnd(SpeechError arg0) {
		
	}
	
	/**
	 * 进行搜索前的检查
	 */
	public void searchInit()
	{
		mStartTextView.clearListSelection();
		mGoalTextView.clearListSelection();
		
		startPos = mStartTextView.getText().toString().trim();
		destPos = mGoalTextView.getText().toString().trim();
		if ( startPos == null || startPos.length() == 0 )
		{
			Message msg = new Message();
     		 msg.what = Constants.TOASTERROR;
     		 msg.obj = "请输入起点";
     		 mRouteHandler.sendMessage(msg);
			return;
		}
		if ( destPos == null || destPos.length() == 0 )
		{
			Message msg = new Message();
     		 msg.what = Constants.TOASTERROR;
     		 msg.obj = "请输入终点";
     		 mRouteHandler.sendMessage(msg);
			return;
		}
		if(!NetUtil.isConnectingToInternet(mContext))
		 {
    		 Message msg = new Message();
    		 msg.what = Constants.TOASTERROR;
    		 msg.obj = "网络错误,建议您检查网络连接后再试";
    		 mRouteHandler.sendMessage(msg);
    		 return;
		 }
		mRouteHandler.sendEmptyMessage(Constants.FUNC_ALERT);
	}

	public void showIatDialog()
	{
		iatDialog.setEngine("sms", null, null);

		iatDialog.setSampleRate(RATE.rate16k);

		if ( getVoiceInputFlag() == 0 )
		{
			mStartTextView.setText( null );
		}
		else if ( getVoiceInputFlag() == 1 )
		{
			mGoalTextView.setText( null );
		}

		iatDialog.show();
	}
	
	
	
	@Override
	protected void onStart() {
		isSearch = true;
		super.onStart();
	}

	@Override
	public void onResults(ArrayList<RecognizerResult> results, boolean isLast)
	{
		StringBuilder builder = new StringBuilder();
		for (RecognizerResult recognizerResult : results) {
			builder.append(recognizerResult.text);
		}

		if ( getVoiceInputFlag() == 0 )
		{
			mStartTextView.append(builder);
			mStartTextView.setSelection(mStartTextView.length());
		}
		else if ( getVoiceInputFlag() == 1 )
		{
			mGoalTextView.append(builder);
			mGoalTextView.setSelection(mGoalTextView.length());
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		ActivityMgr.getActivityManager().clear(this);
		mRouteHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_ENABLE);
	}

	@Override
	protected void onDestroy() 
	{
		Log.i( "BUSX", "RouteActivity onDestroy" );
		super.onDestroy();
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

	
	public String getStartPos() {
		return startPos;
	}

	public void setStartPos(String startPos) {
		this.startPos = startPos;
	}

	public String getDestPos() {
		return destPos;
	}

	public void setDestPos(String destPos) {
		this.destPos = destPos;
	}

	public void routePosOpt( final boolean bStart )
	{
		RoutePosOptDialog dialog = new RoutePosOptDialog(
				RouteActivity.this, mCommonApplication.mRoutePosOptList );
		mStartFlag = bStart;
		if ( bStart )
		{
			dialog.setTitle("设置起点");
		}
		else
		{
			dialog.setTitle("设置终点");
		}
		dialog.show();
		dialog.setOnListClickListener(new OnOptListItemClick()
		{
			@Override
			public void onListItemClick(RoutePosOptDialog dialog, RoutePosOptItem optItem)
			{
				switch ( optItem.id )
				{
					case 1:
					{
						// 使用我的位置
						GeoPoint gpt = mCommonApplication.mLocationOverlay.getMyLocation();
						if ( gpt == null )
						{
							NetUtil.openGPSSettings(mContext);
						}
						else
						{
							if ( mStartFlag )
							{
								POIItem poiItem = new POIItem();
								GPoint gPoint = new GPoint( gpt.getLongitudeE6() * 1.0 / 1E6, gpt.getLatitudeE6() * 1.0 / 1E6 );
								poiItem.gPoint = gPoint;
								poiItem.name = Constants.MAPPOS_POI_CUR_NAME;
								poiItem.id = Constants.MAPPOS_POI_DEFAULT_ID;
								mStartPoiItem = poiItem;
								startPos = Constants.MAPPOS_POI_CUR_NAME;
								mStartTextView.setText( startPos );
							}
							else
							{
								POIItem poiItem = new POIItem();
								GPoint gPoint = new GPoint( gpt.getLongitudeE6() * 1.0 / 1E6, gpt.getLatitudeE6() * 1.0 / 1E6 );
								poiItem.gPoint = gPoint;
								poiItem.name = Constants.MAPPOS_POI_CUR_NAME;
								poiItem.id = Constants.MAPPOS_POI_DEFAULT_ID;
								mDestPoiItem = poiItem;
								destPos = Constants.MAPPOS_POI_CUR_NAME;
								mGoalTextView.setText( destPos );
							}
						}
						break;
					}
					case 3:
					{
						//从地图上选取点
						if (mStartFlag) 
						{
							showToast("在地图上点击您的起点");
						}
						else 
						{
							showToast("在地图上点击您的终点");
						}
						// 新开地图取点 
						Intent intent= new Intent();
						intent.setClass( RouteActivity.this, MapFuncActivity.class );
						intent.putExtra( Constants.EXTRA_MAPPOINTFLAG, mStartFlag );
						startActivityForResult(intent, 0);
					
					}
					default:
					{
						break;
					}
				}
			}
		});
	}

	// 查询起点
	public void startPosSearchResult()
	{
		isSearch = false;
		startPos = mStartTextView.getText().toString().trim();
		if ( mStartPoiItem != null && mStartPoiItem.name.equals(startPos) )
		{
			destPosSearchResult();
		}
		else
		{
			mStartPoiItem = null;
			try 
			{
				List<POIItem> poiItems = getPOIItemByPoiPagedResult(startPos);
				if (poiItems != null && poiItems.size() > 0) 
				{
					if (poiItems.size()==1) 
					{
						mStartPoiItem = poiItems.get(0);
						destPosSearchResult();
					}
					else
					{
						mStartPoiList = poiItems;
						mRouteHandler.sendMessage( mRouteHandler.obtainMessage(Constants.ROUTE_START_SEARCH, ""));

					}
					
				}
				else
				{
					//当找不到POI 点时 查询 站点
					ClientSession.getInstance().asynGetResponse(new GetBusStationListRequest( 
							mCommonApplication.mUserLoginInfo.sid, startPos, 1, ProtocolDef.PAGE_SIZE , mCommonApplication.mCity.admincode),
							new IResponseListener()
							{
								@Override
								public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2) 
								{
									GetBusStationListResponse busStationListResponse = (GetBusStationListResponse) arg0;
									if (busStationListResponse.mBusStationRes.mBusStationList.size()>0) 
									{
										if (busStationListResponse.mBusStationRes.mBusStationList.size()==1) 
										{
											BusStation bs = busStationListResponse.mBusStationRes.mBusStationList.get(0);
											mStartPoiItem = new POIItem();
											mStartPoiItem.gPoint = bs.gPoint;
											mStartPoiItem.name = bs.name;
											mStartPoiItem.adminname = bs.adminname;
											mStartPoiItem.addr = bs.adminname;
											destPosSearchResult();
										}
										else 
										{
											mStartPoiList = new ArrayList<POIItem>();
											for (BusStation bs : busStationListResponse.mBusStationRes.mBusStationList) 
											{
												POIItem poi = new POIItem();
												poi = new POIItem();
												poi.gPoint = bs.gPoint;
												poi.name = bs.name;
												poi.adminname = bs.adminname;
												poi.addr = bs.adminname;
												mStartPoiList.add(poi);
											}
											mRouteHandler.sendMessage( mRouteHandler.obtainMessage(Constants.ROUTE_START_SEARCH, ""));
										}
									}
									else 
									{
										showToast("无搜索起点结果,建议重新设定...");
									}
								}
							});
					mRouteHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_ENABLE);
				}
				
			} 
			catch (Exception e) 
			{
				mRouteHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_ENABLE);
				e.printStackTrace();
			}
		}
	}
	
	// 查询终点
	public void destPosSearchResult()
	{
		destPos = mGoalTextView.getText().toString().trim();
		if ( mDestPoiItem != null && mDestPoiItem.name.equals(destPos) )
		{
			searchRouteResult();
		}
		else
		{
			mDestPoiItem = null;
			try 
			{
				List<POIItem> poiItems_new = getPOIItemByPoiPagedResult(destPos);
				
				if ( poiItems_new.size() > 0) 
				{
					if (poiItems_new.size()==1) 
					{
						mDestPoiItem = poiItems_new.get(0);
						searchRouteResult();
					}
					else
					{
						mDestPoiList = poiItems_new;
						mRouteHandler.sendMessage( mRouteHandler.obtainMessage(Constants.ROUTE_END_SEARCH, ""));
					}
					
				}
				else
				{
					//找不到 poi 点时 查询站点
					ClientSession.getInstance().asynGetResponse(new GetBusStationListRequest( 
							mCommonApplication.mUserLoginInfo.sid, destPos, 1, ProtocolDef.PAGE_SIZE , mCommonApplication.mCity.admincode),
							new IResponseListener()
							{
								@Override
								public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2) 
								{
									GetBusStationListResponse busStationListResponse = (GetBusStationListResponse) arg0;
									if (busStationListResponse.mBusStationRes.mBusStationList.size()>0) 
									{
										if (busStationListResponse.mBusStationRes.mBusStationList.size()==1) 
										{
											BusStation bs = busStationListResponse.mBusStationRes.mBusStationList.get(0);
											mDestPoiItem = new POIItem();
											mDestPoiItem.gPoint = bs.gPoint;
											mDestPoiItem.name = bs.name;
											mDestPoiItem.adminname = bs.adminname;
											mDestPoiItem.addr = bs.adminname;
											destPosSearchResult();
										}
										else 
										{
											mDestPoiList = new ArrayList<POIItem>();
											for (BusStation bs : busStationListResponse.mBusStationRes.mBusStationList) 
											{
												POIItem poi = new POIItem();
												poi = new POIItem();
												poi.gPoint = bs.gPoint;
												poi.name = bs.name;
												poi.adminname = bs.adminname;
												poi.addr = bs.adminname;
												mDestPoiList.add(poi);
											}
											mRouteHandler.sendMessage( mRouteHandler.obtainMessage(Constants.ROUTE_END_SEARCH, ""));
										}
									}
									else 
									{
										showToast("无搜索终点结果,建议重新设定...");
									}
								}
					});
					mRouteHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_ENABLE);
				}
			
			} 
			catch (Exception e) 
			{
				mRouteHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_ENABLE);
				e.printStackTrace();
			}
		}
	}

	public void searchRouteResult()
	{
		String startlatlon = null;
		String destlatlon = null;
		if ( mStartPoiItem != null && mDestPoiItem != null )
		{
			if(null != mStartPoiItem.id && null != mDestPoiItem.id)
			{
				if (mStartPoiItem.id.equals(mDestPoiItem.id) && 
						mStartPoiItem.gPoint.lat==mDestPoiItem.gPoint.lat&&mStartPoiItem.gPoint.lon==mDestPoiItem.gPoint.lon) 
				{
					Message msg = new Message();
					msg.obj = "起点，终点相同，请重新设定！";
					msg.what = Constants.TOASTERROR;
					mRouteHandler.sendMessage(msg);
					return;
				}
			}
			
			startlatlon = mStartPoiItem.gPoint.lat + "," + mStartPoiItem.gPoint.lon;
			destlatlon = mDestPoiItem.gPoint.lat + "," + mDestPoiItem.gPoint.lon;
		}
		if ( startlatlon == null || startlatlon.length() <= 0 || destlatlon == null || destlatlon.length() <= 0 )
		{
			Message msg = new Message();
			msg.obj = "起点，终点设置有误，请重新设定！";
			msg.what = Constants.TOASTERROR;
			mRouteHandler.sendMessage(msg);
			return;
		}
		else
		{
			String timetype = (time_type==0)?"begin":"end";
			String time = Utils.getNowYearMonthAndDay()+gettime[0]+gettime[1]+"00";
			ClientSession.getInstance().setDefStateReceiver(null);
			ClientSession.getInstance().setDefErrorReceiver(null);
			ClientSession.getInstance().asynGetResponse(
					new GetPathListRequest( mCommonApplication.mUserLoginInfo.sid, startlatlon, destlatlon , timetype, time, mCommonApplication.mCity.admincode),
					new IResponseListener() {

						@Override
						public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,
								ControlRunnable arg2) {

							GetPathListResponse pathListResponse = (GetPathListResponse) arg0;
							mCommonApplication.mUserLoginInfo.copySID( pathListResponse.mUserLoginInfo );

							ActivityMgr.getActivityManager().pushActivity(RouteActivity.this);
							Intent intent= new Intent();
							intent.setClass( RouteActivity.this, RouteBusResultActivity.class );
							intent.putExtra( Constants.EXTRA_BUSXRES, pathListResponse.mBusXRes );
							intent.putExtra( Constants.EXTRA_STARTPOI, mStartPoiItem );
							intent.putExtra( Constants.EXTRA_DESTPOI, mDestPoiItem );
							intent.putExtra( Constants.EXTRA_TIMETYPE, mStarttimeTextView.getText().toString() );
							intent.putExtra( Constants.EXTRA_TIME, mStarttimeButton.getText().toString());
							mCommonApplication.mIsFavorite = false;
							startActivity(intent);
							mRouteHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_ENABLE);
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
								mRouteHandler.sendMessage(msg);
							}
						}}
					);
		}
	}
	
	public void showTime(){
		new TimePickerDialog(RouteActivity.this,new OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay,int minute) {
				gettime[0]=hourOfDay;
				gettime[1]=minute;
				getText();
			}
		}, gettime[0], gettime[1], true).show();
	}

	public void showSet(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.choose_set);
		
		builder.setItems(setlist, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				time_type=which;
				getText();
				showTime();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void getText(){
		
		if (time_type==0) {
			mStarttimeTextView.setText(R.string.start_time);
		}else if (time_type==1) {
			mStarttimeTextView.setText(R.string.end_time);
		}
		
		c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		if (gettime.length!=2) {
			gettime=new int[]{hour,minute};
		}
		String new_time=((gettime[0]>9)?gettime[0]:"0"+gettime[0])+":"+((gettime[1]>9)?gettime[1]:"0"+gettime[1]);
		mStarttimeButton.setText(new_time);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// 通过新的MAP获得位置点，但由于多个地图实例有问题，暂时没有启用
		// 
		switch ( resultCode )
		{
			case RESULT_OK:
			{
				GPoint gpt = (GPoint) data.getSerializableExtra( Constants.EXTRA_MAPPOINT );
				if ( gpt == null )
				{
					Log.e( "BUSX", "gpt null" );
				}
				if ( mStartFlag )
				{
					mStartPoiItem = new POIItem();
					mStartPoiItem.gPoint = new GPoint(gpt.lon, gpt.lat);
					mStartPoiItem.name = Constants.MAPPOS_POI_DEFAULT_NAME;
					mStartPoiItem.id = Constants.MAPPOS_POI_DEFAULT_ID;
					startPos = mStartPoiItem.name;
					mStartTextView.setText( startPos );
				}
				else
				{
					mDestPoiItem = new POIItem();
					mDestPoiItem.gPoint = new GPoint(gpt.lon, gpt.lat);
					mDestPoiItem.name = Constants.MAPPOS_POI_DEFAULT_NAME;
					mDestPoiItem.id = Constants.MAPPOS_POI_DEFAULT_ID;
					destPos = mDestPoiItem.name;
					mGoalTextView.setText( destPos );
				}
				break;
			}
			default:
			{
				break;
			}
		}
	}

	
	public void splitPOIItem(){
		poidata=new ArrayList<String>();
		for (POIItem poiItem : mAutoPoiList) {
			poidata.add(poiItem.name);
		}
		mAdapter =new ArrayAdapter<String>(RouteActivity.this,R.layout.list_items_autocomplete, poidata);
		if(mePoiType == ePoiType.eStart)
		{
			mStartTextView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
			mStartTextView.showDropDown();
		}
		else if(mePoiType == ePoiType.eGoal)
		{
			mGoalTextView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
			mGoalTextView.showDropDown();			
		}
	}

	 public List<POIItem> getPOIItemByPoiPagedResult(String keyWord) throws Exception
	 {
		 List<POIItem> list = new ArrayList<POIItem>();
		try 
		{
			PoiSearch poiSearch = new PoiSearch(RouteActivity.this,new PoiSearch.Query(keyWord, PoiTypeDef.All, mCommonApplication.mCity.admincode)); // 城市区号
			PoiPagedResult mPagedResult = poiSearch.searchPOI();
			if (null != mPagedResult ) 
			{
				List<PoiItem> poiItems = mPagedResult.getPage(1);
				
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
	
	private Handler mRouteHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch ( msg.what )
			{
				case Constants.ROUTE_START_SEARCH:
				{
					RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
							RouteActivity.this, mStartPoiList);
					mRouteHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_ENABLE);
					dialog.setTitle("您要找的起点是:");
					dialog.show();
					dialog.setOnListClickListener(new OnRSPListItemClick() {
						@Override
						public void onListItemClick(
								RouteSearchPoiDialog dialog,
								POIItem startpoiItem) {
							mStartPoiItem = startpoiItem;
							mStartTextView.setText( startpoiItem.name );
							mRouteHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_DISABLE);
							destPosSearchResult();
						}
					});
					break;
				}
				case Constants.ROUTE_END_SEARCH:
				{
					RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
							RouteActivity.this, mDestPoiList);
					mRouteHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_ENABLE);
					dialog.setTitle("您要找的终点是:");
					dialog.show();
					dialog.setOnListClickListener(new OnRSPListItemClick() {
						@Override
						public void onListItemClick(
								RouteSearchPoiDialog dialog,
								POIItem destpoiItem) {
							mDestPoiItem = destpoiItem;
							mGoalTextView.setText( destpoiItem.name );
							mRouteHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_DISABLE);
							searchRouteResult();
						}
					});
					break;
				}
				case Constants.FUNC_POI_SEARCH:
				{

					try 
					{
						mAutoPoiList.clear();
						mAutoPoiList.addAll(getPOIItemByPoiPagedResult(autoKeyWord));
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				
					mRouteHandler.sendEmptyMessage(Constants.FUNC_TEXT_AUTOCOMPLETE);
					break;
				}
				case Constants.FUNC_TEXT_AUTOCOMPLETE:
				{
					ClientSession.getInstance().setDefStateReceiver(mStateAlert);
					ClientSession.getInstance().setDefErrorReceiver(mStateAlert);
					if(isSearch)
					{
						splitPOIItem();
					}
					break;
				}
				case Constants.FUNC_ALERT:
				{
					startPosSearchResult();
					break;
				}
				case Constants.ERROR:
				{
					String errStr = (String) msg.obj;
					mStateAlert.showAlert(errStr);
					mRouteHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_ENABLE);
					break;
				}
				case Constants.TOASTERROR:
				{
					String errStr = (String) msg.obj;
					showToast(errStr);
					mRouteHandler.sendEmptyMessage(Constants.ROUTE_SEARCH_ENABLE);
					break;
				}
				case Constants.ROUTE_SEARCH_INIT:
				{
					searchInit();
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
				default:
				{
					break;
				}
			}
		}
	};
	
}

