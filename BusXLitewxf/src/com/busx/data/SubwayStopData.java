//**************************************************************
//Copyright (C) 2011 Neusoft Co,Ltd. All Rights Reserved.
//-------------------------------------------------------------------
//项目名 ： CnOnFootNavi
//-------------------------------------------------------------------
//版本			作者			完成日			备注
//1.0.0			liupengfei	2011/08/16		新規
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
public class SubwayStopData implements SaveDataListener
{
    /* 地铁站点编号*/
	private static final String CRSTOPID = "CRSTOPID";

    /* 中文名称*/
	private static final String NAMEC = "NAMEC";

    /* 车门开启情报*/
	private static final String DOORSWITCH = "DOORSWITCH";

    /* 站点介绍*/
	private static final String STOPINTRODUCTION = "STOPINTRODUCTION";

    /* 运行时间*/
	private static final String RUNTIME = "RUNTIME";

    /* 首班车发车时间*/
	private static final String TIMEF = "TIMEF";

    /* 末班车发车时间*/
	private static final String TIMEL = "TIMEL";

    /* 反向首班车发车时间*/
	private static final String TIMEFREVERSE = "TIMEFREVERSE";

    /* 反向末班车发车时间*/
	private static final String TIMELREVERSE = "TIMELREVERSE";

    /* 出口编号*/
	private static final String EXITID = "EXIT";

    /* 厕所标识*/
	private static final String WCFLAG = "WCFLAG";

    /* 厕所标识*/
	private static final String WKTFLAG = "WKTFLAG";

    /* 厕所标识*/
	private static final String VCFLAG = "VCFLAG";

    /* 内部平面图*/
	private static final String PLAN = "PLAN";

    /* 经度*/
	private static final String LONGITUDE = "LONGITUDE";

    /* 纬度*/
	private static final String LATITUDE = "LATITUDE";

    /** 地铁站点编号对应的值*/
    public String m_iCrstopid = "";

    /** 中文名称对应的值*/
    public String m_sNamec = "";

    /** 车门开启情报对应的值*/
    public String m_sDoorswitch = "";

    /** 站点介绍对应的值*/
    public String m_sStopintroduction = "";

    /** 运行时间对应的值*/
    public String m_sRuntime = "";

    /** 首班车发车时间对应的值*/
    public String m_sTimef = "";

    /** 末班车发车时间对应的值*/
    public String m_sTimel = "";

    /** 反向首班车发车时间对应的值*/
    public String m_sTimefReverse = "";

    /** 反向末班车发车时间对应的值*/
    public String m_sTimelReverse = "";

    /** 出口编号对应的值*/
    public String m_iExitID = "";

    /** 出口信息对应的值*/
    public String m_sExitInfo = "";

    /** 厕所标识对应的值*/
    public String m_iWcFlag = "";

    /** 厕所标识对应的值*/
    public String m_iWktFlag = "";

    /** 厕所标识对应的值*/
    public String m_iVcFlag = "";

    /** 内部平面图对应的值*/
    public String m_sPlan = "";

    /** 经度对应的值*/
    public String m_iLongitude = "";

    /** 纬度对应的值*/
    public String m_iLatitude = "";

    @Override
    public Hashtable<String, String> onCreateTable()
    {
        Hashtable<String,String> ohKey = new Hashtable<String,String>();
        ohKey.put(CRSTOPID, SaveManager.TYPE_INT);
        ohKey.put(NAMEC, SaveManager.TYPE_TEXT);
        ohKey.put(DOORSWITCH, SaveManager.TYPE_TEXT);
        ohKey.put(STOPINTRODUCTION, SaveManager.TYPE_TEXT);
        ohKey.put(RUNTIME, SaveManager.TYPE_INT);
        ohKey.put(EXITID, SaveManager.TYPE_INT);
        ohKey.put(TIMEF, SaveManager.TYPE_TEXT);
        ohKey.put(TIMEL, SaveManager.TYPE_TEXT);
        ohKey.put(TIMEFREVERSE, SaveManager.TYPE_TEXT);
        ohKey.put(TIMELREVERSE, SaveManager.TYPE_TEXT);
        ohKey.put(PLAN, SaveManager.TYPE_TEXT);
        ohKey.put(WCFLAG, SaveManager.TYPE_INT);
        ohKey.put(WKTFLAG, SaveManager.TYPE_INT);
        ohKey.put(VCFLAG, SaveManager.TYPE_INT);
        ohKey.put(LONGITUDE, SaveManager.TYPE_INT);
        ohKey.put(LATITUDE, SaveManager.TYPE_INT);
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
        if(oCursor.getColumnIndex(CRSTOPID)!=-1)
        {
            //地铁站点编号
            m_iCrstopid = oCursor.getString(oCursor.getColumnIndex(CRSTOPID));
        }
        //中文名称
        byte namecs[]=oCursor.getBlob(oCursor.getColumnIndex(NAMEC));
        if (namecs != null)
        {
            m_sNamec = new String(namecs,"UTF-8");
        }
        if(oCursor.getColumnIndex(DOORSWITCH)!=-1)
        {
            //车门开启情报
            byte doorswitchs[]=oCursor.getBlob(oCursor.getColumnIndex(DOORSWITCH));
            if (doorswitchs != null)
            {
                m_sDoorswitch = new String(doorswitchs,"UTF-8");
            }
        }
        if(oCursor.getColumnIndex(STOPINTRODUCTION) !=-1)
        {
            //站点介绍
            byte stopintroduction[]=oCursor.getBlob(oCursor.getColumnIndex(STOPINTRODUCTION));
            if (stopintroduction != null)
            {
                m_sStopintroduction = new String(stopintroduction,"UTF-8");
            }
        }
        if(oCursor.getColumnIndex(RUNTIME) !=-1)
        {
            //运行时间
            m_sRuntime = oCursor.getString(oCursor.getColumnIndex(RUNTIME));
        }
        if(oCursor.getColumnIndex(EXITID) !=-1)
        {
            //出口 编号
            m_iExitID = oCursor.getString(oCursor.getColumnIndex(EXITID));
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
        if(oCursor.getColumnIndex(PLAN) !=-1)
        {
            //内部平面图
            m_sPlan = oCursor.getString(oCursor.getColumnIndex(PLAN));
        }
        if(oCursor.getColumnIndex(WCFLAG) !=-1)
        {
            //厕所标识
            m_iWcFlag = oCursor.getString(oCursor.getColumnIndex(WCFLAG));
        }
        if(oCursor.getColumnIndex(WKTFLAG) !=-1)
        {
            //厕所标识
            m_iWktFlag = oCursor.getString(oCursor.getColumnIndex(WKTFLAG));
        }
        if(oCursor.getColumnIndex(VCFLAG) !=-1)
        {
            //厕所标识
            m_iVcFlag = oCursor.getString(oCursor.getColumnIndex(VCFLAG));
        }
        if(oCursor.getColumnIndex(LONGITUDE) !=-1)
        {
            //经度
            m_iLongitude = oCursor.getString(oCursor.getColumnIndex(LONGITUDE));
        }
        if(oCursor.getColumnIndex(LATITUDE) !=-1)
        {
            //纬度
            m_iLatitude = oCursor.getString(oCursor.getColumnIndex(LATITUDE));
        }
    }

    @Override
    public ContentValues onSaveData() throws Exception
    {
        ContentValues oValues = new ContentValues();
        oValues.put(CRSTOPID, m_iCrstopid);
        oValues.put(NAMEC, m_sNamec);
        oValues.put(DOORSWITCH, m_sDoorswitch);
        oValues.put(STOPINTRODUCTION, m_sStopintroduction);
        oValues.put(RUNTIME, m_sRuntime);
        oValues.put(EXITID, m_iExitID);
        oValues.put(TIMEF, m_sTimef);
        oValues.put(TIMEL, m_sTimel);
        oValues.put(TIMEFREVERSE, m_sTimefReverse);
        oValues.put(TIMELREVERSE, m_sTimelReverse);
        oValues.put(PLAN, m_sPlan);
        oValues.put(WCFLAG, m_iWcFlag);
        oValues.put(WKTFLAG, m_iWktFlag);
        oValues.put(VCFLAG, m_iVcFlag);
        oValues.put(LONGITUDE, m_iLongitude);
        oValues.put(LATITUDE, m_iLatitude);
        return oValues;
    }
}
