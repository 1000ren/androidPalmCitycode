package com.busx.activity.map;

import com.busx.R;

public class MapPublicMethod {
	
	/**
	 * 根据position获得相应位置的图片
	 * @param index
	 * @return R.drawable.ImageResource
	 */
	public static int getImageResource(int index,int searchMode)
	{
			if(index==0)
			{
				return R.drawable.icon_marka;
			}
			else if(index==1)
			{
				return R.drawable.icon_markb;
			}
			else if(index==2)
			{
				return R.drawable.icon_markc;
			}
			else if(index==3)
			{
				return R.drawable.icon_markd;
			}
			else if(index==4)
			{
				return R.drawable.icon_marke;
			}
			else if(index==5)
			{
				return R.drawable.icon_markf;
			}
			else if(index==6)
			{
				return R.drawable.icon_markg;
			}
			else if(index==7)
			{
				return R.drawable.icon_markh;
			}
			else if(index==8)
			{
				return R.drawable.icon_marki;
			}
			else if(index==9)
			{
				return R.drawable.icon_markj;
			}
		return R.drawable.icon_gcoding;
	}
}
