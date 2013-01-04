package com.busx.protocol.poi;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cm.framework.protocol.BaseJSONRsponse;

import com.busx.entities.BusLine;
import com.busx.entities.GPoint;
import com.busx.entities.POIItem;
import com.busx.entities.POIRes;
import com.busx.entities.UserLoginInfo;

public class GetNearbyListResponse extends BaseJSONRsponse implements
		Serializable {
	private static final long serialVersionUID = 1L;

	public POIRes mNearbyRes = null;
	public int mTotal = 0;
	public UserLoginInfo mUserLoginInfo = new UserLoginInfo();

	@Override
	protected boolean extractBody(JSONObject arg0) 
	{
		try 
		{
			JSONObject dataJsonObject = arg0.getJSONObject("res");
			mTotal = dataJsonObject.getInt("num");
			JSONArray listJsonArray = dataJsonObject.getJSONArray("detail");
			if ( mTotal > 0 && listJsonArray != null && listJsonArray.length() > 0 )
			{
				mNearbyRes = new POIRes();
				mNearbyRes.mPoiList = new ArrayList<POIItem>();

				for (int i = 0, len = listJsonArray.length(); i < len; i++)
				{
					POIItem nearby = new POIItem();
					JSONObject nearbyListJsonObject = listJsonArray.getJSONObject(i);
					if (nearbyListJsonObject.has("poiid")) 
					{
						nearby.id = nearbyListJsonObject.getString("poiid");
					}
					else if (nearbyListJsonObject.has("stopid")) 
					{
						nearby.id = nearbyListJsonObject.getString("stopid");
					}
					nearby.name = nearbyListJsonObject.getString("name");
					double lon = nearbyListJsonObject.getDouble("lon");
					double lat = nearbyListJsonObject.getDouble("lat");
					nearby.gPoint = new GPoint(lon, lat);
					nearby.cat = nearbyListJsonObject.getString("cat");
					if (nearbyListJsonObject.has("address")) 
					{
						nearby.addr = nearbyListJsonObject.getString("address");
					}
					nearby.admincode = nearbyListJsonObject.getString("admincode");
					nearby.adminname = nearbyListJsonObject.getString("adminname");
					nearby.score = nearbyListJsonObject.getString("score");
					if (nearbyListJsonObject.has("tel")) 
					{
						nearby.tel = nearbyListJsonObject.getString("tel");
					}
					if (nearbyListJsonObject.has("busline")) 
					{
						JSONArray jsonArray = nearbyListJsonObject.getJSONArray("busline");
						nearby.busline=new ArrayList<BusLine>();
						nearby.buslinename_dialog=new String[jsonArray.length()];
						for (int j = 0; j < jsonArray.length(); j++)
						{
							String buslineInfo[]=jsonArray.getString(j).split(":");
							BusLine busLine=new BusLine();
							busLine.lineid = buslineInfo[0];
							busLine.linename = buslineInfo[1];
							nearby.busline.add(busLine);
							nearby.buslinename_dialog[j]=buslineInfo[1];
							nearby.buslinename+=buslineInfo[1].substring(0,buslineInfo[1].indexOf("("))+",";
						}
						
					}
					mNearbyRes.mPoiList.add(nearby);
				}
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
