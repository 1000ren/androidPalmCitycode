//**************************************************************
//Copyright (C) 2011 Neusoft Co,Ltd. All Rights Reserved.
//-------------------------------------------------------------------
//项目名 ： CnOnFootNavi
//-------------------------------------------------------------------
//版本			作者			完成日			备注
//1.0.0			liupengfei	2011/07/18		新規
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
public class SubwayLinePData implements SaveDataListener
{
	/* 地铁线路编号*/
	private static final String CRLINEID = "CRLINEID";

	/* 地铁站点编号*/
	private static final String CRSTOPID = "CRSTOPID";

	/* 站点序号*/
	private static final String SERIALNUM = "SERIALNUM";

	/* 运行时间*/
	private static final String RUNTIME = "RUNTIME";

	/* 反向运行时间*/
	private static final String RUNTIMEREVERSE = "RUNTIMEREVERSE";

	/* 首班车发车时间*/
	private static final String TIMEF = "TIMEF";

	/* 末班车发车时间*/
	private static final String TIMEL = "TIMEL";

	/* 反向首班车发车时间*/
	private static final String TIMEFREVERSE = "TIMEFREVERSE";

	/* 反向末班车发车时间*/
	private static final String TIMELREVERSE = "TIMELREVERSE";

	/** 地铁线路编号对应的值*/
	public String m_iCRLineID = "";

	/** 地铁站点编号对应的值*/
	public String m_iCrstopID = "";

	/** 地铁站点序号对应的值 */
	public String m_iSerialnum = "";

	/** 运行时间对应的值*/
	public String m_sRumTime = "";

	/** 反向运行时间对应的值*/
	public String m_sRumTimeReverse = "";

	/** 首班车发车时间对应的值*/
	public String m_sTimef = "";

	/** 末班车发车时间对应的值*/
	public String m_sTimel = "";

	/** 反向首班车发车时间对应的值*/
	public String m_sTimefReverse = "";

	/** 反向末班车发车时间对应的值*/
	public String m_sTimelReverse = "";

	@Override
	public Hashtable<String, String> onCreateTable()
	{
		Hashtable<String,String> ohKey = new Hashtable<String,String>();
		ohKey.put(CRLINEID, SaveManager.TYPE_INT);
		ohKey.put(CRSTOPID, SaveManager.TYPE_INT);
		ohKey.put(SERIALNUM, SaveManager.TYPE_INT);
		ohKey.put(RUNTIME, SaveManager.TYPE_TEXT);
		ohKey.put(RUNTIMEREVERSE, SaveManager.TYPE_TEXT);
		ohKey.put(TIMEF, SaveManager.TYPE_TEXT);
		ohKey.put(TIMEL, SaveManager.TYPE_TEXT);
		ohKey.put(TIMEFREVERSE, SaveManager.TYPE_TEXT);
		ohKey.put(TIMELREVERSE, SaveManager.TYPE_TEXT);
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
		if (oCursor.getColumnIndex(CRSTOPID) != -1) {
			// 地铁站点编号
			m_iCrstopID = oCursor.getString(oCursor.getColumnIndex(CRSTOPID));
		}

		if (oCursor.getColumnIndex(SERIALNUM) != -1) {
			m_iSerialnum = oCursor.getString(oCursor.getColumnIndex(SERIALNUM));
		}

		if (oCursor.getColumnIndex(RUNTIME) != -1) {
			// 运行时间
			byte runtimes[] = oCursor.getBlob(oCursor.getColumnIndex(RUNTIME));
			if (runtimes != null) {
				m_sRumTime = new String(runtimes, "UTF-8");
			}
		}
		if (oCursor.getColumnIndex(RUNTIMEREVERSE) != -1) {
			// 反向运行时间
			byte runtimeReverses[] = oCursor.getBlob(oCursor.getColumnIndex(RUNTIMEREVERSE));
			if (runtimeReverses != null) {
				m_sRumTimeReverse = new String(runtimeReverses, "UTF-8");
			}
		}
		if (oCursor.getColumnIndex(TIMEF) != -1) {
			// 首班车发车时间
			byte timefs[] = oCursor.getBlob(oCursor.getColumnIndex(TIMEF));
			if (timefs != null) {
				m_sTimef = new String(timefs, "UTF-8");
			}
		}
		if (oCursor.getColumnIndex(TIMEL) != -1) {
			// 末班车发车时间
			byte timels[] = oCursor.getBlob(oCursor.getColumnIndex(TIMEL));
			if (timels != null) {
				m_sTimel = new String(timels, "UTF-8");
			}
		}
		if (oCursor.getColumnIndex(TIMEFREVERSE) != -1) {
			// 反向首班车发车时间
			byte timefReverse[] = oCursor.getBlob(oCursor.getColumnIndex(TIMEFREVERSE));
			if (timefReverse != null) {
				m_sTimefReverse = new String(timefReverse, "UTF-8");
			}
		}
		if (oCursor.getColumnIndex(TIMELREVERSE) != -1) {
			// 反向末班车发车时间
			byte timelReverse[] = oCursor.getBlob(oCursor.getColumnIndex(TIMELREVERSE));
			if (timelReverse != null) {
				m_sTimelReverse = new String(timelReverse, "UTF-8");
			}
		}
	}

	@Override
	public ContentValues onSaveData() throws Exception
	{
		ContentValues oValues = new ContentValues();
		oValues.put(CRLINEID, m_iCRLineID);
		oValues.put(CRSTOPID, m_iCrstopID);
		oValues.put(SERIALNUM, m_iSerialnum);
		oValues.put(RUNTIME, m_sRumTime);
		oValues.put(RUNTIMEREVERSE, m_sRumTimeReverse);
		oValues.put(TIMEF, m_sTimef);
		oValues.put(TIMEL, m_sTimel);
		oValues.put(TIMEFREVERSE, m_sTimefReverse);
		oValues.put(TIMELREVERSE, m_sTimelReverse);
		return oValues;
	}
}