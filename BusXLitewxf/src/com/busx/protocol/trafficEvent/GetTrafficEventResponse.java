package com.busx.protocol.trafficEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cm.framework.protocol.BaseJSONRsponse;
import com.busx.entities.TrafficEvent;
import com.busx.entities.UserLoginInfo;

public class GetTrafficEventResponse extends BaseJSONRsponse implements Serializable
{
	private static final long serialVersionUID = 1L;
	public List<TrafficEvent> mTrafficEventList = new ArrayList<TrafficEvent>();
	public UserLoginInfo mUserLoginInfo = new UserLoginInfo();
	@Override
	protected boolean extractBody(JSONObject arg0)
	{
		try 
		{
			JSONObject dataJsonObject = arg0.getJSONObject("res");
			JSONArray listJsonArray = dataJsonObject.getJSONArray("eventlist");
			for (int i=0,len=listJsonArray.length(); i<len; i++)
			{
				JSONObject trafficEventJsonObject = listJsonArray.getJSONObject(i);
				TrafficEvent trafficEvent = new TrafficEvent();
				trafficEvent.id = trafficEventJsonObject.getString("id");
				trafficEvent.title= trafficEventJsonObject.getString("title");
				trafficEvent.author= trafficEventJsonObject.getString("author");
				trafficEvent.pubtime= trafficEventJsonObject.getString("pubtime");
				trafficEvent.starttime= trafficEventJsonObject.getString("starttime");
				trafficEvent.endtime= trafficEventJsonObject.getString("endtime");
				trafficEvent.cat= trafficEventJsonObject.getString("cat");
				mTrafficEventList.add(trafficEvent);
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
