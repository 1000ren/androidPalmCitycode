package com.busx.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import cm.framework.net.ClientSession;
import cm.framework.net.ControlRunnable;
import cm.framework.net.IErrorListener;
import cm.framework.net.IResponseListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;
import cm.framework.protocol.ErrorResponse;
import cm.framework.utils.ConfigManager;

import com.busx.R;
import com.busx.Version;
import com.busx.activity.base.BaseActivity;
import com.busx.database.SaveManager;
import com.busx.entities.City;
import com.busx.entities.CityVsersion;
import com.busx.entities.Province;
import com.busx.protocol.ProtocolDef;
import com.busx.protocol.city.GetCityRequest;
import com.busx.protocol.city.GetCityResponse;
import com.busx.protocol.login.LoginRequest;
import com.busx.protocol.login.LoginResponse;
import com.busx.protocol.update.AppUpdateRequest;
import com.busx.protocol.update.AppUpdateResponse;
import com.busx.utils.Constants;
import com.busx.utils.NetUtil;
import com.busx.utils.Utils;

/**
 * @author liujingzhou
 *
 */
public class InitActivity extends BaseActivity 
{
	private final Timer mTimer = new Timer();
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_init);
		
		//初始化数据库
		SaveManager.initDatabase(this);
		
		//加载logo 时记录时间（开始时间）
		mCommonApplication.mbeginTime =  Utils.getNowTime();
		
		mTimer.schedule(mTimerTask, 1000);
		 
		
		
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// init 页面时，不显示等待框
		ClientSession.getInstance().setDefErrorReceiver(null);
		ClientSession.getInstance().setDefStateReceiver(null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		// init 页面时，不显示等待框
		ClientSession.getInstance().setDefErrorReceiver(null);
		ClientSession.getInstance().setDefStateReceiver(null);
	}

	private TimerTask mTimerTask = new TimerTask() {

		@Override
		public void run() {
			SaveManager.initDataBaseFile(R.raw.clientdata);
			mHandler.sendEmptyMessage(0);
		}
	};

	public void getTerminalInfo()
	{
		TelephonyManager telMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if (telMgr.getDeviceId() != null)
		{
			mCommonApplication.mTerminalInfo.IMEI = telMgr.getDeviceId();
		}
		if (telMgr.getSubscriberId() != null)
		{
			mCommonApplication.mTerminalInfo.IMSI = telMgr.getSubscriberId();
		}
		if (null != telMgr.getNetworkOperatorName()) 
		{
			mCommonApplication.mTerminalInfo.mno=telMgr.getNetworkOperatorName();
		}
		mCommonApplication.mTerminalInfo.appver=Version.proVer;
		mCommonApplication.mTerminalInfo.channelid = Version.channelID;
		mCommonApplication.mTerminalInfo.phonetype=android.os.Build.MODEL;
		mCommonApplication.mTerminalInfo.osver=android.os.Build.VERSION.RELEASE;
		ConnectivityManager connectionManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		if (null != connectionManager) 
		{
			NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
			if (null!= networkInfo ) 
			{
				mCommonApplication.mTerminalInfo.network=networkInfo.getTypeName();
			}
		}
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mCommonApplication.mTerminalInfo.srmWidth = Math.min(dm.widthPixels, dm.heightPixels);
		mCommonApplication.mTerminalInfo.srmHeight = Math.max(dm.widthPixels, dm.heightPixels);
		mCommonApplication.mTerminalInfo.screensize = dm.density +"";
	}

	public void Login()
	{
		if(NetUtil.isConnectingToInternet(mContext))
		{
			String userNameString = ConfigManager.getInstance(mContext).loadString( Constants.CONFIG_USERNAME );
			String passwordString = "";
			if ( userNameString.equals("") )
			{
				userNameString = ProtocolDef.defGuestName;
				passwordString = ProtocolDef.defGuestPassword;
			}
			else
			{
				passwordString = ConfigManager.getInstance(mContext).loadString( Constants.CONFIG_PASSWORD );
			}
			
			loginNet(userNameString,passwordString);
		}
		else
		{
			Message msg = mHandler.obtainMessage( 10, getResString(R.string.err_net) );
			mHandler.sendMessage( msg );
		}
	}
	
	/**
	 * 跟服务器的用户名密码进行验证
	 * @param userNameString
	 * @param passwordString
	 */
	public void loginNet(String userNameString,String passwordString)
	{
		ClientSession.getInstance().asynGetResponse(
				new LoginRequest( userNameString, passwordString ), 
				new IResponseListener()
				{
					@Override
					public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,
							ControlRunnable arg2)
					{
						// 登录成功
						LoginResponse loginResponse = (LoginResponse)arg0;
						mCommonApplication.mUserLoginInfo.copy( loginResponse.mUserLoginInfo );
						ConfigManager.getInstance(mContext).putString( 
								Constants.CONFIG_USERNAME, mCommonApplication.mUserLoginInfo.userName );
						ConfigManager.getInstance(mContext).putString( 
								Constants.CONFIG_PASSWORD, mCommonApplication.mUserLoginInfo.password );
						mHandler.sendEmptyMessage( 1 );
					}
				}, 
				new IErrorListener()
				{
					@Override
					public void onError(ErrorResponse arg0, BaseHttpRequest arg1,
							ControlRunnable arg2)
					{
						if ( arg0 != null )
						{
							if ( arg0.getErrorDesc() != null && 
									!arg0.getErrorDesc().equals("") )
							{
								//如果上次的用户名密码登陆错误，则使用默认用户名密码进行验证
								loginNet(ProtocolDef.defGuestName,ProtocolDef.defGuestPassword);
							}
							else
							{
								//如果报错，但服务器没有返回错误信息，则退出程序
								Message msg = mHandler.obtainMessage( 10, getResString(R.string.err_server_busy) );
								mHandler.sendMessage( msg );
							}
						}
					}
				});
	}
	
	public void checkVersion()
	{
		ClientSession.getInstance().asynGetResponse(
				new AppUpdateRequest(mCommonApplication.mUserLoginInfo.sid ),
				new IResponseListener()
				{
					@Override
					public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2) 
					{
						AppUpdateResponse appUpdateResponse = (AppUpdateResponse) arg0;
						mCommonApplication.mUserLoginInfo.copySID( appUpdateResponse.mUserLoginInfo );
						mCommonApplication.mAppUpdateInfo = appUpdateResponse.mAppUpdateInfo;
						
						if ( mCommonApplication.mAppUpdateInfo.needUpdate == 1 && 
								mCommonApplication.mAppUpdateInfo.downloadUrl != null )
						{
							// 需要更新
							if ( mCommonApplication.mAppUpdateInfo.enforce == 1 )
							{
								// 需要强制更新
								mHandler.sendEmptyMessage(5);
							}
							else
							{
								// 可选择更新
								mHandler.sendEmptyMessage(6);
							}
						}
						else
						{
							mHandler.sendEmptyMessage(9);
						}

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
							// 更新服务异常，仍正常启动进入
							mHandler.sendEmptyMessage( 9 );
						}
					}
				});
	}

	private void showUpdateDialog()
	{
		new AlertDialog.Builder(this).setTitle(R.string.app_update).setMessage(
				getResString(R.string.update_tip) + 
		mCommonApplication.mAppUpdateInfo.desc).setPositiveButton(R.string.update_now,
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						Intent intent = new Intent();
						intent.setClass(InitActivity.this,
								PackageInstallerActivity.class);
						intent.putExtra( Constants.EXTRA_APPURL, mCommonApplication.mAppUpdateInfo.downloadUrl );
						intent.putExtra(Constants.EXTRA_FILESIZE, mCommonApplication.mAppUpdateInfo.fileSize);
						startActivity(intent);
						finish();
					}
				}).setNegativeButton(R.string.update_later,
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						mHandler.sendEmptyMessage(9);
					}
				}).show();

	}

	private void showEnforceUpdateDialog()
	{
		new AlertDialog.Builder(this).setTitle(R.string.app_update)
				.setMessage( getResString(R.string.update_enforce_tip) + 
				mCommonApplication.mAppUpdateInfo.desc).setPositiveButton(R.string.update_now,
						new DialogInterface.OnClickListener()
								{
							@Override
							public void onClick(DialogInterface dialog,
									int which)
							{
								Intent intent = new Intent();
								intent.setClass(InitActivity.this,
										PackageInstallerActivity.class);
								intent.putExtra( Constants.EXTRA_APPURL, mCommonApplication.mAppUpdateInfo.downloadUrl );
								intent.putExtra(Constants.EXTRA_FILESIZE, mCommonApplication.mAppUpdateInfo.fileSize);
								startActivity(intent);

								finish();
							}
						}).setNegativeButton(R.string.update_later,
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,
									int which) 
							{
								System.exit(0);
							}
						}).show();

	}

	private void sysExit( String strErr )
	{
		new AlertDialog.Builder(this).setTitle(getResString(R.string.app_name))
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage(strErr)
		.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				finish();
			}
			
		}).show();

	}
	
	private void updateCity()
	{
		String version = "1.0.0.0";
		String pubdate = "20120901000000";
		int count = SaveManager.getCount(Constants.TABLE_CITYVERSION,null);
		int provinceCount = SaveManager.getCount(Constants.TABLE_PROVINCE,null);
		if (count>0&&provinceCount>0) 
		{
			CityVsersion[] cityVsersions =new CityVsersion[1];
			CityVsersion cityVsersion =new CityVsersion();
			cityVsersions[0] = cityVsersion;
			SaveManager.readAllData(Constants.TABLE_CITYVERSION, cityVsersions);
			version = cityVsersions[0].version;
			pubdate = cityVsersions[0].last_modified;
		}
		//检查更新
		ClientSession.getInstance().asynGetResponse(new GetCityRequest(version,pubdate,"",mCommonApplication.mUserLoginInfo.sid),
				new IResponseListener() 
		{
					@Override
					public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2)
					{
						GetCityResponse cityListResponse = (GetCityResponse) arg0;
						List<Province> provinceList = cityListResponse.mProvinces;
						if (provinceList.size()>0) 
						{
							//更新数据库
							//清空旧数据
							SaveManager.deleteData(Constants.TABLE_PROVINCE, null);
							SaveManager.deleteData(Constants.TABLE_CITY, null);
							//更新城市数据版本
							CityVsersion cityVsersion = new CityVsersion();
							cityVsersion.version = cityListResponse.mVersion;
							cityVsersion.last_modified = cityListResponse.mLastmodified;
							SaveManager.saveData(Constants.TABLE_CITYVERSION, cityVsersion, 0);
							//更新省份
							for (Province province : provinceList) 
							{
								SaveManager.saveData(Constants.TABLE_PROVINCE, province, 0);
								List<City> cities = province.cities;
								//更新城市数据
								for (City city : cities) 
								{
									SaveManager.saveData(Constants.TABLE_CITY, city, 0);
								}
							
							}
						}
					}
		});
		
	}
	
	private void getMcity()
	{
		//获取上次 退出时 保存的城市信息
		SharedPreferences sharedPreferences = getSharedPreferences("mCitycode", Context.MODE_PRIVATE);
		City city = new City();
		city.admincode = sharedPreferences.getString("admincode", "110000");
		city.adminname = sharedPreferences.getString("adminname", "北京");
		city.adminnamep =sharedPreferences.getString("adminnamep", "beijing"); 
		city.provincecode = sharedPreferences.getString("provincecode", "110000");
		city.lonLats = sharedPreferences.getString("lonLats", "116.363239,39.928563");
		mCommonApplication.mCity =city;
		 
	}
	
	private Handler mHandler = new Handler( new Handler.Callback()
	{
		@Override
		public boolean handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
				{
					// init terminal info
					getTerminalInfo();
					// login
					Login();
					//updateCity
					updateCity();
					getMcity();
					break;
				}
				case 1:
				{
					// check version
					checkVersion();
					mHandler.sendEmptyMessage(Constants.INGATHER);
					break;
				}
				case 5:
				{
					showEnforceUpdateDialog();
					break;
				}
				case 6:
				{
					showUpdateDialog();
					break;
				}
				case 9:
				{
					Intent intent = new Intent();
					intent.setClass(mContext, BusXActivity.class);
					startActivity(intent);
					mTimer.cancel();
					finish();
					break;
				}
				case 10:
				{
					// 登录失败，退出系统
					String strErr = (String)msg.obj;
					sysExit(strErr);
					break;
				}
				case Constants.INGATHER:
				{
					mClientAgent.getPhoneClientinfo();
					break;
				}
				default:
				{
					break;
				}
			}
			return false;
		}
	});

}
