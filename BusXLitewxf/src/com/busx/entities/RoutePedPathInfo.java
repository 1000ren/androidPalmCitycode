package com.busx.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RoutePedPathInfo implements Serializable
{
	private static final long serialVersionUID = 21L;

	public int jikan;
	public int kyori;
	public int pathindex;
	public int type;
	public int guidecount;
	
	public List<PedNaviGuide> naviguidelist;
	
	public JSONObject packageJson() 
	{
		JSONObject jo = new JSONObject();
		try 
		{
			jo.put("jikan", jikan);
			jo.put("kyori", kyori);
			jo.put("pathindex", pathindex);
			jo.put("type", type);
			jo.put("guidecount", guidecount);
			if (null != naviguidelist && naviguidelist.size()>0) 
			{
				JSONArray jsonArray = new JSONArray();
				for (PedNaviGuide pedNaviGuide : naviguidelist) 
				{
					jsonArray.put(pedNaviGuide.packageJson());
				}
				jo.put("naviguidelist", jsonArray);
			}
			else
			{
				jo.put("naviguidelist", new JSONArray());
			}
		} 
		catch(JSONException e)
    	{
    		Log.d("packageJson",e.getMessage());
    	}
		return jo;
	}
	
	public RoutePedPathInfo setJSONObjectToObject(JSONObject jsonObj)
	{
		RoutePedPathInfo routePedPathInfo = new RoutePedPathInfo();
		try
		{
			routePedPathInfo.jikan =  jsonObj.getInt("jikan");
			routePedPathInfo.kyori =  jsonObj.getInt("kyori");
			routePedPathInfo.pathindex =  jsonObj.getInt("pathindex");
			routePedPathInfo.type =  jsonObj.getInt("type");
			routePedPathInfo.guidecount =  jsonObj.getInt("guidecount");
			routePedPathInfo.naviguidelist = new ArrayList<PedNaviGuide>();
			
			JSONArray listJsonArray = jsonObj.getJSONArray("naviguidelist");
			if(listJsonArray != null && listJsonArray.length()>0)
			{
				for(int j=0;j<listJsonArray.length();j++)
				{
					JSONObject jo = listJsonArray.getJSONObject(j);
					PedNaviGuide pedNaviGuide = new PedNaviGuide();
					routePedPathInfo.naviguidelist.add(pedNaviGuide.setJSONObjectToObject(jo));
				}
			}
		}
		catch(JSONException e)
		{
			Log.d("packageJson",e.getMessage());
		}
		return routePedPathInfo;
	}
	
}
