//**************************************************************
//Copyright (C) 2011 Neusoft Co,Ltd. All Rights Reserved.
//-------------------------------------------------------------------
//项目名 ： CnOnFootNavi
//-------------------------------------------------------------------
//版本			作者			完成日			备注
//1.0.0			liupengfei	2011/08/31		新規
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
 *
 */
public class SubwayPosData implements SaveDataListener
{
	/* 地铁站点编号*/
	private static final String CRSTOPID = "CRSTOPID";

	/* 中文名称*/
	private static final String NAMEC = "NAMEC";

	/* X坐标*/
	private static final String X = "X";

	/* Y坐标*/
	private static final String Y = "Y";

	/** 是否是换乘站*/
	public static final String TRANSFER = "TRANSFER";

	/** 地铁站点编号对应的值*/
	public String m_iCRStopID = "";

	/** 中文名称对应的值*/
	public String m_sNamec = "";

	/** X坐标对应的值*/
	public float m_iX = 0;

	/** Y坐标对应的值*/
	public float m_iY = 0;

	/** 是否是换乘站对应的值*/
	public int m_iTransfer = 0;

	@Override
	public Hashtable<String, String> onCreateTable()
	{
		Hashtable<String,String> ohKey = new Hashtable<String,String>();
		ohKey.put(CRSTOPID, SaveManager.TYPE_INT);
		ohKey.put(NAMEC, SaveManager.TYPE_TEXT);
		ohKey.put(X, SaveManager.TYPE_INT);
		ohKey.put(Y, SaveManager.TYPE_INT);
		ohKey.put(TRANSFER, SaveManager.TYPE_INT);
		return ohKey;
	}

	@Override
	public String onFilterData()
	{
		String sSql = CRSTOPID + " = " + m_iCRStopID;
		return sSql;
	}

	@Override
	public void onReadData(Cursor oCursor) throws Exception
	{
		//地铁站点编号
		m_iCRStopID = oCursor.getString(oCursor.getColumnIndex(CRSTOPID));
		//中文名称
		byte namecs[]=oCursor.getBlob(oCursor.getColumnIndex(NAMEC));
		if (namecs != null)
		{
			m_sNamec = new String(namecs,"UTF-8");
		}
		//X坐标
		m_iX = oCursor.getFloat(oCursor.getColumnIndex(X));
		//Y坐标
		m_iY = oCursor.getFloat(oCursor.getColumnIndex(Y));
		//是否是换乘站
		m_iTransfer = oCursor.getInt(oCursor.getColumnIndex(TRANSFER));
	}

	@Override
	public ContentValues onSaveData() throws Exception
	{
		ContentValues oValues = new ContentValues();
		oValues.put(CRSTOPID, m_iCRStopID);
		oValues.put(NAMEC, m_sNamec);
		oValues.put(X, m_iX);
		oValues.put(Y, m_iY);
		oValues.put(TRANSFER, m_iTransfer);
		return oValues;
	}
}
