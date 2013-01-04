package com.busx.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.busx.database.SaveDataListener;
import com.busx.database.SaveManager;

public class Province implements Serializable , SaveDataListener
{
	/**
	 * 城市信息
	 */
	private static final long serialVersionUID = 1L;
	public int id;//
	public String provincename = "";//所属省份
	public String provincecode = "";//省份编码
	public String provincenamep = "";//省份名称拼音
	
	public List<City> cities = new ArrayList<City>();
	@Override
	public Hashtable<String, String> onCreateTable() 
	{
		
		Hashtable<String,String> ohKey = new Hashtable<String,String>();
		ohKey.put("provincename", SaveManager.TYPE_TEXT);
		ohKey.put("provincecode", SaveManager.TYPE_TEXT);
		ohKey.put("provincenamep", SaveManager.TYPE_TEXT);
		return ohKey;
	
	}
	@Override
	public String onFilterData() 
	{
		String sql = " provincename = '"+provincename+"' and provincecode ='"+provincecode+"'";
		return sql;
	}
	@Override
	public ContentValues onSaveData() throws Exception 
	{

		ContentValues oValues = new ContentValues();
		oValues.put("provincename", provincename);
		oValues.put("provincecode", provincecode);
		oValues.put("provincenamep", provincenamep);
		return oValues;
	
	}
	@Override
	public void onReadData(Cursor oCursor) throws Exception 
	{
		if (oCursor.getColumnIndex("provincename") != -1) 
		{
			provincename= oCursor.getString(oCursor.getColumnIndex("provincename"));
			provincename= provincename==null ? null :provincename.trim();
		}
		if (oCursor.getColumnIndex("provincecode") != -1) 
		{
			provincecode= oCursor.getString(oCursor.getColumnIndex("provincecode"));
			provincecode= provincecode==null ? null :provincecode.trim();
		}
		if (oCursor.getColumnIndex("provincenamep") != -1) 
		{
			provincenamep= oCursor.getString(oCursor.getColumnIndex("provincenamep"));
			provincenamep=provincenamep ==null ? null :provincenamep.trim();
		}
	}
}
