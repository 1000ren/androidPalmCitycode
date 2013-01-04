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
public class SubwayLineData implements SaveDataListener
{
	/* 地铁线路编号*/
	private static final String CRLINEID = "CRLINEID";

	/* 线路方向*/
	private static final String LINEDIRE = "LINEDIRE";

	/* 中文名称*/
	private static final String NAMEC = "NAMEC";

	/* 里程（公里）*/
	private static final String LENGTH = "LENGTH";

	/* 首班车发车时间*/
	private static final String TIMEF = "TIMEF";

	/* 末班车发车时间*/
	private static final String TIMEL = "TIMEL";

	/* 发车时间间隔（分钟）*/
	private static final String INTERVAL = "INTERVAL";

	/* 全程时间（分钟）*/
	private static final String HOURS = "HOURS";

	/* 全程价（元）*/
	private static final String APRICE = "APRICE";

	/* 线路介绍*/
	private static final String INTRODUCTION = "INTRODUCTION";

	/* 经度*/
	private static final String LONGITUDE = "LONGITUDE";

	/* 纬度*/
	private static final String LATITUDE = "LATITUDE";

	/** 地铁线路编号对应的值*/
	public String m_iCRLineID = "";

	/** 线路方向对应的值*/
	public String m_iLinedire = "";

	/** 中文名称对应的值*/
	public String m_sNamec = "";

	/** 里程（公里）对应的值*/
	public String m_iLength = "";

	/** 首班车发车时间对应的值*/
	public String m_sTimef = "";

	/** 末班车发车时间对应的值*/
	public String m_sTimel = "";

	/** 发车时间间隔（分钟）对应的值*/
	public String m_sInterval = "";

	/** 全程时间（分钟）对应的值*/
	public String m_iHours = "";

	/** 全程价（元）对应的值*/
	public String m_iAprice = "";

	/** 线路介绍对应的值*/
	public String m_sIntroduction = "";

	/** 经度对应的值*/
	public String m_iLongitude = "";

	/** 纬度对应的值*/
	public String m_iLatitude = "";

	@Override
	public Hashtable<String, String> onCreateTable()
	{
		Hashtable<String,String> ohKey = new Hashtable<String,String>();
		ohKey.put(CRLINEID, SaveManager.TYPE_INT);
		ohKey.put(LINEDIRE, SaveManager.TYPE_INT);
		ohKey.put(NAMEC, SaveManager.TYPE_TEXT);
		ohKey.put(LENGTH, SaveManager.TYPE_INT);
		ohKey.put(TIMEF, SaveManager.TYPE_TEXT);
		ohKey.put(TIMEL, SaveManager.TYPE_TEXT);
		ohKey.put(INTERVAL, SaveManager.TYPE_TEXT);
		ohKey.put(HOURS, SaveManager.TYPE_INT);
		ohKey.put(APRICE, SaveManager.TYPE_TEXT);
		ohKey.put(INTRODUCTION, SaveManager.TYPE_TEXT);
		ohKey.put(LONGITUDE, SaveManager.TYPE_INT);
		ohKey.put(LATITUDE, SaveManager.TYPE_INT);
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
		if (oCursor.getColumnIndex(NAMEC) != -1) {
			// 中文名称
			byte namecs[] = oCursor.getBlob(oCursor.getColumnIndex(NAMEC));
			if (namecs != null) {
				// m_sNamec = new String(namecs,"gb2312");
				m_sNamec = new String(namecs, "UTF-8");
			}
		}
		if (oCursor.getColumnIndex(LENGTH) != -1) {
			// 里程（公里）
			m_iLength = oCursor.getString(oCursor.getColumnIndex(LENGTH));
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
				// m_sTimel = new String(timels,"gb2312");
				m_sTimel = new String(timels, "UTF-8");
			}
		}
		if (oCursor.getColumnIndex(INTERVAL) != -1) {
			// 发车时间间隔（分钟）
			byte intervals[] = oCursor
					.getBlob(oCursor.getColumnIndex(INTERVAL));
			if (intervals != null) {
				// m_sInterval = new String(intervals,"gb2312");
				m_sInterval = new String(intervals, "UTF-8");
			}
		}
		if (oCursor.getColumnIndex(HOURS) != -1) {
			// 全程时间（分钟）
			m_iHours = oCursor.getString(oCursor.getColumnIndex(HOURS));
		}
		if (oCursor.getColumnIndex(APRICE) != -1) {
			// 全程价（元）
			m_iAprice = oCursor.getString(oCursor.getColumnIndex(APRICE));
		}
		if (oCursor.getColumnIndex(INTRODUCTION) != -1) {
			// 线路介绍
			byte introductions[] = oCursor.getBlob(oCursor
					.getColumnIndex(INTRODUCTION));
			if (introductions != null) {
				// m_sIntroduction = new String(introductions,"gb2312");
				m_sIntroduction = new String(introductions, "UTF-8");
			}
		}
		if (oCursor.getColumnIndex(LONGITUDE) != -1) {
			m_iLongitude = oCursor.getString(oCursor.getColumnIndex(LONGITUDE));
		}
		if (oCursor.getColumnIndex(LATITUDE) != -1) {
			m_iLatitude = oCursor.getString(oCursor.getColumnIndex(LATITUDE));
		}
	}

	@Override
	public ContentValues onSaveData() throws Exception
	{
		ContentValues oValues = new ContentValues();
		oValues.put(CRLINEID, m_iCRLineID);
		oValues.put(LINEDIRE, m_iLinedire);
		oValues.put(NAMEC, m_sNamec);
		oValues.put(LENGTH, m_iLength);
		oValues.put(TIMEF, m_sTimef);
		oValues.put(TIMEL, m_sTimel);
		oValues.put(INTERVAL, m_sInterval);
		oValues.put(HOURS, m_iHours);
		oValues.put(APRICE, m_iAprice);
		oValues.put(INTRODUCTION, m_sIntroduction);
		oValues.put(LONGITUDE, m_iLongitude);
		oValues.put(LATITUDE, m_iLatitude);
		return oValues;
	}
}