package com.busx.entities;

import java.io.Serializable;
import java.util.List;



public class POIRes implements Serializable
{
	private static final long serialVersionUID = 11L;

	public List<POIItem> mPoiList=null;

	// 用于在周边搜索中传递方向感应值
	public float[] mValues = null;
	
}
