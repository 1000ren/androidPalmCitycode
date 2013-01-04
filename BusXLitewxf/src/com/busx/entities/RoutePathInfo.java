package com.busx.entities;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RoutePathInfo implements Serializable
{
	private static final long serialVersionUID = 15L;

	public String entry;
	public String exit;
	public String fucode;
	public int fare;
	public String fromname;
	public int jikan;
	public int kyori;
	public int mati;
	public int passstops;
	public int pathindex;
	public String pathinfo;
	public String rucode;
	public String rosenname;
	public GPoint startGPoint;
	public String tucode;
	public String toname;
	public int tr_tm;
	public int type;

	public String pathstops="";
	
	public JSONObject packageJson() 
	{
		JSONObject jo = new JSONObject();
		try {
			jo.put("fucode",fucode==null?"":fucode);
			jo.put("fare",fare);
			jo.put("fromname",fromname==null?"":fromname);
			jo.put("jikan",jikan);
			jo.put("kyori",kyori);
			jo.put("mati",mati);
			jo.put("passstops",passstops);
			jo.put("pathindex",pathindex);
			jo.put("pathinfo",pathinfo==null?"":pathinfo);
			jo.put("rucode",rucode==null?"":rucode);
			jo.put("rosenname",rosenname==null?"":rosenname);
			if(null != startGPoint)
			{
				jo.put("startlat",startGPoint.lat);
				jo.put("startlng",startGPoint.lon);
			}
			else
			{
				jo.put("startlat",0);
				jo.put("startlng",0);
			}
			jo.put("tucode",tucode==null?"":tucode);
			jo.put("toname",toname==null?"":toname);
			jo.put("tr_tm",tr_tm);
			jo.put("type",type);
			jo.put("entry",entry==null?"":entry);
			jo.put("exit",exit==null?"":exit);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("packageJson",e.getMessage());
		}
		
		return jo;
	}
	
	public RoutePathInfo setJSONObjectToObject(JSONObject jsonObj)
	{
		RoutePathInfo routePathInfo = new RoutePathInfo();
		try 
		{
			routePathInfo.entry =  jsonObj.getString("entry");
			routePathInfo.exit =  jsonObj.getString("exit");
			routePathInfo.fucode =  jsonObj.getString("fucode");
			routePathInfo.fare =  jsonObj.getInt("fare");
			routePathInfo.fromname =  jsonObj.getString("fromname");
			routePathInfo.jikan =  jsonObj.getInt("jikan");
			routePathInfo.kyori =  jsonObj.getInt("kyori");
			routePathInfo.mati =  jsonObj.getInt("mati");
			routePathInfo.passstops =  jsonObj.getInt("passstops");
			routePathInfo.pathindex =  jsonObj.getInt("pathindex");
			routePathInfo.pathinfo =  jsonObj.getString("pathinfo");
			routePathInfo.rucode =  jsonObj.getString("rucode");
			routePathInfo.rosenname =  jsonObj.getString("rosenname");
			routePathInfo.startGPoint = new GPoint();
			routePathInfo.startGPoint.lat =  jsonObj.getDouble("startlat");
			routePathInfo.startGPoint.lon =  jsonObj.getDouble("startlng");
			routePathInfo.tucode =  jsonObj.getString("tucode");
			routePathInfo.toname =  jsonObj.getString("toname");
			routePathInfo.tr_tm =  jsonObj.getInt("tr_tm");
			routePathInfo.type =  jsonObj.getInt("type");
			
		}
		catch(JSONException e)
		{
			Log.d("packageJson",e.getMessage());
		}
		return routePathInfo;
	}
}
