package com.busx.protocol.poi;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cm.framework.protocol.BaseJSONRsponse;

import com.busx.entities.BusLine;
import com.busx.entities.BusStop;
import com.busx.entities.GPoint;
import com.busx.entities.UserLoginInfo;

public class GetBusStopListResponse extends BaseJSONRsponse implements Serializable
{
	private static final long serialVersionUID = 1L;

	public BusLine mBusLine = null;
	public UserLoginInfo mUserLoginInfo = new UserLoginInfo();

	@Override
	protected boolean extractBody(JSONObject arg0)
	{
		try 
		{
			JSONObject dataJsonObject = arg0.getJSONObject("res");
			if ( dataJsonObject != null )
			{
				mBusLine = new BusLine();
				//线路详细信息
				JSONObject lineDetailJsonObject = dataJsonObject.getJSONObject("linedetail");
				if ( lineDetailJsonObject != null )
				{
					mBusLine.lineid=lineDetailJsonObject.getString("lineid");
					mBusLine.linename=lineDetailJsonObject.getString("linename");
					mBusLine.length=lineDetailJsonObject.getString("length");
					mBusLine.linetype=lineDetailJsonObject.getString("linetype");
					mBusLine.linedire=lineDetailJsonObject.getString("linedire");
					mBusLine.firsttime=lineDetailJsonObject.getString("timef");
					mBusLine.lasttime=lineDetailJsonObject.getString("timel");
					mBusLine.intervalm=lineDetailJsonObject.getString("intervalm");
					mBusLine.intervaln=lineDetailJsonObject.getString("intervaln");
					mBusLine.hoursl=lineDetailJsonObject.getString("hoursl");
					mBusLine.rhourm=lineDetailJsonObject.getString("rhourm");
					mBusLine.rhourn=lineDetailJsonObject.getString("rhourn");
					mBusLine.coblineid=lineDetailJsonObject.getString("coblineid");
					mBusLine.shortname=lineDetailJsonObject.getString("shortname");
				}
				
				//线路站点详细信息
				JSONObject stopDeatilJsonObject = dataJsonObject.getJSONObject("stopdetail");
				JSONArray stops = stopDeatilJsonObject.getJSONArray("detail");
				if(stops != null && stops.length() > 0)
				{
					for (int i=0,len=stops.length(); i<len; i++)
					{
						JSONObject stopJsonObject = stops.getJSONObject(i);
						BusStop busStop=new BusStop();
						busStop.id=stopJsonObject.getString("id");
						busStop.stopid=stopJsonObject.getString("stopid");
						busStop.stopname=stopJsonObject.getString("stopname");
						busStop.tonextlen=stopJsonObject.getString("tonextlen");
						if (isGetFromCache) 
						{
							double lon = stopJsonObject.getDouble( "stoplon" );
							double lat = stopJsonObject.getDouble( "stoplat" );
							GPoint gPoint=new GPoint(lon,lat);
							busStop.gPoint=gPoint;
						}
						
						mBusLine.busStops.add( busStop );
					}
				}
				//画地图用的点
				JSONObject pointsJsonObject = dataJsonObject.getJSONObject("points");
				JSONArray points = pointsJsonObject.getJSONArray("detail");
				if(points != null && points.length() > 0)
				{
					for (int i=0,len=points.length(); i<len; i++){
						JSONObject pointJsonObject = points.getJSONObject(i);
						double lon = pointJsonObject.getDouble( "x" );
						double lat = pointJsonObject.getDouble( "y" );
						GPoint gPoint=new GPoint(lon,lat);
						mBusLine.gPoints.add( gPoint );
					}
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
