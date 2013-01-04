//**************************************************************
//Copyright (C) 2011 Neusoft Co,Ltd. All Rights Reserved.
//-------------------------------------------------------------------
//项目名 ： CnOnFootNavi
//-------------------------------------------------------------------
//版本			作者			完成日			备注
//1.0.0			刘鹏飞		2011/08/25		新規
//**************************************************************
package com.busx.map;

/**
 * Map数据管理
 * @author liupengfei
 *
 */
public class SubWayMapGlobal {

	/** 放大、缩小的宽度比例 */
	public static float scaleWidth = 1;
	/** 放大、缩小的高度比例 */
	public static float scaleHeight = 1;

	/** 放大、缩小的级别 */
	public static int level = 3;

	/** x轴触摸拖动距离 */
	public static float xoff = 0;
	/** y轴触摸拖动距离 */
	public static float yoff = 0;

	/** x轴触摸点位置坐标 */
	public static float touchX = 0;
	/** y轴触摸点位置坐标 */
	public static float touchY = 0;

    /** 点击图片站点时换乘线路ID */
	public static String[] strLineIds = null;
	/** 点击图片站点时换乘线路名称 */
	public static String[] strLineNames = null;

    /** 图片站点ID */
	public static String cRStopID = "";

    /** 图片站点名称 */
	public static String stopName = "";

    /** 是否弹出站点 */
	public static int isPopUp = 0;

    /** 是否是换乘站 */
	public static int isTransfer = 0;
}
