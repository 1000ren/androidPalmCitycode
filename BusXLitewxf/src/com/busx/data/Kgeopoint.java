package com.busx.data;

import com.amap.mapapi.core.GeoPoint;

public class Kgeopoint {
	/**两个地理坐标之间的距离*/
	public double distance ;
	/**对应的下标*/
	public int index ;
	/**对应的路线的索引值*/
	public int RoutIndex ;
	/**对应的地理坐标*/
	public  GeoPoint geopoint;
	
	public GeoPoint getGeopoint() {
		return geopoint;
	}
	public void setGeopoint(GeoPoint geopoint) {
		this.geopoint = geopoint;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public int getIndex() {
		return index;
	}
	
	public int getRoutIndex() {
		return RoutIndex;
	}
	public void setRoutIndex(int routIndex) {
		RoutIndex = routIndex;
	}
	
	
	
	
}
