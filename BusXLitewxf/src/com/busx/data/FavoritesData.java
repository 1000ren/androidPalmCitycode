//**************************************************************
//Copyright (C) 2011 Neusoft Co,Ltd. All Rights Reserved.
//-------------------------------------------------------------------
//项目名 ： CnOnFootNavi
//-------------------------------------------------------------------
//版本			作者			完成日			备注
//1.0.0			yangyang	2011/07/07		新規
//**************************************************************
package com.busx.data;

import java.util.Hashtable;

import com.busx.database.SaveDataListener;
import com.busx.database.SaveManager;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 收藏夹功能使用的数据对象
 *
 * */
public class FavoritesData implements SaveDataListener
{
	/**数据库表字段:类型id*/
	public final static String TYPEID = "typeid"; 
	
	/**数据库表字段:城市编码*/
	public final static String CITYCODE = "citycode"; 
	
	/**数据库表字段:站点、POI、线路id*/
	public final static String ENTITYID = "entityid";
	
	/**数据库表字段:站点、POI、线路名称*/
	public final static String ENTITYNAME = "entityname";
	
	/**数据库表字段:乘车路线*/
	public final static String ENTITYRIDINGROUTE = "entityridingroute";
	
	/**数据库表字段:实体类BUSROUTE*/
	public final static String BUSROUTE = "busroute";
	
	/**数据库表字段:实体类STARTPOIITEM*/
	public final static String STARTPOIITEM = "startpoiitem";
	
	/**数据库表字段:实体类DESTPOIITEM*/
	public final static String DESTPOIITEM = "destpoiitem";
	
	
	/**sid对应的值*/
	public String mSid;
	
	/**城市编码对应的值*/
	public String mCityCode;
	
	/**类型id对应的值*/
	public String mTypeId; 
	
	/**站点、POI、线路id对应的值*/
	public String mEntityId;
	
	/**站点、POI、线路名称对应的值*/
	public String mEntityName;
	
	/**乘车路线对应的值*/
	public String mEntityRidingRoute;
	
	/**BUSROUTE对应的值*/
	public String mBusroute;
	
	/**STARTPOIITEM对应的值*/
	public String mStartPOIItem;
	
	/**DESTPOIITEM对应的值*/
	public String mDestPOIItem;
	
	/***/
	public boolean isRouteSearch = false;
	
	
	@Override
	public Hashtable<String, String> onCreateTable() 
	{
		
		Hashtable<String,String> ohKey = new Hashtable<String,String>();
		ohKey.put(TYPEID, SaveManager.TYPE_INT);
		ohKey.put(CITYCODE, SaveManager.TYPE_TEXT);
		ohKey.put(ENTITYID, SaveManager.TYPE_TEXT);
		ohKey.put(ENTITYNAME, SaveManager.TYPE_TEXT);
		ohKey.put(ENTITYRIDINGROUTE, SaveManager.TYPE_TEXT);
		ohKey.put(BUSROUTE, SaveManager.TYPE_TEXT);
		ohKey.put(STARTPOIITEM, SaveManager.TYPE_TEXT);
		ohKey.put(DESTPOIITEM, SaveManager.TYPE_TEXT);

		return ohKey;
	}

	@Override
	public String onFilterData() 
	{
		String sSql = TYPEID + " = " + mTypeId + " and " + ENTITYID + " = '"+ mEntityId +"' and "+ CITYCODE +" = '"+mCityCode+"'";
		if(isRouteSearch)
		{
			sSql += " and " + ENTITYRIDINGROUTE + " = '"+ mEntityRidingRoute +"' and " + ENTITYNAME + " = '"+ mEntityName +"'";
		}
		return sSql;
	}

	@Override
	public ContentValues onSaveData() throws Exception 
	{
		ContentValues oValues = new ContentValues();
		oValues.put(TYPEID, mTypeId);
		oValues.put(CITYCODE, mCityCode);
		oValues.put(ENTITYID, mEntityId);
		oValues.put(ENTITYNAME, mEntityName);
		oValues.put(ENTITYRIDINGROUTE, mEntityRidingRoute);
		oValues.put(BUSROUTE, mBusroute);
		oValues.put(STARTPOIITEM, mStartPOIItem);
		oValues.put(DESTPOIITEM, mDestPOIItem);
		
		return oValues;
	}

	@Override
	public void onReadData(Cursor oCursor) throws Exception 
	{
		if (oCursor.getColumnIndex("sid") != -1) 
		{
			mSid = oCursor.getString(oCursor.getColumnIndex("sid"));
			mSid = mSid==null ? null :mSid.trim();
		}
		if (oCursor.getColumnIndex(TYPEID) != -1) 
		{
			mTypeId = oCursor.getString(oCursor.getColumnIndex(TYPEID));
			mTypeId = mTypeId==null ? null :mTypeId.trim();
		}
		if (oCursor.getColumnIndex(CITYCODE) != -1) 
		{
			byte bytes[] = oCursor.getBlob(oCursor.getColumnIndex(CITYCODE));
			if (bytes != null) {
				mCityCode = new String(bytes, "UTF-8");
				mCityCode = mCityCode==null ? null :mCityCode.trim();
			}
		}
		if (oCursor.getColumnIndex(ENTITYID) != -1) 
		{
			byte bytes[] = oCursor.getBlob(oCursor.getColumnIndex(ENTITYID));
			if (bytes != null) {
				mEntityId = new String(bytes, "UTF-8");
				mEntityId = mEntityId==null ? null :mEntityId.trim();
			}
		}
		if (oCursor.getColumnIndex(ENTITYNAME) != -1) 
		{
			byte bytes[] = oCursor.getBlob(oCursor.getColumnIndex(ENTITYNAME));
			if (bytes != null) {
				mEntityName = new String(bytes, "UTF-8");
				mEntityName = mEntityName==null ? null :mEntityName.trim();
			}
		}
		if (oCursor.getColumnIndex(ENTITYRIDINGROUTE) != -1) 
		{
			byte bytes[] = oCursor.getBlob(oCursor.getColumnIndex(ENTITYRIDINGROUTE));
			if (bytes != null) {
				mEntityRidingRoute = new String(bytes, "UTF-8");
				mEntityRidingRoute = mEntityRidingRoute==null ? null :mEntityRidingRoute.trim();
			}
		}
		if (oCursor.getColumnIndex(BUSROUTE) != -1) 
		{
			byte bytes[] = oCursor.getBlob(oCursor.getColumnIndex(BUSROUTE));
			if (bytes != null) {
				mBusroute = new String(bytes, "UTF-8");
				mBusroute = mBusroute==null ? null :mBusroute.trim();
			}
		}
		if (oCursor.getColumnIndex(STARTPOIITEM) != -1) 
		{
			byte bytes[] = oCursor.getBlob(oCursor.getColumnIndex(STARTPOIITEM));
			if (bytes != null) {
				mStartPOIItem = new String(bytes, "UTF-8");
				mStartPOIItem = mStartPOIItem==null ? null :mStartPOIItem.trim();
			}
		}
		if (oCursor.getColumnIndex(DESTPOIITEM) != -1) 
		{
			byte bytes[] = oCursor.getBlob(oCursor.getColumnIndex(DESTPOIITEM));
			if (bytes != null) {
				mDestPOIItem = new String(bytes, "UTF-8");
				mDestPOIItem = mDestPOIItem==null ? null :mDestPOIItem.trim();
			}
		}
	}
	
}
