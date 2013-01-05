package com.busx.data;

import com.amap.mapapi.core.GeoPoint;
import com.busx.entities.GPoint;

public class MgeoPoint {
	
	public  GeoPoint geopoint;
	public GPoint gPoint;
	public GPoint getgPoint() {
		return gPoint;
	}
	public void setgPoint(GPoint gPoint) {
		this.gPoint = gPoint;
	}
	public  int index;
	public int distance;
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public  GeoPoint getGeopoint() {
		return geopoint;
	}
	public  void setGeopoint(GeoPoint geopoint) {
		this.geopoint = geopoint;
	}
	public int getIndex() {
		return index;
	}
	public  void setIndex(int index) {
		this.index = index;
	}
	
}
