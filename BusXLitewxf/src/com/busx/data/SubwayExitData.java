//**************************************************************
//Copyright (C) 2011 Neusoft Co,Ltd. All Rights Reserved.
//-------------------------------------------------------------------
//项目名 ： CnOnFootNavi
//-------------------------------------------------------------------
//版本			作者			完成日			备注
//1.0.0			liupengfei	2011/08/17		新規
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
public class SubwayExitData implements SaveDataListener
{
	/** 地铁站点编号*/
    private static final String CRSTOPID = "crstopid";

	/** 出口编号*/
    private static final String EXITID = "serialnum";

	/** 出口信息*/
    private static final String EXITINFO = "info";

	/** 地铁站点对应的值*/
	public String m_iCrstopid = "";

	/** 出口编号对应的值*/
	public String m_iExitid = "";

	/** 出口信息对应的值*/
	public String m_sExitinfo = "";

	@Override
	public Hashtable<String, String> onCreateTable()
	{
		Hashtable<String,String> ohKey = new Hashtable<String,String>();
		ohKey.put(CRSTOPID, SaveManager.TYPE_INT);
		ohKey.put(EXITID, SaveManager.TYPE_INT);
		ohKey.put(EXITINFO, SaveManager.TYPE_TEXT);

		return ohKey;
	}

	@Override
	public String onFilterData()
	{
		String sSql = CRSTOPID + " = '" + m_iCrstopid + "'";
		return sSql;
	}

	@Override
	public void onReadData(Cursor oCursor) throws Exception
	{
		//地铁站点编号
		m_iCrstopid = oCursor.getString(oCursor.getColumnIndex(CRSTOPID));
		//出口编号
		m_iExitid = oCursor.getString(oCursor.getColumnIndex(EXITID));
		//出口信息
		byte exitinfo[]=oCursor.getBlob(oCursor.getColumnIndex(EXITINFO));
		if (exitinfo != null)
		{
			m_sExitinfo = new String(exitinfo,"UTF-8");
		}
	}

	@Override
	public ContentValues onSaveData() throws Exception
	{
		ContentValues oValues = new ContentValues();
		oValues.put(CRSTOPID, m_iCrstopid);
		oValues.put(EXITID, m_iExitid);
		oValues.put(EXITINFO, m_sExitinfo);
		return oValues;
	}
}
