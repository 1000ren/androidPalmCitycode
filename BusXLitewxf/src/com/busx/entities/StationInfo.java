//**************************************************************
//Copyright (C) 2011 Neusoft Co,Ltd. All Rights Reserved.
//-------------------------------------------------------------------
//项目名 ： CnOnFootNavi
//-------------------------------------------------------------------
//版本			作者			完成日			备注
//1.0.0			刘鹏飞		2011/08/30		新規
//**************************************************************
package com.busx.entities;

import java.io.Serializable;

/**
 * 地铁图绘制数据类
 * @author liupengfei
 *
 */
public class StationInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**线路名*/
    public String station_name;

    /**线路名坐标*/
    public float station_x,station_y;

    /**线路名颜色*/
    public int name_r,name_g,name_b;

    /**线路颜色*/
    public int line_r,line_g,line_b;

    /**描画点个数*/
    public int point_num;

    /**描画点信息*/
    public PointInfo[] points;

    /**线路中心点坐标*/
    public float lineCentre_x,lineCentre_y;

    /**站点信息类*/
    public class PointInfo{
    	/**是否是站点*/
        public boolean bStation;

        /**站点名称*/
        public String sName;
        /**
         * 站点名坐标
         */
        public float name_x,name_y;
        /** 站点坐标 */
        public float point_x,point_y;
        /** 是否是换乘 */
        public boolean bTransfer;
        /** 放大、缩小级别（1、2、3、4、5级）*/
        public int[] level = new int[5];
    }
}
