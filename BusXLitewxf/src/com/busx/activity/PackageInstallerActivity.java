package com.busx.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cm.framework.net.ClientSession;
import cm.framework.net.ControlRunnable;
import cm.framework.net.IErrorListener;
import cm.framework.net.IResponseListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;
import cm.framework.protocol.ErrorResponse;

import com.busx.R;
import com.busx.Version;
import com.busx.activity.base.BaseActivity;
import com.busx.protocol.update.AppUpdateRequest;
import com.busx.protocol.update.AppUpdateResponse;
import com.busx.utils.Constants;

public class PackageInstallerActivity extends BaseActivity
{
	private Button mUpdateButton;
	private String mAppURL = null;
	private int fileSize = 0;
	private boolean sdCardExit;
	private TextView mAppVersionTextView;
	private int mMode = 0; // 0：正常模式，1：初始化时进入模式
	
	private ProgressDialog progressDialog = null;
	private final int PROGRESS_DIALOG = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_appupdate);
		
		mAppURL = getIntent().getStringExtra( Constants.EXTRA_APPURL );
		fileSize = getIntent().getIntExtra(Constants.EXTRA_FILESIZE, 0);
		
		if (mAppURL != null && !mAppURL.equals(""))
		{
			mMode = 1;
		}

		TextView titleTextView = (TextView) findViewById(R.id.title_text);
		titleTextView.setText( this.getResString( R.string.menu_appupdate ) );

		mAppVersionTextView = (TextView) findViewById(R.id.text_version);
		mAppVersionTextView.setText( getResString(R.string.str_version)+"\n" + Version.proVer);
		mUpdateButton = (Button) findViewById(R.id.btn_update);

		mUpdateButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mUpdateButton.setClickable(false);
				
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
						if ( mMode == 1 && mAppURL != null && !mAppURL.equals("") )
						{
							mHandler.sendEmptyMessage(10);
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
									downLoadFile();
								}
							}).start();
							
						}
						else
						{
							checkVersion();
						}
					}
				}).start();
				
			}
		});

		// 判断SD Card是否插入
		sdCardExit = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	public void checkVersion()
	{
		ClientSession.getInstance().asynGetResponse(
				new AppUpdateRequest(mCommonApplication.mUserLoginInfo.sid),
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
							mAppURL = mCommonApplication.mAppUpdateInfo.downloadUrl;
							fileSize = mCommonApplication.mAppUpdateInfo.fileSize;
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
							// 不需要更新
							mHandler.sendEmptyMessage(0);
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
							// 更新服务异常
							mHandler.sendEmptyMessage(9);
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
						mHandler.sendEmptyMessage(8);
					}
				}).setNegativeButton(R.string.update_later,
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
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
								mHandler.sendEmptyMessage(8);
							}
						}).setNegativeButton(R.string.update_later,
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,
									int which) 
							{
								// 退出系统
								System.exit(0);
							}
						}).show();

	}

	public boolean saveApp(byte data[], String fileName)
	{
		File file = new File(Environment.getExternalStorageDirectory() + "/"
				+ fileName);

		if (file.exists())
		{
			file.delete();
		}
		try
		{
			file.createNewFile();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			return false;
		}

		FileOutputStream out;
		try
		{
			out = new FileOutputStream(file);
			out.write(data);
			out.flush();
			out.close();
			return true;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// 按下的如果是BACK，同时没有重复
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (mMode == 1)
			{
				System.exit(0);
			}
			else
			{
				mCommonApplication.mIsBack = true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	

	@Override
	protected void onResume() 
	{
		super.onResume();
		mUpdateButton.setClickable(true);
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
					mStateAlert.showAlert( R.string.update_noneed );
					mUpdateButton.setClickable(true);
					break;
				}
				case 1:
				{
					checkVersion();
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
					mUpdateButton.setClickable(true);
					break;
				}
				case 8:
				{
					downLoadFile();
					break;
				}
				case 9:
				{
					// 更新服务异常
					mStateAlert.hidenWaitDialog();
					mStateAlert.showAlert( R.string.update_err );
					mUpdateButton.setClickable(true);
					break;
				}
				case 10:
				{
					//弹出下载进度对话框
					showDialog(PROGRESS_DIALOG);
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
	
	
	
	/**
	 * 下载apk程序代码
	 * 
	 * */
	private void downLoadFile()
	{
		if ( !sdCardExit )
		{
			showToast( R.string.sdcard_miss );
			return;
		}
		String fileName = Version.APP_NAME;
		String httpUrl = mAppURL;
		File file = new File(Environment.getExternalStorageDirectory() + "/"
				+ fileName);
		if (file.exists())
		{
			file.delete();
		}
		try
		{
			file.createNewFile();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			return;
		}

		
		HttpURLConnection conn = null;
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			URL url = new URL(httpUrl);
			try {
				conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				is = conn.getInputStream();
				fos = new FileOutputStream(file);
				byte[] buf = new byte[512];
				// 目前已经已下载的文件的大小
				int downFileSize = 0;
				if (conn.getResponseCode() >= 400) {
					Toast.makeText(PackageInstallerActivity.this, "连接超时",
							Toast.LENGTH_SHORT).show();
				} else {
					do {
						if (is != null) {
							// 如果获得总文件大小，就显示下载进度
							if (fileSize != 0) {
								// 线程沉睡一毫秒，下载百分比会显示的更精确
								try {
									Thread.sleep(10);
								} catch (InterruptedException e)
								{
									e.printStackTrace();
								}
								progressHandler.sendEmptyMessage(downFileSize);
							}

							int numRead = is.read(buf);
							if (numRead <= 0) {
								break;
							} else {
								fos.write(buf, 0, numRead);
							}
							downFileSize += numRead;

						} else {
							break;
						}
					} while (true);
				}
				// 线程休眠2秒钟，不然不会显示到100%就开始安装
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e)
				{
					Log.e("DownAppError", "下载应用错误");
					e.printStackTrace();
				}
				openFile(file);
				exitApp();
				

			} catch (IOException e)
			{
				e.printStackTrace();
			}finally{
				try {
					conn.disconnect();
					fos.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 打开APK程序代码
	 * 
	 * @param file
	 */
	private void openFile(File file)
	{
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	/**
	 * 退出程序
	 * 
	 */
	private void exitApp()
	{
		Intent oIntent = new Intent();
		oIntent.putExtra(Constants.PARAMS_SIGN, true);
		setResult(RESULT_OK, oIntent);
		finish();
	}
	
	private Handler progressHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			int rsCount = msg.what * 100 / fileSize;
			progressDialog.setProgress(rsCount);
			if (progressDialog.getProgress() >= 100)
			{
				// 关闭下载进度对话框
				progressDialog.setMessage("下载完成");
				try
				{
					Thread.sleep(1000);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				progressDialog.dismiss();
			}
		}
	};
	
	@Override
	public Dialog onCreateDialog(int id)
	{
		switch (id) {
		case PROGRESS_DIALOG:
			// this表示该对话框是针对当前Activity的
			progressDialog = new ProgressDialog(this);
			// 设置最大值为100
			progressDialog.setMax(100);
			// 设置进度条风格STYLE_HORIZONTAL
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setTitle("下载进度");
			progressDialog.setMessage("请稍等....");
			progressDialog.setIcon(R.drawable.ic_launcher);

			break;
		}
		return progressDialog;
	}

	@Override
	public void onPrepareDialog(int id, Dialog dialog)
	{
		switch (id)
		{
		case PROGRESS_DIALOG:
			// 将进度条清0
			progressDialog.incrementProgressBy(-progressDialog.getProgress());
			break;
		}
	}
	
}
