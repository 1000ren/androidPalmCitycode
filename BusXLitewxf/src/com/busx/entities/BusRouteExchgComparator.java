package com.busx.entities;

import java.util.Comparator;

public class BusRouteExchgComparator implements Comparator<BusRoute>
{
	// 换乘次数 --> 时间
	@Override
	public int compare(BusRoute object1, BusRoute object2)
	{
		if ( object1.rosencount > object2.rosencount )
		{
			return 1;
		}
		else 
		{
			if ( object1.rosencount == object2.rosencount )
			{
				if ( object1.spendtime > object2.spendtime )
				{
					return 1;
				}
				else
				{
					if ( object1.spendtime == object2.spendtime )
					{
						return 0;
					}
					else
					{
						return -1;
					}
				}
			}
			else
			{
				return -1;
			}
		}
	}

}
