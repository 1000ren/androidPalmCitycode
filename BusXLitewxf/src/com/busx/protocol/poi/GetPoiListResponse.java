package com.busx.protocol.poi;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cm.framework.protocol.BaseJSONRsponse;

import com.busx.entities.GPoint;
import com.busx.entities.POIItem;
import com.busx.entities.POIRes;
import com.busx.entities.UserLoginInfo;

public class GetPoiListResponse extends BaseJSONRsponse implements Serializable
{
	private static final long serialVersionUID = 1L;

	public POIRes mPoiRes = null;
	public int mTotal = 0;
	public UserLoginInfo mUserLoginInfo = new UserLoginInfo();

	@Override
	protected boolean extractBody(JSONObject arg0)
	{
		try 
		{
			JSONObject dataJsonObject = arg0.getJSONObject("res");
			mTotal = dataJsonObject.getInt("numFound");
			JSONArray listJsonArray = dataJsonObject.getJSONArray("detail");
			if(listJsonArray != null && listJsonArray.length() > 0)
			{
				mPoiRes = new POIRes();
				mPoiRes.mPoiList = new ArrayList<POIItem>();

				for (int i=0,len=listJsonArray.length(); i<len; i++)
				{
					POIItem poiItem = new POIItem();
					JSONObject poiJsonObject = listJsonArray.getJSONObject(i);
					poiItem.cat = poiJsonObject.getString( "cat" );
					if(poiItem.cat.equals("busstop") || poiItem.cat.equals("subwaystop"))
					{
						poiItem.stopid = poiJsonObject.getString( "stopid" );
					}
					else
					{
						poiItem.id = poiJsonObject.getString( "poiid" );
						poiItem.addr = poiJsonObject.getString( "address" );
					}
					
					poiItem.name = poiJsonObject.getString( "name" );
					double lon = poiJsonObject.getDouble( "lon" );
					double lat = poiJsonObject.getDouble( "lat" );
					
					poiItem.gPoint = new GPoint(lon, lat);
					poiItem.admincode = poiJsonObject.getString( "admincode" );
					poiItem.adminname = poiJsonObject.getString( "adminname" );
					if (poiJsonObject.has("tel")) 
					{
						poiItem.tel = poiJsonObject.getString("tel");
					}
					mPoiRes.mPoiList.add( poiItem );
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
