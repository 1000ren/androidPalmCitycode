package com.busx.protocol;

public class ProtocolDef 
{
	// page size
	public static final int PAGE_SIZE = 10;

	// 统一服务接口
	public static final String ABSOLUTEURI = "http://58.83.237.103:8000/bussvc/index.php?";

	// 登录接口
	public static final String METHOD_LOGIN = "login";

	// 更新接口
	public static final String METHOD_APPUPDATE = "appupdate";

	// POI查询接口
	public static final String METHOD_POI_QUERY = "getpoint";

	// POI查询接口(汉字)
	public static final String METHOD_POI_ALL_QUERY = "getpointall";
	
	// POI查询接口(拼音)
	public static final String METHOD_POI_ALLPP_QUERY = "getpointallpp";
	
	// POITIP查询接口(汉字)
	public static final String METHOD_POI_TIP_ALL_QUERY = "tippoint";
	
	// POITIP查询接口(拼音)
	public static final String METHOD_POI_TIP_ALLPP_QUERY = "tippinpoint";
	
	//导航模式返回经过的站点名称
  	public static final String METHOD_NAVI_STATIONS = "getpassStops";
	
	// POI查询接口(ID)
	public static final String METHOD_POI_BYID_QUERY = "getpoibyid";

//	// 换乘路线查询接口
//	public static final String METHOD_BUSPATH_QUERY = "getpath.php";
	// 换乘路线查询接口 - 新
	public static final String METHOD_BUSPATH_QUERY = "getpathdetail";
	// 换乘线路 - 形状点
	public static final String METHOD_BUSPATH_GUIDE_QUERY = "getpathshape";
	// 公交站点查询接口
	public static final String METHOD_BUSSTATION_QUERY = "getstop";

	// 公交线路查询接口
	public static final String METHOD_BUSLINE_QUERY = "getlinelist";
	
	// 根据ID查询公交线路接口
	public static final String METHOD_BUSLINE_DETAIL_QUERY = "getlinedetail";

	// 根据线路Id获取所有站点接口
	public static final String METHOD_BUSSTOP_QUERY = "getlinedetail";
		
	// 查询周边类型接口
	public static final String METHOD_AROUNDTYPE_QUERY = "getsrdcatlist";

	// 查询周边信息接口
	public static final String METHOD_AROUNDPOI_QUERY = "getsrdlistbycat";
	
	// 客户端统计接口
	public static final String METHOD_INGATHER_QUERY = "reportcltinfo";
	
	// 推荐线路接口
	public static final String METHOD_BUSPATH_RECOMMEND ="pathcommend";
	
	// 获得推荐线路接口
	public static final String METHOD_BUSPATH_GET_RECOMMEND = "getpathcomlist";
	
	//用户评价(赞或不赞)接口
	public static final String METHOD_BUSPATH_USER_COMMENT = "evaluatecompath";
	//出行信息
	public static final String METHOD_TRAFFICEVENT = "TrafficEvent";
	
	// 默认的用户名，密码
	public static final String defGuestName = "guest";
	public static final String defGuestPassword = "guest";

	
	/** SD卡图片存放路径*/
	public static final String SDPACKAGEIMAGE = "/sdcard/busx/image/";
	
    /** 机身内存图片存放路径*/
    public static final String INPACKAGEIMAGE = "/data/data/com.busx/image/";
    
    /** SD卡天气预报信息存放路径*/
	public static final String SDPACKAGEWEATHER = "/sdcard/busx/weather/";
	
    /** 机身天气预报信息存放路径*/
    public static final String INPACKAGEWEATHER = "/data/data/com.busx/weather/";
    
    /** 图片下载功能的服务器查询地址*/
    public static final String SERVER_URL = "http://tns.palmcity.cn/mappic/getimage.php";
    
    /** 图片下载功能的图片下载地址*/
    public final static String DOWNPIC_URL = "http://tns.palmcity.cn/mappic/image/";
	
    //查询天气
    public final static String WEATHER_URL = "http://cloud.palmcity.cn/sp/zweather/getWeather.service?api_key=f3773906593e1aaab7bf07f59c242207&format=xml&zip=yes&cityCode=";
    //尾号限行
    public final static String WEATHER_CARNUM = "http://newtsp.palmcity.cn/CarnoLimit?imsi=358574043835364&partnerCode=12E8E2BC-5E26-419f-9B7C-D034F6F5EA79&cityCode=110000&zip=no&returnType=xml";
    //出行信息
    public final static String TRANSFERINFO = "http://124.205.53.122:8088/tsp_publish_http/EventList?imsi=55555&partnerCode=FEBF69ED-45C1-4799-AD5C-8E6C5B2652EF&username=111111&password=22222&cityCode=110000&zip=no&by=startdate&asc=desc";
    //出行信息详细
    public final static String TRANSFERINFO_DETAIL = "http://124.205.53.122:8088/tsp_publish_http/EventInfo?imsi=55555&partnerCode=FEBF69ED-45C1-4799-AD5C-8E6C5B2652EF&username=111111&password=22222&cityCode=110000&zip=no&eventId=";
    //出行提示
    public final static String TRAVELTIPS = "http://tns.palmcity.cn:8080/palmgo/NaviSv?type=guide&version=1.0";
    //获取城市更新信息
    public final static String METHOD_CITYLIST = "http://58.83.237.103/citylist/index.php?";
    //根据经纬度获取城市信息
    public final static String METHDO_ADMINCODE = "getadmincode";
}
