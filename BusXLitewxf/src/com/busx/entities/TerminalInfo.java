package com.busx.entities;

public class TerminalInfo 
{
	//”设备IMEI号”,
	public String IMEI = "";
	//”设备IMSI号”,
	public String IMSI = "";
	
//	public String SMD = "";
	//”屏幕分辨率宽度”,
	public int srmWidth = 0;
	//”屏幕分辨率高度”,
	public int srmHeight = 0;
	//”屏幕物理尺寸”,(密度)
	public String screensize= "";
	//”应用程序名称”
	public String appname = "行人导航";
	//”版本号”,
	public String appver= "";  
	//”渠道id”,
	public String channelid= "";
	//”生产厂家”,(无)
	public String manufacturer= "";
	//”手机型号”,
	public String phonetype= "";
	//”硬件系统版本号”,(无)
	public String hardware= "";
	//”终端操作系统名称”,
	public String os= "Android";
	//”终端操作系统版本号”,
	public String osver= "";
	//”移动网络运营商名称”,
	public String mno= "";
	//”联网方式”
	public String network= "";
	
	public String IPAddress = "";
	
	public GPoint mGPoint=new  GPoint(0, 0);

	//设置发送策略
	public int sendMessageType ;
}
