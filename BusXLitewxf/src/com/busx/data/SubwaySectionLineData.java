//**************************************************************
//Copyright (C) 2011 Neusoft Co,Ltd. All Rights Reserved.
//-------------------------------------------------------------------
//项目名 ： CnOnFootNavi
//-------------------------------------------------------------------
//版本			作者			完成日			备注
//1.0.0			刘鹏飞		2011/08/25		新規
//**************************************************************
package com.busx.data;

import java.util.Hashtable;

import com.busx.database.SaveDataListener;
import com.busx.database.SaveManager;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 为了保存检索出来的数据
 * @author liupengfei
 */
public class SubwaySectionLineData implements SaveDataListener
{
	/* 地铁线路编号*/
	private static final String CRLINEID = "CRLINEID";

	/* 线路方向*/
	private static final String LINEDIRE = "LINEDIRE";

	/* 区间信息*/
	private static final String SECTIONINFO = "SECTIONINFO";

	/** 地铁线路编号对应的值*/
	public String m_iCRLineID = "";

	/** 线路方向对应的值*/
	public String m_iLinedire = "";

	/** 区间信息对应的值*/
	public String m_sSectionInfo = "";

	@Override
	public Hashtable<String, String> onCreateTable()
	{
		Hashtable<String,String> ohKey = new Hashtable<String,String>();
		ohKey.put(CRLINEID, SaveManager.TYPE_INT);
		ohKey.put(LINEDIRE, SaveManager.TYPE_INT);
		ohKey.put(SECTIONINFO, SaveManager.TYPE_TEXT);
		return ohKey;
	}

	@Override
	public String onFilterData()
	{
		String sSql = CRLINEID + " = '" + m_iCRLineID + "'";
		return sSql;
	}

	@Override
	public void onReadData(Cursor oCursor) throws Exception
	{
		if (oCursor.getColumnIndex(CRLINEID) != -1) {
			// 地铁线路编号
			m_iCRLineID = oCursor.getString(oCursor.getColumnIndex(CRLINEID));
		}
		if (oCursor.getColumnIndex(LINEDIRE) != -1) {
			// 线路方向
			m_iLinedire = oCursor.getString(oCursor.getColumnIndex(LINEDIRE));
		}
		if (oCursor.getColumnIndex(SECTIONINFO) != -1) {
			// 区间信息
			byte namecs[] = oCursor.getBlob(oCursor.getColumnIndex(SECTIONINFO));
			if (namecs != null) {
				m_sSectionInfo = new String(namecs, "UTF-8");
			}
		}
	}

	@Override
	public ContentValues onSaveData() throws Exception
	{
		ContentValues oValues = new ContentValues();
		oValues.put(CRLINEID, m_iCRLineID);
		oValues.put(LINEDIRE, m_iLinedire);
		oValues.put(SECTIONINFO, m_sSectionInfo);
		return oValues;
	}
}
