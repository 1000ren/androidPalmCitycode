package com.busx.entities;

import java.io.Serializable;

import com.amap.mapapi.poisearch.PoiPagedResult;


public class NearbyKindItem implements Serializable,Cloneable
{
	private static final long serialVersionUID = 1L;
	public String id;
	public String name;
	public int num=0;
	
	public PoiPagedResult mPoiPagedResult = null;
}
