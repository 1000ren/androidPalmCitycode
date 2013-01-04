package com.busx.service;

import cm.framework.net.ClientSession;
import cm.framework.net.ControlRunnable;
import cm.framework.net.IResponseListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

import com.busx.protocol.ingather.IngatherRequest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ClientAgentService extends Service 
{
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}

	@Override
	public void onCreate() 
	{
		super.onCreate();
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		String busxUseTtime = intent.getStringExtra("busxUseTtime");
		String sid = intent.getStringExtra("sid");
		//上传到服务器
		ClientSession.getInstance().setDefErrorReceiver(null);
 		ClientSession.getInstance().setDefStateReceiver(null);
 		ClientSession.getInstance().asynGetResponse(
 				new IngatherRequest( sid,busxUseTtime),
 				new IResponseListener() {
 					@Override
 					public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2)
 					{
 						//关闭service
 						onDestroy();
 					}
 				});
		return super.onStartCommand(intent, flags, startId);
	}
}
