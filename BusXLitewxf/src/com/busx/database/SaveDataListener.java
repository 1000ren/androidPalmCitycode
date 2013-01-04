//**************************************************************
//Copyright (C) 2011 Neusoft Co,Ltd. All Rights Reserved.
//-------------------------------------------------------------------
//项目名 ： CnOnFootNavi
//-------------------------------------------------------------------
//版本			作者			完成日			备注
//1.0.0			yangyang	2011/07/07		新規
//**************************************************************
package com.busx.database;

import java.util.Hashtable;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 本地数据保存用的接口
 * @author yang_yang
 *
 *
 */
public interface SaveDataListener
{
	/**
	 * 表创建时使用的方法
	 */
	public Hashtable<String,String> onCreateTable();

	/** 检索用的ｗｈｅｒｅ条件部分
	public String onFilterData();

	/**
	 * 数据保存用的方法
	 * @throws Exception 异常发生时，返回异常内容
	 */
	public ContentValues onSaveData()throws Exception;

	/**
	 * 数据读取的方法
	 */
	public void onReadData(Cursor oCursor)throws Exception;
}