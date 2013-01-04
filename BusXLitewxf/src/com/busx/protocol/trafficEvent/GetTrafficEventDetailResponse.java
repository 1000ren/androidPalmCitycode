package com.busx.protocol.trafficEvent;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cm.framework.protocol.BaseJSONRsponse;
import com.busx.entities.TrafficEvent;
import com.busx.entities.UserLoginInfo;

public class GetTrafficEventDetailResponse extends BaseJSONRsponse implements Serializable
{
	private static final long serialVersionUID = 1L;
	public TrafficEvent mTrafficEvent = null;
	public UserLoginInfo mUserLoginInfo = new UserLoginInfo();
	@Override
	protected boolean extractBody(JSONObject arg0)
	{
		try 
		{
			mTrafficEvent = new TrafficEvent();
			JSONArray listJsonArray = arg0.getJSONArray("res");
			for (int i=0,len=listJsonArray.length(); i<len; i++)
			{
				JSONObject trafficEventJsonObject = listJsonArray.getJSONObject(i);
				mTrafficEvent.id = trafficEventJsonObject.getString("id");
				mTrafficEvent.title= trafficEventJsonObject.getString("title");
				mTrafficEvent.author= trafficEventJsonObject.getString("author");
				mTrafficEvent.pubtime= trafficEventJsonObject.getString("pubtime");
				mTrafficEvent.starttime= trafficEventJsonObject.getString("starttime");
				mTrafficEvent.endtime= trafficEventJsonObject.getString("endtime");
				mTrafficEvent.cat= trafficEventJsonObject.getString("cat");
				mTrafficEvent.content = trafficEventJsonObject.getString("content");
			}
			// parse user login info
			mUserLoginInfo.ParseUserLoginInfo(arg0);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return true;
	}


}
