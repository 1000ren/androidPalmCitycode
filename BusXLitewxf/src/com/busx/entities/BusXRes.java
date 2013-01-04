package com.busx.entities;

import java.io.Serializable;
import java.util.List;

public class BusXRes implements Serializable
{
	private static final long serialVersionUID = 13L;

	public int mode;
	public List<BusRoute> busRouteList;
	public int orderMode;

}
