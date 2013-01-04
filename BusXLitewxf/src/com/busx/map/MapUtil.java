package com.busx.map;


public class MapUtil
{
	
	/**
	 * 判断给定区域是否超出中国地图范围
	 * @param	rect		
	 * @return
	 */

	public static boolean isInChina(double lon, double lat) {
		if (lon > MapDef.LEFTLON 
		    && lon < MapDef.RIGHTLON 
		    && lat > MapDef.DOWNLAT 
		    && lat < MapDef.UPLAT) {
			return true;
		}
		return false;
	}



}
