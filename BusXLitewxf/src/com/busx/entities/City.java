package com.busx.entities;

import java.io.Serializable;
import java.util.Hashtable;

import android.content.ContentValues;
import android.database.Cursor;

import com.busx.database.SaveDataListener;
import com.busx.database.SaveManager;

public class City implements Serializable , SaveDataListener
{
	/**
	 * 城市信息
	 */
	private static final long serialVersionUID = 1L;
	public int id;//
	public String adminname = "";//城市名称
	public String admincode = "";//城市编码
	public String adminnamep = "";//城市名称拼音
	public String provincecode = "";//省份编码
	public String servercode = "";//服务编码
	public String lonLats = "";
	public String isCenter = "";
	
	@Override
	public Hashtable<String, String> onCreateTable() 
	{
		
		Hashtable<String,String> ohKey = new Hashtable<String,String>();
		ohKey.put("adminname", SaveManager.TYPE_TEXT);
		ohKey.put("admincode", SaveManager.TYPE_TEXT);
		ohKey.put("adminnamep", SaveManager.TYPE_TEXT);
		ohKey.put("provincecode", SaveManager.TYPE_TEXT);
		ohKey.put("servercode", SaveManager.TYPE_TEXT);
		ohKey.put("lonLats", SaveManager.TYPE_TEXT);
		ohKey.put("isCenter", SaveManager.TYPE_TEXT);
		return ohKey;
	
	}
	@Override
	public String onFilterData() 
	{
		String sql = " adminname = '"+adminname+"' and admincode ='"+admincode+"'";
		return sql;
	}
	@Override
	public ContentValues onSaveData() throws Exception 
	{

		ContentValues oValues = new ContentValues();
		oValues.put("adminname", adminname);
		oValues.put("admincode", admincode);
		oValues.put("adminnamep", adminnamep);
		oValues.put("provincecode", provincecode);
		oValues.put("servercode", servercode);
		oValues.put("lonLats", lonLats);
		oValues.put("isCenter", isCenter);
		return oValues;
	
	}
	@Override
	public void onReadData(Cursor oCursor) throws Exception 
	{
		if (oCursor.getColumnIndex("adminname") != -1) 
		{
			adminname= oCursor.getString(oCursor.getColumnIndex("adminname"));
			adminname = adminname==null ? null :adminname.trim();
		}
		if (oCursor.getColumnIndex("admincode") != -1) 
		{
			admincode= oCursor.getString(oCursor.getColumnIndex("admincode"));
			admincode =admincode ==null ? null :admincode.trim();
		}
		if (oCursor.getColumnIndex("adminnamep") != -1) 
		{
			adminnamep = oCursor.getString(oCursor.getColumnIndex("adminnamep"));
			adminnamep=adminnamep ==null ? null :adminnamep.trim();
		}
		if (oCursor.getColumnIndex("provincecode") != -1) 
		{
			provincecode= oCursor.getString(oCursor.getColumnIndex("provincecode"));
			provincecode= provincecode==null ? null :provincecode.trim();
		}
		if (oCursor.getColumnIndex("lonLats") != -1) 
		{
			lonLats= oCursor.getString(oCursor.getColumnIndex("lonLats"));
			lonLats= lonLats==null ? null :lonLats.trim();
		}
		if (oCursor.getColumnIndex("isCenter") != -1) 
		{
			isCenter= oCursor.getString(oCursor.getColumnIndex("isCenter"));
			isCenter= isCenter==null ? null :isCenter.trim();
		}
	}
}
