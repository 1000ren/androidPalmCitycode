package com.busx.entities;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GPoint implements Serializable
{
	private static final long serialVersionUID = 1L;

	public double lon;
	public double lat;

	public GPoint( double longitude, double latitude )
	{
		lon = longitude;
		lat = latitude;
	}
	public GPoint()
	{
	}
	
	public JSONObject packageJson() 
	{
		JSONObject jo = new JSONObject();
		try 
		{
			jo.put("lon", lon);
			jo.put("lat", lat);
		} 
		catch(JSONException e)
    	{
    		Log.d("packageJson",e.getMessage());
    	}
		return jo;
	}
	
	public GPoint setJSONObjectToObject(JSONObject jsonObj)
	{
		GPoint gPoint = new GPoint();
		try
		{
			gPoint.lat = jsonObj.getDouble("lat");
			gPoint.lon = jsonObj.getDouble("lon");
		}
		catch(JSONException e)
		{
			Log.d("packageJson",e.getMessage());
		}
		return gPoint;
	}

}
