package com.busx.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NearbyKind implements Serializable  
{
	private static final long serialVersionUID = 1L;

	public String name;
	public String key;
	//用来携带大分类下面的所有二级分类
	public List<NearbyKindItem> nearbykinditems=new ArrayList<NearbyKindItem>();
	//将数量为0 的分类踢出后 剩下的分类（显示在手机端的）
	public List<NearbyKindItem> viewKindItems=new ArrayList<NearbyKindItem>();
	public int num=0;
	public String[] kinditems={};
	
	public void getKindiems()
	{
		kinditems = new String[viewKindItems.size()];
		int k=0;
		for (NearbyKindItem nearby_kinditem : viewKindItems) 
		{
			kinditems[k]=nearby_kinditem.name;
			k++;
		}
	}
}
