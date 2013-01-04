package com.busx.protocol.path;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cm.framework.protocol.BaseJSONRsponse;

import com.busx.entities.GPoint;
import com.busx.entities.RoutePathShp;
import com.busx.entities.UserLoginInfo;

public class GetPathGuideListResponse extends BaseJSONRsponse implements Serializable
{
	private static final long serialVersionUID = 11L;

	public UserLoginInfo mUserLoginInfo = new UserLoginInfo();
	public List<RoutePathShp> routePathShpList = null;
	@Override
	protected boolean extractBody(JSONObject arg0)
	{
		try 
		{
			// routeshape 形状点数据（画图用）
			JSONObject dataJsonObject = arg0.getJSONObject("res");
			
			routePathShpList = new ArrayList<RoutePathShp>();
			JSONArray pathListJsonArray = dataJsonObject.getJSONArray("PathList");
			if ( pathListJsonArray != null && pathListJsonArray.length() > 0 )
			{
				for (int j=0,num=pathListJsonArray.length(); j<num; j++)
				{
					JSONObject pathJsonObject = pathListJsonArray.getJSONObject(j);
					if ( pathJsonObject == null )
					{
						continue;
					}
					RoutePathShp routePathShp = new RoutePathShp();
					routePathShp.iPathIndex = pathJsonObject.getInt("iPathIndex");
					routePathShp.iType = pathJsonObject.getInt("iType");
					routePathShp.iPointCount = pathJsonObject.getInt("iPointCount");
					if ( routePathShp.iPointCount > 0 )
					{
						routePathShp.pointList = new ArrayList<GPoint>();
						JSONArray pointListJsonArray = pathJsonObject.getJSONArray("PointList");
						if ( pointListJsonArray != null && pointListJsonArray.length() > 0 )
						{
							for (int k=0,size=pointListJsonArray.length(); k<size; k++)
							{
								JSONObject pointJsonObject = pointListJsonArray.getJSONObject(k);
								if ( pointJsonObject == null )
								{
									continue;
								}
								double lon = pointJsonObject.getDouble("x");
								double lat = pointJsonObject.getDouble("y");
								GPoint gPoint = new GPoint( lon, lat );
								routePathShp.pointList.add( gPoint );
							}
						}
					}
					routePathShpList.add( routePathShp );
				}
			}
			
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return true;
	}

}
