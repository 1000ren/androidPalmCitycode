package com.busx.entities;

import java.util.Comparator;

public class BusRouteTimeComparator implements Comparator<BusRoute>
{
	// 采用推荐排序
	// 首先是直达，然后是时间少

	@Override
	public int compare(BusRoute object1,
			BusRoute object2)
	{
		if ( object1.rosencount == object2.rosencount && object1.rosencount == 1 )
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
		else if ( object1.rosencount == 1 && object2.rosencount > 1 )
		{
			return -1;
		}
		else if ( object1.rosencount > 1 && object2.rosencount == 1 )
		{
			return 1;
		}
		else
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
	}

}
