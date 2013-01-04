package com.busx.entities;

import java.io.Serializable;

import org.json.JSONObject;

import android.util.Log;

public class PedNaviGuide implements Serializable
{
	private static final long serialVersionUID = 22L;

	public int dir;
	public int dist;
	public int pointno;
	public int pointtype;
	public String roadname;
	public GPoint gpt;

	public JSONObject packageJson() 
	{
		JSONObject jo = new JSONObject();
		try 
		{
			jo.put("dir",dir);
			jo.put("dist",dist);
			jo.put("pointno",pointno);
			jo.put("pointtype",pointtype);
			jo.put("roadname",roadname==null?"":roadname);
			if(null != gpt)
			{
				jo.put("latitude",gpt.lat);
				jo.put("longitude",gpt.lon);
			}
			else
			{
				jo.put("latitude",0);
				jo.put("longitude",0);
			}
		} catch (Exception e) {
			Log.d("packageJson",e.getMessage());
		}
		
		return jo;
	}
	
	public PedNaviGuide setJSONObjectToObject(JSONObject jsonObj)
	{
		PedNaviGuide pedNaviGuide = new PedNaviGuide();
		try 
		{
			pedNaviGuide.dir = jsonObj.getInt("dir");
			pedNaviGuide.dist = jsonObj.getInt("dist");
			pedNaviGuide.pointno = jsonObj.getInt("pointno");
			pedNaviGuide.pointtype = jsonObj.getInt("pointtype");
			pedNaviGuide.roadname = jsonObj.getString("roadname");
			pedNaviGuide.gpt = new GPoint();
			pedNaviGuide.gpt.lat = jsonObj.getDouble("latitude");
			pedNaviGuide.gpt.lon = jsonObj.getDouble("longitude");
		}
		catch (Exception e) 
		{
			Log.d("packageJson",e.getMessage());
		}
		return pedNaviGuide;
	}
}
