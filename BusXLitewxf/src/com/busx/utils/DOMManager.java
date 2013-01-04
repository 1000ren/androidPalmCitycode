package com.busx.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.res.AssetManager;
import android.util.Log;

import com.busx.entities.NearbyKind;
import com.busx.entities.NearbyKindItem;
import com.busx.entities.WeatherData;
import com.busx.protocol.ProtocolDef;

public class DOMManager {
	
	private String weatherdataPath=Utils.getPath(ProtocolDef.SDPACKAGEWEATHER, ProtocolDef.INPACKAGEWEATHER);

	public List<NearbyKind> getNearby_kinds(AssetManager assetManager)throws Exception 
	{
		List<NearbyKind> nearby_kinds = new ArrayList<NearbyKind>();
		InputStream inputStream = null;
		try 
		{
			inputStream=assetManager.open("nearby_kind_item.xml");
			Element element = getElementByInputStream(inputStream);
			NodeList perosnNodes = element.getElementsByTagName("nearbykind");
			for (int i = 0; i < perosnNodes.getLength(); i++) 
			{
				Element kindElement = (Element) perosnNodes.item(i); 
				NearbyKind nearby_kind = new NearbyKind(); 
				nearby_kind.key=kindElement.getAttribute("key");
				nearby_kind.name=kindElement.getAttribute("name");
				NodeList kinditemElement = kindElement.getChildNodes(); 
				for(int y = 0 ; y < kinditemElement.getLength() ; y++)
				{
					if(kinditemElement.item(y).getNodeType()==Node.ELEMENT_NODE)
					{
						Element item=(Element)kinditemElement.item(y);
						NearbyKindItem nearby_kinditem=new NearbyKindItem();
						nearby_kinditem.name = item.getFirstChild().getNodeValue(); 
						nearby_kinditem.id = item.getAttribute("id");
						nearby_kind.nearbykinditems.add(nearby_kinditem);
					} 
				} 
				nearby_kinds.add(nearby_kind); 
			}
		} 
		catch (Exception e) 
		{
			Log.d(" getNearby_kinds ", e.getMessage());
			return nearby_kinds;
		}
		return nearby_kinds;
	}
	
	//获取天气数据
	public WeatherData getWeatherData (String cityCode)throws Exception 
	{
		weatherdataPath+="/";
		WeatherData weatherData=new WeatherData();
		InputStream inputStream = null;
		try 
		{	
			if (cityCode.equals("110000")) 
			{
				cityCode = "110100";
			}
			weatherData.id = "-1" ;
			String newZipFile = "weatherTraffic"+cityCode+Utils.getNowYearMonthAndDay()+".zip";
			File zipFile = new File(weatherdataPath+newZipFile);
			if (!zipFile.exists()) 
			{
				//下载zip文件到SD卡里
				int longth=downloadWeatherZip(ProtocolDef.WEATHER_URL+cityCode, newZipFile);
				if (longth!=1) 
				{
					weatherData.id = longth+"";
					return weatherData;
				}
				//解压 zip  文件
				Untilzip(weatherdataPath+newZipFile, weatherdataPath);
			}
			//读取解压后的xml文件 
			File weatherFile = new File(weatherdataPath+"weatherTraffic.xml");
			inputStream = new FileInputStream(weatherFile);
			Element element = getElementByInputStream(inputStream);
			NodeList perosnNodes = element.getElementsByTagName("weather");
			for (int i=0;i<perosnNodes.getLength();i++) 
			{
				Node weatherElement = (Node) perosnNodes.item(i);  
				NodeList l_att = weatherElement.getChildNodes();
				for (int k=0;k<l_att.getLength();k++) 
				{
					Node node =(Node) l_att.item(k);  
					String nodeName = node.getNodeName();
					
					String nodeValue = (null==node.getFirstChild()) ?"":node.getFirstChild().getNodeValue();
					
					if ("id".equals(nodeName)) 
						weatherData.id = nodeValue;
					else if ("air_cure".equals(nodeName)) 
						weatherData.air_cure = nodeValue;
					else if ("car_wash".equals(nodeName)) 
						weatherData.car_wash = nodeValue;
					else if ("city_name".equals(nodeName)) 
						weatherData.city_name = nodeValue;
					else if ("cold".equals(nodeName)) 
						weatherData.cold = nodeValue;
					else if ("comfort".equals(nodeName)) 
						weatherData.comfort = nodeValue;
					else if ("direction".equals(nodeName)) 
						weatherData.direction = nodeValue;
					else if ("dress".equals(nodeName)) 
						weatherData.dress = nodeValue;
					else if ("emperature".equals(nodeName)) 
						weatherData.emperature = nodeValue;
					else if ("exercise".equals(nodeName)) 
						weatherData.exercise = nodeValue;
					else if ("first_direction".equals(nodeName)) 
						weatherData.first_direction = nodeValue;
					else if ("first_temperature".equals(nodeName)) 
						weatherData.first_temperature = nodeValue;
					else if ("first_weather".equals(nodeName)) 
						weatherData.first_weather = nodeValue;
					else if ("first_weather_img".equals(nodeName)) 
						weatherData.first_weather_img = nodeValue;
					else if ("humidity".equals(nodeName)) 
						weatherData.humidity = nodeValue;
					else if ("journey".equals(nodeName)) 
						weatherData.journey = nodeValue;
					else if ("pressure".equals(nodeName)) 
						weatherData.pressure = nodeValue;
					else if ("quality".equals(nodeName)) 
						weatherData.quality = nodeValue;
					else if ("second_direction".equals(nodeName)) 
						weatherData.second_direction = nodeValue;
					else if ("second_temperature".equals(nodeName)) 
						weatherData.second_temperature = nodeValue;
					else if ("second_weather".equals(nodeName)) 
						weatherData.second_weather = nodeValue;
					else if ("second_weather_img".equals(nodeName)) 
						weatherData.second_weather_img = nodeValue;
					else if ("third_direction".equals(nodeName)) 
						weatherData.third_direction = nodeValue;
					else if ("third_temperature".equals(nodeName)) 
						weatherData.third_temperature = nodeValue;
					else if ("third_weather".equals(nodeName)) 
						weatherData.third_weather = nodeValue;
					else if ("third_weather_img".equals(nodeName)) 
						weatherData.third_weather_img = nodeValue;
					else if ("traffic_case".equals(nodeName)) 
						weatherData.traffic_case = nodeValue;
					else if ("ultraviol".equals(nodeName)) 
						weatherData.ultraviol = nodeValue;
					else if ("weather_date".equals(nodeName)) 
						weatherData.weather_date = nodeValue;
				}
				
			}
			 
		} 
		catch (Exception e) 
		{
			Log.d("getWeatherData", e.getMessage());
			return weatherData;
		}
		
		return weatherData;
	}

	public int downloadWeatherZip(String urlStr,String newFileName)
	{

		File file = null;
		OutputStream output = null;
		InputStream input = null;
		try
		{
			//获取输入流
			URL url= new URL(urlStr);
			input = url.openStream();
			//创建文件夹
			File dir = new File(weatherdataPath);
			if (!dir.exists()) 
			{
				dir.mkdirs();
			}
			else 
			{
				File[] files =dir.listFiles();
				for (File f : files) 
				{
					f.delete();
				}
			}
			//创建空的文件
			file = new File(weatherdataPath + newFileName);
			file.createNewFile();
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			while ((input.read(buffer)) != -1)
			{
				output.write(buffer);
			}
			output.flush();
			if (file.length()>0) 
			{
				return 1;
			}
			else
			{
				return -1;
			}	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				output.close();
				input.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return -2;
	
	}
	
	public Element getElementByInputStream(InputStream inputStream) throws Exception
	{
		Element element = null;
		try 
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			element = document.getDocumentElement();
		} 
		catch (Exception e) 
		{
			Log.d(" busx ", e.getMessage());
		}
		finally
    	{
    		if (null!=inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    	}
		return element;
	}
	private static void Untilzip(String zipFile, String targetDir) 
	{
		int BUFFER = 4096; //这里缓冲区我们使用4KB，
	    String strEntry; //保存每个zip的条目名称

	    try 
	    {
		    BufferedOutputStream dest = null; //缓冲输出流
		    FileInputStream fis = new FileInputStream(zipFile);
		    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		    ZipEntry entry; //每个zip条目的实例

		    while ((entry = zis.getNextEntry()) != null) 
		    {
			     try 
			     {
				      int count;
				      byte data[] = new byte[BUFFER];
				      strEntry = entry.getName();
		
				      File entryFile = new File(targetDir + strEntry);
				      File entryDir = new File(entryFile.getParent());
				      if (!entryDir.exists()) 
				      {
				       entryDir.mkdirs();
				      }
		
				      FileOutputStream fos = new FileOutputStream(entryFile);
				      dest = new BufferedOutputStream(fos, BUFFER);
				      while ((count = zis.read(data, 0, BUFFER)) != -1) 
				      {
				    	  dest.write(data, 0, count);
				      }
				      dest.flush();
				      dest.close();
			     } 
			     catch (Exception ex) 
			     {
			    	 ex.printStackTrace();
			     }
		    }
		    zis.close();
	   } 
	   catch (Exception cwj) 
	   {
		    cwj.printStackTrace();
	   }
  }
}
