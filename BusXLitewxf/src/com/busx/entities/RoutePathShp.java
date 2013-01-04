package com.busx.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RoutePathShp implements Serializable
{
	private static final long serialVersionUID = 16L;

	public int iPathIndex;
	public int iType;
	public int iPointCount;
	public List<GPoint> pointList;
	
	
	public JSONObject packageJson() 
	{
		JSONObject jo = new JSONObject();
		try 
		{
			jo.put("iPathIndex", iPathIndex);
			jo.put("iType", iType);
			jo.put("iPointCount", iPointCount);
			
			if (null != pointList && pointList.size()>0) 
			{
				JSONArray jsonArray = new JSONArray();
				for (GPoint gPoint : pointList) 
				{
					jsonArray.put(gPoint.packageJson());
				}
				jo.put("pointList", jsonArray);
			}
			else
			{
				jo.put("pointList", new JSONArray());
			}
		} 
		catch(JSONException e)
    	{
    		Log.d("packageJson",e.getMessage());
    	}
		return jo;
	}
	
	public RoutePathShp setJSONObjectToObject(JSONObject jsonObj)
	{
		RoutePathShp routePathShp = new RoutePathShp();
		try
		{
			routePathShp.iPathIndex =  jsonObj.getInt("iPathIndex");
			routePathShp.iType =  jsonObj.getInt("iType");
			routePathShp.iPointCount =  jsonObj.getInt("iPointCount");
			routePathShp.pointList = new ArrayList<GPoint>();
			
			JSONArray listJsonArray = jsonObj.getJSONArray("pointList");
			if(listJsonArray != null && listJsonArray.length()>0)
			{
				for(int j=0;j<listJsonArray.length();j++)
				{
					JSONObject jo = listJsonArray.getJSONObject(j);
					GPoint gPoint = new GPoint();
					routePathShp.pointList.add(gPoint.setJSONObjectToObject(jo));
				}
			}
		}
		catch(JSONException e)
		{
			Log.d("packageJson",e.getMessage());
		}
		return routePathShp;
	}
	
	

}
