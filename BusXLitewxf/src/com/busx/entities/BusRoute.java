package com.busx.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class BusRoute implements Serializable
{
	private static final long serialVersionUID = 14L;

	public int distance;
	public int go_off;
	public int icon;
	public int icount;
	public int pedcount;
	public int peddist;
	public int pedtime;
	public int rosencount;
	public int routeNo;
	public int spendtime;
	public int toll;

	public String shapekey;
	
	public List<RoutePathInfo> pathInfoList;
	public List<RoutePedPathInfo> pedPathInfoList;
	public List<RoutePathShp> pathShpList = new ArrayList<RoutePathShp>();
	public List<RouteGuide> guideList = new ArrayList<RouteGuide>();
	
	public JSONObject packageJson() 
	{
		JSONObject jo = new JSONObject();
		try 
		{
			jo.put("distance",distance);
			jo.put("go_off",go_off);
			jo.put("icon",icon);
			jo.put("icount",icount);
			jo.put("pedcount",pedcount);
			jo.put("peddist",peddist);
			jo.put("pedtime",pedtime);
			jo.put("rosencount",rosencount);
			jo.put("routeNo",routeNo);
			jo.put("spendtime",spendtime);
			jo.put("toll",toll);
			

			JSONArray jsonArray1 = new JSONArray();
			for (RoutePathInfo routePathInfo : pathInfoList) 
			{
				jsonArray1.put(routePathInfo.packageJson());
			}
			
			jo.put("pathInfoList", jsonArray1==null||jsonArray1.length()<1?new JSONArray():jsonArray1);
			
			JSONArray jsonArray2 = new JSONArray();
			for (RoutePedPathInfo routePedPathInfo : pedPathInfoList) 
			{
				jsonArray2.put(routePedPathInfo.packageJson());
			}
			jo.put("pedPathInfoList",jsonArray2==null||jsonArray2.length()<1?new JSONArray():jsonArray2);
			
			JSONArray jsonArray3 = new JSONArray();
			for (RoutePathShp routePedPathInfo : pathShpList) 
			{
				jsonArray3.put(routePedPathInfo.packageJson());
			}
			jo.put("pathShpList",jsonArray3==null||jsonArray3.length()<1?new JSONArray():jsonArray3);
			
			JSONArray jsonArray4 = new JSONArray();
			for (RouteGuide routePedPathInfo : guideList) 
			{
				jsonArray4.put(routePedPathInfo.packageJson());
			}
			jo.put("guideList",jsonArray4==null||jsonArray4.length()<1?new JSONArray():jsonArray4);
		} 
		catch (JSONException e) 
		{
			Log.d("packageJson",e.getMessage());
		}
		return jo;
	} 
	
	public BusRoute setJSONObjectToObject(JSONObject jsonObj)
	{
		BusRoute busRoute = new BusRoute();
		try {
			busRoute.distance = jsonObj.getInt("distance");
			busRoute.go_off = jsonObj.getInt("go_off");
			busRoute.icon = jsonObj.getInt("icon");
			busRoute.icount = jsonObj.getInt("icount");
			busRoute.pedcount = jsonObj.getInt("pedcount");
			busRoute.peddist = jsonObj.getInt("peddist");
			busRoute.pedtime = jsonObj.getInt("pedtime");
			busRoute.rosencount = jsonObj.getInt("rosencount");
			busRoute.routeNo = jsonObj.getInt("routeNo");
			busRoute.spendtime = jsonObj.getInt("spendtime");
			busRoute.toll = jsonObj.getInt("toll");
			busRoute.pathInfoList = new ArrayList<RoutePathInfo>();
			busRoute.pedPathInfoList = new ArrayList<RoutePedPathInfo>();
			busRoute.pathShpList = new ArrayList<RoutePathShp>();
			busRoute.guideList = new ArrayList<RouteGuide>();
			
			JSONArray listJsonArray1 = jsonObj.getJSONArray("pathInfoList");
			if(listJsonArray1 != null && listJsonArray1.length() > 0)
			{
				
				for(int i=0;i<listJsonArray1.length();i++)
				{
					JSONObject jo1 = listJsonArray1.getJSONObject(i);
					RoutePathInfo routePathInfo = new RoutePathInfo();
					busRoute.pathInfoList.add(routePathInfo.setJSONObjectToObject(jo1));
				}
			}
			
			JSONArray listJsonArray2 = jsonObj.getJSONArray("pedPathInfoList");
			if(listJsonArray2 != null && listJsonArray2.length() > 0)
			{
				for(int i=0;i<listJsonArray2.length();i++)
				{
					JSONObject jo2 = listJsonArray2.getJSONObject(i);
					RoutePedPathInfo routePedPathInfo = new RoutePedPathInfo();
					busRoute.pedPathInfoList.add(routePedPathInfo.setJSONObjectToObject(jo2));
				}
			}
			
			
			JSONArray listJsonArray3 = jsonObj.getJSONArray("pathShpList");
			if(listJsonArray3 != null && listJsonArray3.length() > 0)
			{
				for(int i=0;i<listJsonArray3.length();i++)
				{
					JSONObject jo3 = listJsonArray3.getJSONObject(i);
					RoutePathShp routePathShp = new RoutePathShp();
					busRoute.pathShpList.add(routePathShp.setJSONObjectToObject(jo3));
				}
			}
			
			
			JSONArray listJsonArray4 = jsonObj.getJSONArray("guideList");
			if(listJsonArray4 != null && listJsonArray4.length() > 0)
			{
				for(int i=0;i<listJsonArray4.length();i++)
				{
					JSONObject jo4 = listJsonArray4.getJSONObject(i);
					RouteGuide routeGuide = new RouteGuide();
					busRoute.guideList.add(routeGuide.setJSONObjectToObject(jo4));
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("packageJson",e.getMessage());
		}
		
		return busRoute;
	}
}
