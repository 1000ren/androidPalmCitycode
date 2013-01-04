package com.busx.entities;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RouteGuide implements Serializable
{
	private static final long serialVersionUID = 17L;

	
	public int type;// 0:起点   1:公交    2:地铁    4:步行    9:终点
	public String desc;//步行或者乘车文字描述信息(例如：乘坐386路，经过7站，到达安慧桥东站)
	public GPoint gPoint;//经纬度
	public String mapDesc;//步行或者乘车文字描述信息(例如：乘坐386路，经过7站，到达安慧桥东站)
	public String walkGuide;//步行详情描述(例如：【步行路线】：在学院路直行532米;)
	public int time;//花费时间
	public int distance;//距离(米)
	public String fromName;//出发站点名称
	public String toName;//到达站点名称
	public String lineId;//线路id
	public String fromId;//出发站id
	public String toId;//到达站id
	public String rosenName;//公交或线路名称
	public int passStops;//经过几站
	public String exit;//出口
	public String entry;//进站口
	public List<PedNaviGuide> pedNaviGuide; //步行的分段详情
	
	public JSONObject packageJson() 
	{
		JSONObject jo = new JSONObject();
		try 
		{
			jo.put("type", type);
			jo.put("desc", desc==null?"":desc);
			if(null != gPoint)
			{
				jo.put("lat", gPoint.lat);
				jo.put("lon", gPoint.lon);
			}
			else
			{
				jo.put("lat", 0);
				jo.put("lon", 0);
			}
			
			jo.put("mapDesc", mapDesc==null?"":mapDesc);
			jo.put("walkGuide", walkGuide==null?"":walkGuide);
		} 
		catch(JSONException e)
    	{
    		Log.d("packageJson",e.getMessage());
    	}
		return jo;
	}
	
	public RouteGuide setJSONObjectToObject(JSONObject jsonObj)
	{
		RouteGuide routeGuide = new RouteGuide();
		try
		{
			routeGuide.type =  jsonObj.getInt("type");
			routeGuide.desc =  jsonObj.getString("desc");
			routeGuide.gPoint = new GPoint();
			routeGuide.gPoint.lat =  jsonObj.getDouble("lat");
			routeGuide.gPoint.lon =  jsonObj.getDouble("lon");
			routeGuide.mapDesc =  jsonObj.getString("mapDesc");
			routeGuide.walkGuide =  jsonObj.getString("walkGuide");
		}
		catch(JSONException e)
		{
			Log.d("packageJson",e.getMessage());
		}
		return routeGuide;
	}

}
