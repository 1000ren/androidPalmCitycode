package com.busx.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import android.graphics.Paint;
import android.os.Environment;



public class Utils 
{
	static String[] weekdays ={"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
	public static double computeAzimuth(double lat1, double lon1, double lat2,	double lon2)
	{
		double result = 0.0;

		int ilat1 = (int) (0.50 + lat1 * 360000.0);
		int ilat2 = (int) (0.50 + lat2 * 360000.0);
		int ilon1 = (int) (0.50 + lon1 * 360000.0);
		int ilon2 = (int) (0.50 + lon2 * 360000.0);

		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);
		lat2 = Math.toRadians(lat2);
		lon2 = Math.toRadians(lon2);

		if ((ilat1 == ilat2) && (ilon1 == ilon2)) {
			return result;
		} else if (ilon1 == ilon2) {
			if (ilat1 > ilat2)
				result = 180.0;
		} else {
			double c = Math
					.acos(Math.sin(lat2) * Math.sin(lat1) + Math.cos(lat2)
							* Math.cos(lat1) * Math.cos((lon2 - lon1)));
			double A = Math.asin(Math.cos(lat2) * Math.sin((lon2 - lon1))
					/ Math.sin(c));
			result = Math.toDegrees(A);
			if ((ilat2 > ilat1) && (ilon2 > ilon1)) {
			} else if ((ilat2 < ilat1) && (ilon2 < ilon1)) {
				result = 180.0 - result;
			} else if ((ilat2 < ilat1) && (ilon2 > ilon1)) {
				result = 180.0 - result;
			} else if ((ilat2 > ilat1) && (ilon2 < ilon1)) {
				result += 360.0;
			}
		}
		return result;
	}

	private static double AVG_ERAD = 6371.0;
	public static double computeDistance(double lat1, double lon1, double lat2, double lon2)
	{

		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);
		lat2 = Math.toRadians(lat2);
		lon2 = Math.toRadians(lon2);
		double d = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.cos(lon1 - lon2);
		return (AVG_ERAD * Math.acos(d));
	}
	
	/**
     *以固定分隔符分割后的数组List
     *@param sStr 需要分割的字符串
     *@param sSign 分隔符
     *
     * */
    public static String[] tokenize(String sStr,String sSign)
    {
        if(sStr != null && sSign != null && sSign.length() > 0)
        {
            Vector<String> ovStr = new Vector<String>();

            int wBegin = 0;
            int wEnd = 0;
            while((wEnd = sStr.indexOf(sSign, wBegin)) != -1)
            {
                ovStr.addElement(sStr.substring(wBegin,wEnd));
                wBegin = wEnd + sSign.length();
            }
            if(wBegin == 0)
            {
                String[] szOutput = new String[1];
                szOutput[0] = sStr;
                return szOutput;
            }
            else
            {
                ovStr.addElement(sStr.substring(wBegin));
                return vectorToArray(ovStr);
            }
        }
        return null;
    }
    
    /**
     * Vector的List转换为String[]
     * @param ovInput Vector的List
     * @return String[] 转换后的返回值
     * */
    public static String[] vectorToArray(Vector<String> ovInput)
    {
        if(ovInput != null)
        {
            String[] szOutput = new String[ovInput.size()];
            for(int i = 0;i< ovInput.size();++i)
            {
                szOutput[i] = ovInput.elementAt(i);
            }
            return szOutput;
        }
        return null;
    }

    /**
     * x.x分钟换为x分x秒
     * @param min 需要转换的分钟
     * @return 转换后的数据
     */
    public static String minToMinAndSec(String min)
    {
		String result;
		float minf = 0;
		if (min != null && !"".equals(min.trim())){
			minf = Float.parseFloat(min);
		}
		DecimalFormat df=new DecimalFormat("0");
		int second = Integer.parseInt((df.format(minf * 60)));
		int m = 0;
		int s = 0;

	    m = second/60;
	    if(second%60!=0)
	    {
	        s = second%60;
	        result = m+"分"+s+"秒";
	    }
	    else
	    {
	    	result = m+"分钟";
	    }

		return result;
	}
    
    /**
     * TextView文字不正常折行
     * @param text 需要转换的文字
     * @param width 折行的宽度
     * @param mTextPaint TextView.getPaint()
     * @return 转换后的数据
     */
	public static String wrapText(String text,float width,Paint mTextPaint){
		if(text!=null && width>0){
	        String temp = new String(text);
	        text = temp.replaceAll("\n", "");
			String result="";
			float maxWidth = mTextPaint.measureText(text);
			if(maxWidth>width){
				String tmpString="";
				float tmpLength=0;
				for(int i=0;i<text.length();i++){
					tmpString=text.substring(i,i+1);
					tmpLength+=mTextPaint.measureText(tmpString);
					if(tmpLength>=width){
						result+="\n";
						tmpLength=0;
						tmpLength+=mTextPaint.measureText(tmpString);
					}
					result+=tmpString;
				}
			}else{
				result=text;
			}

			return result;
		}else{
			return text;
		}
	}
	
	
	/**
	 * 返回 年月日时分秒
	 * @return
	 */
		public static String getNowTime()
		{
			Date date =new Date(); 
			SimpleDateFormat sp = new SimpleDateFormat("yyyyMMddHHmmss");
			return sp.format(date);
		}
		
		/**
		 * 返回 年月日
		 * @return
		 */
		public static String getNowYearMonthAndDay()
		{
			Date date =new Date(); 
			SimpleDateFormat sp = new SimpleDateFormat("yyyyMMdd");
			return sp.format(date);
		}
		
		/**
		 * 根据日期返回星期
		 * @param dateStr 输入的日期
		 * @return   星期字符串
		 */
		public static String ymdToWeek(String dateStr) 
		{
			SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			try 
			{
				Date date = sp.parse(dateStr);
				if(date == null) return "";
				calendar.setTime(date);
			} 
			catch (ParseException e) 
			{
			}
			int weekday = calendar.get(Calendar.DAY_OF_WEEK) - 1;
			return weekdays[weekday];
		}
		
		/**
		 * 根据日期返回星期
		 * @param dateStr 输入的日期
		 * @return   星期字符串
		 */
		public static String ymdToWeek(String dateStr,int num) 
		{
			
			SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			try 
			{
				Date date = sp.parse(dateStr);
				if(date == null) return "";
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_YEAR, num);
			} 
			catch (ParseException e) 
			{
			}
			int weekday = calendar.get(Calendar.DAY_OF_WEEK) - 1;
			return weekdays[weekday];
		}
		/**
		 * 获取年份中的日期
		 * @param dateStr 输入的日期
		 * @param num 0:表当天
		 *            正数：计算num天之后的日期
		 * 			     负数：计算num天之前的日期
		 * @return  日期字符串
		 */
		public static String getDateOfYear(String dateStr,int num){
			SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			try 
			{
				Date date = sp.parse(dateStr);
				if(date == null) return "";
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_YEAR, num);
				return sp.format(calendar.getTime());
			} 
			catch (ParseException e) 
			{
			}
			
			return "";
		}
		
		
		/**
	     * 获取图片的存储目录
	     * @param sdkpath sd卡路径
	     * @param inpath 手机内存路径
	     * @return 本地图片存储目录
	     */
	    public static String getPath(String sdpath,String inpath){
	        String path = null;
	        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	       
	        if(hasSDCard){
	            path=sdpath;
	        }else{
	            path=inpath;    
	        } 
	        File file = new File(path);
	        boolean isExist = file.exists();
	        if(!isExist){
	            file.mkdirs();
	        }
	        
	        return file.getPath();
	    }
	    
	    /**
	     * 转化为两位小数(四舍五入)
	     * @return
	     */
	    public static String formatDoubleNum(double dou)
	    {
			DecimalFormat df = new DecimalFormat("0.00");
			return df.format(dou);
	    }
}
