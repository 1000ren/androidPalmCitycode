package com.busx.entities;

import java.io.Serializable;
import java.util.List;


/**
 * 提交用户推荐json中的实体类
 * @author WANGXUFEI
 *
 */
public class BusRouteReq implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public int time;//花费时间
	public int cost;// 线路花费（单位为分）
	public int exnum;//换乘次数
	public List<BusRouteReqDetail> exdetail;//换成详情

	
	public boolean equals(BusRouteReq brr)
	{
		if(this == brr)
		{
			return true;
		}
		if(cost == brr.cost && exnum == brr.exnum)
		{
			return getBooleanList(brr.exdetail);
		}
		return false;
	}
	
	private boolean getBooleanList(List<BusRouteReqDetail> list)
	{
		if(exdetail == list)
		{
			return true;
		}
		if(exdetail.size() == list.size())
		{
			for(int i=0;i<exdetail.size();i++)
			{
				if(!exdetail.get(i).equals(list.get(i)))
				{
					return  false;
				}
			}
		}
		else
		{
			return false;
		}
		return true;
	}
}
