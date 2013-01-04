//**************************************************************
//Copyright (C) 2011 Neusoft Co,Ltd. All Rights Reserved.
//-------------------------------------------------------------------
//项目名 ： CnOnFootNavi
//-------------------------------------------------------------------
//版本			作者			完成日			备注
//1.0.0			liupengfei	2011/08/18		新規
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
public class SubwayStopLData implements SaveDataListener
{
	/* 地铁站点编号*/
	private static final String CRSTOPID = "CRSTOPID";

	/* 中文名称*/
	private static final String NAMEC = "NAMEC";

	/* 地铁线路编号*/
	private static final String CRLINEID = "CRLINEID";

	/* 是否为换乘车站*/
	private static final String TRANSFER = "TRANSFER";

	/* 线路运行方向*/
	private static final String DOUBLEWAY = "LINEDIRE";

	/** 地铁站点编号对应的值*/
	public String m_iCrstopid = "";

	/** 中文名称对应的值*/
	public String m_sNamec = "";

	/** 地铁线路编号对应的值*/
	public String m_sCrlineid = "";

	/** 是否为换乘车站对应的值*/
	public String m_sTransfer = "";

	/** 线路运行方向对应的值*/
	public String m_sBack = "";

	@Override
	public Hashtable<String, String> onCreateTable()
	{
		Hashtable<String,String> ohKey = new Hashtable<String,String>();
		ohKey.put(CRSTOPID, SaveManager.TYPE_INT);
		ohKey.put(NAMEC, SaveManager.TYPE_TEXT);
		ohKey.put(CRLINEID, SaveManager.TYPE_INT);
		ohKey.put(TRANSFER, SaveManager.TYPE_INT);
		return ohKey;
	}

	@Override
	public String onFilterData()
	{
		String sSql = CRSTOPID + " = " + m_iCrstopid;
		return sSql;
	}

	@Override
	public void onReadData(Cursor oCursor) throws Exception
	{
		//地铁站点编号
		m_iCrstopid = oCursor.getString(oCursor.getColumnIndex(CRSTOPID));
		//中文名称
		byte namecs[]=oCursor.getBlob(oCursor.getColumnIndex(NAMEC));
		if (namecs != null)
		{
			m_sNamec = new String(namecs,"UTF-8");
		}
		//地铁线路编号
		m_sCrlineid = oCursor.getString(oCursor.getColumnIndex(CRLINEID));
		//是否为换乘车站
		m_sTransfer = oCursor.getString(oCursor.getColumnIndex(TRANSFER));
		//线路运行方向
		m_sBack = oCursor.getString(oCursor.getColumnIndex(DOUBLEWAY));
	}

	@Override
	public ContentValues onSaveData() throws Exception
	{
		ContentValues oValues = new ContentValues();
		oValues.put(CRSTOPID, m_iCrstopid);
		oValues.put(NAMEC, m_sNamec);
		oValues.put(CRLINEID, m_sCrlineid);
		oValues.put(TRANSFER, m_sTransfer);
		return oValues;
	}
}