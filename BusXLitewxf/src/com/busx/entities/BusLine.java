package com.busx.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BusLine implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String lineid;//线路Id
	public String linename;//线路名称
	public String length;//运行长度
	public String linetype;//类型
	public String linedire;//上、下行
	public String firsttime;//首班车时间
	public String lasttime;//末班车时间
	public String intervalm;//发车间隔
	public String intervaln;//高峰时发车间隔
	public String hoursl;//运行时间
	public String rhourm;//早高峰
	public String rhourn;//晚高峰
	public String coblineid;//反向线路Id
	public String shortname;

	//收藏标识
    public boolean favoriteflag = false; 
	
	public List<BusStop> busStops=new ArrayList<BusStop>();
	public List<GPoint> gPoints=new ArrayList<GPoint>();
	
	public void copy( BusLine busLine )
	{
		if ( busLine == null )
		{
			return;
		}
		lineid = busLine.lineid;
		linename = busLine.linename;
		length = busLine.length;
		linetype = busLine.linetype;
		linedire = busLine.linedire;
		firsttime = busLine.firsttime;
		lasttime = busLine.lasttime;
		intervalm = busLine.intervalm;
		intervaln = busLine.intervaln;
		hoursl = busLine.hoursl;
		rhourm = busLine.rhourm;
		rhourn = busLine.rhourn;
		coblineid = busLine.coblineid;
		shortname = busLine.shortname;
		
		if ( busLine.busStops.size() > 0 )
		{
			busStops.clear();
			for (int i=0, len=busLine.busStops.size(); i < len; i++)
			{
				busStops.add( busLine.busStops.get(i) );
			}
		}

		if ( busLine.gPoints.size() > 0 )
		{
			gPoints.clear();
			for (int i=0, len=busLine.gPoints.size(); i < len; i++)
			{
				gPoints.add( busLine.gPoints.get(i) );
			}
		}
	}
}
