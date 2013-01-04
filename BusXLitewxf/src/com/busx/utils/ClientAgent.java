package com.busx.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import cm.framework.net.ClientSession;
import cm.framework.net.ControlRunnable;
import cm.framework.net.IErrorListener;
import cm.framework.net.IResponseListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;
import cm.framework.protocol.ErrorResponse;

import com.busx.CommonApplication;
import com.busx.common.StateAlert;
import com.busx.protocol.ingather.IngatherRequest;
import com.busx.protocol.ingather.IngatherResponse;



public class ClientAgent
{
	private Context mContext;
	private CommonApplication mCommonApplication = null;
	private StateAlert mStateAlert = null;

	public ClientAgent(Context context,StateAlert mStateAlert)
	{
		this.mContext = context;
		mCommonApplication = (CommonApplication)mContext.getApplicationContext();
		this.mStateAlert = mStateAlert;
	}

	public void getPhoneClientinfo ()
	{
		StringBuffer clientinfo=new StringBuffer();
		clientinfo.append("{");
		clientinfo.append("\"sid\":").append("\""+mCommonApplication.mUserLoginInfo.sid+"\"").append(",");//”用户会话ID”,
		clientinfo.append("\"uid\":").append("\""+mCommonApplication.mUserLoginInfo.uid+"\"").append(",");//”用户ID”,
		clientinfo.append("\"tm\":").append("\""+Utils.getNowTime()+"\"").append(",");//”时间”,
		clientinfo.append("\"terminal\":");
		clientinfo.append("{");
		//”应用程序名称”,
		clientinfo.append("\"appname\":").append("\""+mCommonApplication.mTerminalInfo.appname+"\"").append(",");
		//”版本号”,
		clientinfo.append("\"appver\":").append("\""+mCommonApplication.mTerminalInfo.appver+"\"").append(",");
		//”渠道id”,
		clientinfo.append("\"channelid\":").append("\""+mCommonApplication.mTerminalInfo.channelid+"\"").append(",");
		//”生产厂家”
		clientinfo.append("\"manufacturer\":").append("\""+mCommonApplication.mTerminalInfo.manufacturer+"\"").append(",");
		//”手机型号”,
		clientinfo.append("\"phonetype\":").append("\""+mCommonApplication.mTerminalInfo.phonetype+"\"").append(",");
		//”硬件系统版本号”,
		clientinfo.append("\"hardware\":").append("\""+mCommonApplication.mTerminalInfo.hardware+"\"").append(",");
		//”终端操作系统名称”,
		clientinfo.append("\"os\":").append("\""+mCommonApplication.mTerminalInfo.os+"\"").append(",");
		//”终端操作系统版本号”,
		clientinfo.append("\"osver\":").append("\""+mCommonApplication.mTerminalInfo.osver+"\"").append(",");
		//”屏幕分辨率宽度”,
		clientinfo.append("\"screenx\":").append("\""+mCommonApplication.mTerminalInfo.srmWidth+"\"").append(",");
		//”屏幕分辨率高度”,
		clientinfo.append("\"screeny\":").append("\""+mCommonApplication.mTerminalInfo.srmHeight+"\"").append(",");
		//”屏幕物理尺寸”,(密度)
		clientinfo.append("\"screensize\":").append("\""+mCommonApplication.mTerminalInfo.screensize+"\"").append(",");
		//”设备IMEI号”,
		clientinfo.append("\"imei\":").append("\""+mCommonApplication.mTerminalInfo.IMEI+"\"").append(",");
		//”设备IMSI号”,
		clientinfo.append("\"imsi\":").append("\""+mCommonApplication.mTerminalInfo.IMSI+"\"").append(",");
		//”移动网络运营商名称”,
		clientinfo.append("\"mno\":").append("\""+mCommonApplication.mTerminalInfo.mno+"\"").append(",");
		//”联网方式”
		clientinfo.append("\"network\":").append("\""+mCommonApplication.mTerminalInfo.network+"\"");
		clientinfo.append("}");
		clientinfo.append("}");
		sendClientinfo(clientinfo.toString());
	}
	
	public void getPhoneChangeClientinfo ()
	{
		StringBuffer clientinfo=new StringBuffer();
		clientinfo.append("{");
		clientinfo.append("\"sid\":").append("\""+mCommonApplication.mUserLoginInfo.sid+"\"").append(",");//”用户会话ID”,
		clientinfo.append("\"uid\":").append("\""+mCommonApplication.mUserLoginInfo.uid+"\"").append(",");//”用户ID”,
		clientinfo.append("\"tm\":").append("\""+Utils.getNowTime()+"\"").append(",");//”时间”,
		clientinfo.append("\"change\":");
		clientinfo.append("{");
		clientinfo.append("\"ip\":").append("\""+mCommonApplication.mTerminalInfo.IPAddress+"\"").append(",");
		clientinfo.append("\"lat\":").append("\""+mCommonApplication.mTerminalInfo.mGPoint.lat+"\"").append(",");
		clientinfo.append("\"lon\":").append("\""+mCommonApplication.mTerminalInfo.mGPoint.lon+"\"");
		clientinfo.append("}");
		clientinfo.append("}");
		sendClientinfo(clientinfo.toString());
	}
	
	//事件统计（不带参数的）
	public void getEventClientinfo (String enentid)
	{
		StringBuffer clientinfo=new StringBuffer();
		clientinfo.append("{");
		clientinfo.append("\"sid\":").append("\""+mCommonApplication.mUserLoginInfo.sid+"\"").append(",");//”用户会话ID”,
		clientinfo.append("\"uid\":").append("\""+mCommonApplication.mUserLoginInfo.uid+"\"").append(",");//”用户ID”,
		clientinfo.append("\"tm\":").append("\""+Utils.getNowTime()+"\"").append(",");//”时间”,
		clientinfo.append("\"event\":");
		clientinfo.append("{");
		clientinfo.append("\"enentid\":").append("\""+enentid+"\"");
		clientinfo.append("}");
		clientinfo.append("}");
		sendClientinfo(clientinfo.toString());
	}
	
	//事件统计（带参数的）
	public void getEventClientinfo (String enentid,Map<String, String> paramMap)
	{
		StringBuffer clientinfo=new StringBuffer();
		clientinfo.append("{");
		clientinfo.append("\"sid\":").append("\""+mCommonApplication.mUserLoginInfo.sid+"\"").append(",");//”用户会话ID”,
		clientinfo.append("\"uid\":").append("\""+mCommonApplication.mUserLoginInfo.uid+"\"").append(",");//”用户ID”,
		
		clientinfo.append("\"tm\":").append("\""+Utils.getNowTime()+"\"").append(",");//”时间”,
		clientinfo.append("\"event\":");
		clientinfo.append("{");
		Set<String> set =paramMap.keySet();
		Iterator<String> it=set.iterator();
		while(it.hasNext())
		{
			String key= (String) it.next();
			clientinfo.append("\""+key+"\":").append("\""+paramMap.get(key)+"\"").append(",");
		}
		clientinfo.append("\"enentid\":").append("\""+enentid+"\"");
		clientinfo.append("}");
		clientinfo.append("}");
		sendClientinfo(clientinfo.toString());
	}

	//统计软件使用时长
	public String getBusxUseTtime (String enentid,Map<String, String> paramMap)
	{
		StringBuffer clientinfo=new StringBuffer();
		clientinfo.append("{");
		clientinfo.append("\"sid\":").append("\""+mCommonApplication.mUserLoginInfo.sid+"\"").append(",");//”用户会话ID”,
		clientinfo.append("\"uid\":").append("\""+mCommonApplication.mUserLoginInfo.uid+"\"").append(",");//”用户ID”,
		
		clientinfo.append("\"tm\":").append("\""+Utils.getNowTime()+"\"").append(",");//”时间”,
		clientinfo.append("\"event\":");
		clientinfo.append("{");
		Set<String> set =paramMap.keySet();
		Iterator<String> it=set.iterator();
		while(it.hasNext())
		{
			String key= (String) it.next();
			clientinfo.append("\""+key+"\":").append("\""+paramMap.get(key)+"\"").append(",");
		}
		clientinfo.append("\"enentid\":").append("\""+enentid+"\"");
		clientinfo.append("}");
		clientinfo.append("}");
		return clientinfo.toString();
	}
	
	public void sendClientinfo(String clientinfo)
	{
		ClientSession.getInstance().setDefErrorReceiver(null);
 		ClientSession.getInstance().setDefStateReceiver(null);
		ClientSession.getInstance().asynGetResponse(
				new IngatherRequest( mCommonApplication.mUserLoginInfo.sid,clientinfo),
				new IResponseListener() {
					@Override
					public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2)
					{
						IngatherResponse ingatherResponse = (IngatherResponse) arg0;
						mCommonApplication.mUserLoginInfo.copySID( ingatherResponse.mUserLoginInfo );
						ClientSession.getInstance().setDefErrorReceiver(mStateAlert);
				 		ClientSession.getInstance().setDefStateReceiver(mStateAlert);
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
							ClientSession.getInstance().setDefErrorReceiver(mStateAlert);
					 		ClientSession.getInstance().setDefStateReceiver(mStateAlert);
						}
					}
				});
	}
}
