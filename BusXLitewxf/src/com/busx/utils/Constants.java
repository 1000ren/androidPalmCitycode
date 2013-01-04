package com.busx.utils;

import java.util.Map;


public class Constants
{
//	 默认地图中心位置及比例尺
//	public static final double MAPCENTER_DEFAULT_LON = 116.353546;
//	public static final double MAPCENTER_DEFAULT_LAT = 39.979115;
	public static final int MAPSCALE_DEFAULT = 12;
	public static final String NEARBYSEARCH_DEFAULT_SCALE = "500米";
	
	public static final String MAPPOS_POI_DEFAULT_ID = "-1";
	public static final String MAPPOS_POI_CUR_NAME = "我的位置";
	public static final String MAPPOS_POI_DEFAULT_NAME = "地图上的点";

	public static final int APP_EXIT = 0;
	// MAPSTATE
	// 地图显示模式
	public static final int MAPMODE_DEFAULT = 0;
	// POI
	public static final int MAPMODE_VIEWPOI = 1;
	// POIS
	public static final int MAPMODE_VIEWPOIS = 2;
	// BUS STATION
	public static final int MAPMODE_VIEWSTATION = 4;
	// BUS STATIONS
	public static final int MAPMODE_VIEWSTATIONS = 5;
	// BUS LINE
	public static final int MAPMODE_VIEWBUSLINE = 7;
	//
	public static final int MAPMODE_OTHERCITYCENTER = 8;
	// 公交换乘线路
	public static final int MAPMODE_BUSEXCHG = 10;
	// 公交换乘线路(收藏夹)
		public static final int MAPMODE_FAVORITE_BUSEXCHG = 11;

	// 位置选择
	public static final int MAPMODE_POSSELECT = 13;

	// 位置跟踪
	public static final int MAPMODE_TRACE = 15;
	// 位置设置
	public static final int MAPMODE_LOCATIONSET = 16;


	// 弹出窗口消息
	// 周边搜索                                 
	public static final int FUNC_NEARBYSEARCH = 20;
	// 公交换乘
	public static final int FUNC_BUSEXCHANGE = 21;
	// 点击选择位置点
	public static final int FUNC_MAPPOINT = 22;

	// Search Mode
	// 搜索模式
	public static final int SEARCH_POI = 30;
	public static final int SEARCH_BUSSTATION = 31;
	public static final int SEARCH_BUSLINE = 32;
	public static final int SEARCH_NEARBY = 33;
	
	public static final int STARTACTIVITY = 34;
	public static final int TOAST=35;
	// 结果排序方式
	public static final int ORDER_MODE_DEFAULT = 40;
	public static final int ORDER_MODE_EXCHANGE = 41;
	public static final int ORDER_MODE_WALK = 42;
	public static final int ORDER_MODE_TOLL = 43;
	public static final int ORDER_MODE_USER_RECOMMEND = 44;
	
	//上下翻页
	public static final int PAGE_FLIP_DOME = 50;
	public static final int PAGE_FLIP_UP = 51;
	
	// 传输参数
	public static String EXTRA_BUSXRES = "busxres";
	public static String EXTRA_BUSROUTEUSERREC = "busrouteuserrec";
	public static String EXTRA_STARTPOI = "startpoi";
	public static String EXTRA_DESTPOI = "destpoi";
	public static String EXTRA_SEARCHMODE = "searchmode";
	public static String EXTRA_KEYWORD = "keyword";
	public static String EXTRA_POIRES = "poires";
	public static String EXTRA_BUSSTATIONRES = "busstationres";
	public static String EXTRA_BUSLINERES = "buslineres";
	public static String EXTRA_POIITEM = "poiitem";
	public static String EXTRA_BUSSTATION = "busstation";
	public static String EXTRA_BUSLINE = "busline";
	public static String EXTRA_NEARBYKINDITEMRES = "nearbykinditemres";
	public static String EXTRA_TOTAL = "total";
	public static String EXTRA_BUSROUTE = "busroute";
	public static String EXTRA_CENTERLATLON = "centerlatlon";
	public static String EXTRA_MAPPOINTFLAG = "mapptflag";
	public static String EXTRA_MAPPOINT = "mappoint";
	public static String EXTRA_APPURL = "appurl";
	public static String EXTRA_FILESIZE = "filesize";
	public static String EXTRA_SEARCHNEARBY = "around_type";
	
	public static String EXTRA_TIME = "time";
	public static String EXTRA_TIMETYPE = "timetype";
	public static String EXTRA_MTOTLE = "mTotle";
	public static String EXTRA_PAGENUM ="mPageNum";
	/** Intent的Key 标记*/
    public final static String PARAMS_SIGN = "params_sign";
	
	// 本地配置参数
	public static String CONFIG_USERNAME = "username";
	public static String CONFIG_PASSWORD = "password";

	
	// Handler 消息
	// 提示
	public static final int FUNC_ALERT = 500;
	public static final int FUNC_WAITDIALOG = 501;

	
	// 根据关键字进行搜索
	public static final int FUNC_POI_SEARCH = 520;
	// 搜索到结果自动填充
	public static final int FUNC_TEXT_AUTOCOMPLETE = 521;
	//客户端统计
	public static final int INGATHER = 530;
	public static final int INGATHER_EVENT = 531;
	
	// result code
	public static final int ERROR=1001;
	public static final int FIRST_LOCATION=1002;
	public static final int TOASTERROR=1003;
	public static final int FUNC_HIDDENWAITDIALOG = 1004;
	
	public static final int ROUTE_START_SEARCH=2000;
	public static final int ROUTE_END_SEARCH=2001;
	public static final int ROUTE_SEARCH_RESULT=2002;
	public static final int ROUTE_SEARCH_INIT=2003;
	public static final int ROUTE_SEARCH_ERROR=2004;
	public static final int ROUTE_SEARCH_DISABLE=2005;
	public static final int ROUTE_SEARCH_ENABLE=2006;
	

	public static final int GEOCODER_RESULT=3000;

	/** 点评用户推荐信息 Map<pathcomid,uid>*/
	public static Map<String,String> USER_COMMENT_LINE = null;
	
	/** 地铁图片站点坐标表 */
    public final static String CrailwayStopCoordinate = "CrailwayStopCoordinate";
    /** 收藏夹表 */
    public final static String TABLE_FAVORITES = "table_favorites";
    /** 地铁线路表 */
    public final static String CRailwayLine = "CRailwayLine";
    /** 地铁线路区间表 */
    public final static String CRailwaySectionLine = "CRailwaySectionLine";
    /** 地铁线路P表 */
    public final static String CRailwayLineP = "CRailwayLineP";
    /** 地铁站点表 */
    public final static String CRailwayStop = "CRailwayStop";
   /** */
    public final static String CrailwayStopExitInfo = "CrailwayStopExitInfo";
    /** 站点内部平面图表*/
    public final static String SRStopPic = "SRStopPic";
    /**城市*/
    public final static String TABLE_CITY = "table_city";
    /**城市数据的版本*/
    public final static String TABLE_CITYVERSION = "table_cityVersion";
    /**省份数据**/
    public final static String TABLE_PROVINCE = "table_Province";
}
