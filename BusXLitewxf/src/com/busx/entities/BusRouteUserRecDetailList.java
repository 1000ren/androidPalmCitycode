package com.busx.entities;

import java.io.Serializable;
import java.util.List;

public class BusRouteUserRecDetailList implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int app;
	public int opp;
	public List<BusRouteUserInfo> mBusRouteUserInfo;
	public BusRouteReq busRouteReq;
	
}
