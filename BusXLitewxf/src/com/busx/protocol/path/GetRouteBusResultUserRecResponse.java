package com.busx.protocol.path;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.busx.entities.BusRouteReq;
import com.busx.entities.BusRouteReqDetail;
import com.busx.entities.BusRouteUserInfo;
import com.busx.entities.BusRouteUserRec;
import com.busx.entities.BusRouteUserRecDetail;
import com.busx.entities.UserLoginInfo;

import cm.framework.protocol.BaseJSONRsponse;

public class GetRouteBusResultUserRecResponse extends BaseJSONRsponse
implements Serializable 
{
	
	private static final long serialVersionUID = 1L;
	public BusRouteUserRec mBusRouteUserRec = null; 
	public int mTotal = 0;
	public UserLoginInfo mUserLoginInfo = new UserLoginInfo();

	@Override
	protected boolean extractBody(JSONObject arg0) 
	{
		try {
			String status = arg0.getString("status");
			
			if(status.equals("0"))
			{
				JSONObject dataJsonObject = arg0.getJSONObject("res");
				mTotal = dataJsonObject.getInt("num");
				JSONArray listJsonArray = dataJsonObject.getJSONArray("detail");
				
				if(mTotal>0 && null!=listJsonArray && listJsonArray.length()>0)
				{
					mBusRouteUserRec = new BusRouteUserRec();
					mBusRouteUserRec.busRouteUserRecDetail = new ArrayList<BusRouteUserRecDetail>();
					for(int i=0;i<listJsonArray.length();i++)
					{
						//Json第一级
						JSONObject jo = listJsonArray.getJSONObject(i);
						BusRouteUserRecDetail busRouteUserRecDetail = new BusRouteUserRecDetail();
						BusRouteUserInfo busRouteUserInfo = new BusRouteUserInfo();
						busRouteUserInfo.recid = jo.getString("id");
						busRouteUserInfo.usrname = jo.getString("usrname");
						busRouteUserInfo.nickname = jo.getString("nickname");
						busRouteUserInfo.time = jo.getString("time");
						busRouteUserInfo.pathcomid = jo.getString("pathcomid");
						busRouteUserInfo.approve = jo.getInt("approve");
						busRouteUserInfo.opposition = jo.getInt("opposition");
						
						
						//Json第二级
						JSONObject jo1 = jo.getJSONObject("content");
						if(null != jo1 && jo1.length()>0)
						{
							BusRouteReq busRouteReq = new BusRouteReq();
							busRouteReq.time = jo1.getInt("time");
							busRouteReq.cost = jo1.getInt("cost");
							busRouteUserInfo.reason = jo1.get("reason")==null?"":jo1.getString("reason");
							busRouteReq.exnum = jo1.getInt("exnum");
							busRouteReq.exdetail = new ArrayList<BusRouteReqDetail>();
							
							//Json第三级
							JSONArray ja = jo1.getJSONArray("exdetail");
							if(null != ja && ja.length()>0)
							{
								if(null != ja && ja.length()>0)
								{
									for(int j=0;j<ja.length();j++)
									{
										JSONObject jo2 = ja.getJSONObject(j);
										BusRouteReqDetail busRouteReqDetail = new BusRouteReqDetail();
										
										busRouteReqDetail.linename = jo2.getString("linename");
										busRouteReqDetail.startstop = jo2.getString("startstop");
										busRouteReqDetail.endstop = jo2.getString("endstop");
										busRouteReqDetail.num = jo2.getString("num");
										busRouteReqDetail.type = jo2.getInt("type");
										busRouteReq.exdetail.add(busRouteReqDetail);
									}
								}
								busRouteUserRecDetail.busRouteReq = busRouteReq;
							}
						}
						busRouteUserRecDetail.mBusRouteUserInfo = busRouteUserInfo;
						mBusRouteUserRec.busRouteUserRecDetail.add(busRouteUserRecDetail);
					}
				}
			}
			mUserLoginInfo.ParseUserLoginInfo(arg0);
		} 
		catch (JSONException e) 
		{
			
			e.printStackTrace();
		}
		return true;
	}

}
