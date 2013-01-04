package com.busx.entities;

import java.io.Serializable;

/**
 * 换成路线详情
 * @author WANGXUFEI
 *
 */
public class BusRouteReqDetail implements Serializable{
	private static final long serialVersionUID = 1L;
	
	// 0:起点   1:公交    2:地铁    4:步行    9:终点
	public int type;
	public String linename;
	public String startstop;
	public String endstop;
	public String num;//经过站数

	
	public boolean equals(BusRouteReqDetail busRouteReqDetail)
	{
		if(this == busRouteReqDetail)
		{
			return true;
		}
		
			
		if(type == busRouteReqDetail.type && linename.equals(busRouteReqDetail.linename) && 
				startstop.equals(busRouteReqDetail.startstop) && endstop.equals(busRouteReqDetail.endstop)
				)
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
}
