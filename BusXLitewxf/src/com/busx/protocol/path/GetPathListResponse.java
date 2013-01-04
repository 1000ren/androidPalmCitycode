package com.busx.protocol.path;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cm.framework.protocol.BaseJSONRsponse;

import com.busx.entities.BusRoute;
import com.busx.entities.BusXRes;
import com.busx.entities.GPoint;
import com.busx.entities.PedNaviGuide;
import com.busx.entities.RoutePathInfo;
import com.busx.entities.RoutePedPathInfo;
import com.busx.entities.UserLoginInfo;

public class GetPathListResponse extends BaseJSONRsponse implements Serializable
{
	private static final long serialVersionUID = 11L;

	public BusXRes mBusXRes = null;
	public UserLoginInfo mUserLoginInfo = new UserLoginInfo();

	@Override
	protected boolean extractBody(JSONObject arg0)
	{
		try 
		{
			mBusXRes = new BusXRes();
			mBusXRes.busRouteList = new ArrayList<BusRoute>();

			JSONObject dataJsonObject = arg0.getJSONObject("res");
			JSONObject resultJsonObject = dataJsonObject.getJSONObject("result");

			// result
			mBusXRes.mode = resultJsonObject.getInt("mode");
			JSONArray routelistJsonArray = resultJsonObject.getJSONArray("routelist");
			if ( routelistJsonArray != null && routelistJsonArray.length() > 0 )
			{
				for ( int i=0,len=routelistJsonArray.length(); i<len; i++ )
				{
					JSONObject routeJsonObject = routelistJsonArray.getJSONObject(i);
					if ( routeJsonObject == null )
					{
						continue;
					}
					BusRoute busRoute = new BusRoute();
					busRoute.distance = routeJsonObject.getInt("distance");
					busRoute.go_off = routeJsonObject.getInt("go_off");
					busRoute.icon = routeJsonObject.getInt("icon");
					busRoute.icount = routeJsonObject.getInt("icount");
					busRoute.pedcount = routeJsonObject.getInt("pedcount");
					busRoute.peddist = routeJsonObject.getInt("peddist");
					busRoute.pedtime = routeJsonObject.getInt("pedtime");
					busRoute.rosencount = routeJsonObject.getInt("rosencount");
					busRoute.routeNo = routeJsonObject.getInt("routeNo");
					busRoute.spendtime = routeJsonObject.getInt("spendtime");
					busRoute.toll = routeJsonObject.getInt("toll");
					busRoute.pathInfoList = new ArrayList<RoutePathInfo>();
					busRoute.pedPathInfoList = new ArrayList<RoutePedPathInfo>();

					if ( busRoute.rosencount > 0 )
					{
						JSONArray zpathlistJsonArray = routeJsonObject.getJSONArray("zpathlist");
						if ( zpathlistJsonArray != null && zpathlistJsonArray.length() > 0 )
						{
							for (int j=0,num=zpathlistJsonArray.length(); j<num; j++)
							{
								JSONObject pathJsonObject = zpathlistJsonArray.getJSONObject(j);
								if ( pathJsonObject == null )
								{
									continue;
								}
								RoutePathInfo routePathInfo = new RoutePathInfo();
								routePathInfo.fucode = pathJsonObject.getString("f_ucode");
								routePathInfo.fare = pathJsonObject.getInt("fare");
								routePathInfo.fromname = pathJsonObject.getString("fromname");
								routePathInfo.jikan = pathJsonObject.getInt("jikan");
								routePathInfo.kyori = pathJsonObject.getInt("kyori");
								routePathInfo.mati = pathJsonObject.getInt("mati");
								routePathInfo.passstops = pathJsonObject.getInt("passstops");
								routePathInfo.pathindex = pathJsonObject.getInt("pathindex");
								routePathInfo.pathinfo = pathJsonObject.getString("pathinfo");
								if (null != routePathInfo.pathinfo && !"".equals(routePathInfo.pathinfo)) 
								{
									String[] pathstops = routePathInfo.pathinfo.split(";");

									for (String pathstop : pathstops) 
									{
										
										routePathInfo.pathstops +=pathstop.split(":")[1]+"/";
									}
									routePathInfo.pathstops=routePathInfo.pathstops.substring(0, routePathInfo.pathstops.length()-1);
								}
															
								routePathInfo.rucode = pathJsonObject.getString("r_ucode");
								routePathInfo.rosenname = pathJsonObject.getString("rosenname");
								double startlat = pathJsonObject.getDouble("startlat");
								double startlng = pathJsonObject.getDouble("startlng");
								routePathInfo.startGPoint = new GPoint( startlng, startlat );
								routePathInfo.tucode = pathJsonObject.getString("t_ucode");
								routePathInfo.toname = pathJsonObject.getString("toname");
								routePathInfo.tr_tm = pathJsonObject.getInt("tr_tm");
								routePathInfo.type = pathJsonObject.getInt("type");
								if ( routePathInfo.type == 2 )
								{
									if ( pathJsonObject.has("entry") )
									{
										routePathInfo.entry = pathJsonObject.getString("entry");
									}
									if ( pathJsonObject.has("exit") )
									{
										routePathInfo.exit = pathJsonObject.getString("exit");
									}
								}
								busRoute.pathInfoList.add( routePathInfo );
							}
						}
					}

					if ( busRoute.pedcount > 0 )
					{
						JSONArray zpedpathlistJsonArray = routeJsonObject.getJSONArray("zpedpathlist");
						if ( zpedpathlistJsonArray != null && zpedpathlistJsonArray.length() > 0 )
						{
							for (int j=0,num=zpedpathlistJsonArray.length(); j<num; j++)
							{
								JSONObject pedpathJsonObject = zpedpathlistJsonArray.getJSONObject(j);
								if ( pedpathJsonObject == null )
								{
									continue;
								}
								RoutePedPathInfo routePedPathInfo = new RoutePedPathInfo();
								routePedPathInfo.jikan = pedpathJsonObject.getInt("jikan");
								routePedPathInfo.kyori = pedpathJsonObject.getInt("kyori");
								routePedPathInfo.pathindex = pedpathJsonObject.getInt("pathindex");
								routePedPathInfo.type = pedpathJsonObject.getInt("type");
								routePedPathInfo.guidecount = pedpathJsonObject.getInt("guidecount");
								if ( routePedPathInfo.guidecount > 0 )
								{
									routePedPathInfo.naviguidelist = new ArrayList<PedNaviGuide>();
									JSONArray naviguideJsonArray = pedpathJsonObject.getJSONArray("naviguide");
									if ( naviguideJsonArray != null && naviguideJsonArray.length() > 0 )
									{
										for (int k=0,size=naviguideJsonArray.length(); k<size; k++)
										{
											JSONObject naviguideJsonObject = naviguideJsonArray.getJSONObject(k);
											if ( naviguideJsonObject == null )
											{
												continue;
											}
											PedNaviGuide pedNaviGuide = new PedNaviGuide();
											pedNaviGuide.dir = naviguideJsonObject.getInt("dir");
											pedNaviGuide.dist = naviguideJsonObject.getInt("dist");
											double lat = naviguideJsonObject.getDouble("latitude");
											double lng = naviguideJsonObject.getDouble("longitude");
											pedNaviGuide.gpt = new GPoint( lng, lat );
											pedNaviGuide.pointno = naviguideJsonObject.getInt("pointno");
											pedNaviGuide.pointtype = naviguideJsonObject.getInt("pointtype");
											pedNaviGuide.roadname = naviguideJsonObject.getString("roadname");
											routePedPathInfo.naviguidelist.add( pedNaviGuide );
										}
									}
								}

								busRoute.pedPathInfoList.add( routePedPathInfo );
							}
						}
					}

					mBusXRes.busRouteList.add( busRoute );
				}
			}
			//获取KEY 集合
			HashMap<Integer,String> keyMap = new HashMap<Integer,String>();
			JSONObject shapekeyJsonObject= dataJsonObject.getJSONObject("shapekeys");
			JSONArray shapekeyJsonArray = shapekeyJsonObject.getJSONArray("shapekeys");
			if (shapekeyJsonArray != null && shapekeyJsonArray.length() > 0) 
			{
				for (int i=0,len=shapekeyJsonArray.length(); i<len; i++)
				{
					JSONObject shapekeyObject = shapekeyJsonArray.getJSONObject(i);
					if ( shapekeyObject == null )
					{
						continue;
					}
					keyMap.put(shapekeyObject.getInt("KeyIndex"), shapekeyObject.getString("shapekey"));
				}
			}

			for (int i=0, len=mBusXRes.busRouteList.size(); i<len; i++)
			{
				BusRoute busRoute = mBusXRes.busRouteList.get(i);
				if ( busRoute == null )
				{
					continue;
				}
				Integer routeNo = new Integer( busRoute.routeNo );
				busRoute.shapekey = keyMap.get(routeNo);
			}
			keyMap.clear();
			
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
