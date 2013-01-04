//**************************************************************
//Copyright (C) 2011 Neusoft Co,Ltd. All Rights Reserved.
//-------------------------------------------------------------------
//项目名 ： CnOnFootNavi
//-------------------------------------------------------------------
//版本			作者			完成日			备注
//1.0.0			zhaoyong	2011/08/31		新規
//**************************************************************

package com.busx.data;

import java.util.Hashtable;

import com.busx.database.SaveDataListener;
import com.busx.database.SaveManager;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 为了保存检索出来的数据
 * @author zhaoyong
 *
 */
public class VersionData implements SaveDataListener
{
	/** 版本编号*/
	public static final String ID = "id";

	/** 版本*/
	public static final String VERSION = "version";

	/** 版本描述*/
	public static final String DESCRIPTION = "description";

	/** 版本编号对应的值*/
	public String m_iId = "";

	/** 版本对应的值*/
	public String m_iVersion = "";

	/** 版本描述对应的值*/
	public String m_sDescription = "";

	@Override
	public Hashtable<String, String> onCreateTable()
	{
		Hashtable<String,String> ohKey = new Hashtable<String,String>();
		ohKey.put(ID, SaveManager.TYPE_INT);
		ohKey.put(VERSION, SaveManager.TYPE_INT);
		ohKey.put(DESCRIPTION, SaveManager.TYPE_TEXT);
		return ohKey;
	}

	@Override
	public String onFilterData()
	{
		String sSql = ID + " = '" + m_iId + "'";
		return sSql;
	}

	@Override
	public void onReadData(Cursor oCursor) throws Exception
	{
		//版本编号
		m_iId = oCursor.getString(oCursor.getColumnIndex(ID));
		//版本
		m_iVersion = oCursor.getString(oCursor.getColumnIndex(VERSION));
		//版本描述
		byte descriptions[]=oCursor.getBlob(oCursor.getColumnIndex(DESCRIPTION));
		if (descriptions != null)
		{
			m_sDescription = new String(descriptions,"UTF-8");
		}
	}

	@Override
	public ContentValues onSaveData() throws Exception
	{
		ContentValues oValues = new ContentValues();
		oValues.put(ID, m_iId);
		oValues.put(VERSION, m_iVersion);
		oValues.put(DESCRIPTION, m_sDescription);
		return oValues;
	}

//	private ContentValues onUpdateData(String versioninfo) throws Exception
//	{
//		ContentValues oValues = new ContentValues();
//		oValues.put(ID, m_iId);
//		oValues.put(VERSION, versioninfo);
//		return oValues;
//	}
}
