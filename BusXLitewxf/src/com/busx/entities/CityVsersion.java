package com.busx.entities;

import java.io.Serializable;
import java.util.Hashtable;

import android.content.ContentValues;
import android.database.Cursor;

import com.busx.database.SaveDataListener;
import com.busx.database.SaveManager;

public class CityVsersion implements Serializable , SaveDataListener
{
	/**
	 * 城市版本信息
	 */
	private static final long serialVersionUID = 1L;
	public String version = "";//数据版本
	public String last_modified = "";//最后修改时间
	@Override
	public Hashtable<String, String> onCreateTable() 
	{
		
		Hashtable<String,String> ohKey = new Hashtable<String,String>();
		ohKey.put("version", SaveManager.TYPE_TEXT);
		ohKey.put("last_modified", SaveManager.TYPE_TEXT);
		return ohKey;
	
	}
	@Override
	public String onFilterData() 
	{
		String sql = "1=1";
		return sql;
	}
	@Override
	public ContentValues onSaveData() throws Exception 
	{

		ContentValues oValues = new ContentValues();
		oValues.put("version", version);
		oValues.put("last_modified", last_modified);
		return oValues;
	
	}
	@Override
	public void onReadData(Cursor oCursor) throws Exception 
	{
		if (oCursor.getColumnIndex("version") != -1) 
		{
			version= oCursor.getString(oCursor.getColumnIndex("version"));
			version = version==null ? null :version.trim();
		}
		if (oCursor.getColumnIndex("last_modified") != -1) 
		{
			last_modified = oCursor.getString(oCursor.getColumnIndex("last_modified"));
			last_modified = last_modified==null ? null :last_modified.trim();
		}
	}
}
