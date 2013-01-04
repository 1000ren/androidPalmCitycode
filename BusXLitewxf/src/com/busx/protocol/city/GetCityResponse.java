package com.busx.protocol.city;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cm.framework.protocol.BaseJSONRsponse;

import com.busx.entities.City;
import com.busx.entities.Province;
import com.busx.entities.UserLoginInfo;

public class GetCityResponse extends BaseJSONRsponse implements Serializable
{
	private static final long serialVersionUID = 1L;
	public int mNeedupdate ;
	public String mVersion;
	public String mLastmodified;
	public List<Province> mProvinces = new ArrayList<Province>();
	public UserLoginInfo mUserLoginInfo = new UserLoginInfo();
	@Override
	protected boolean extractBody(JSONObject arg0)
	{
		try 
		{
			JSONObject dataJsonObject = arg0.getJSONObject("res");
			mNeedupdate = dataJsonObject.getInt("needupdate");
			
			if ( mNeedupdate == 1 )
			{
				mVersion = dataJsonObject.getString("version");
				mLastmodified = dataJsonObject.getString("lastmodified");
				JSONArray listJsonArray = dataJsonObject.getJSONArray("provincelist");
				for (int i=0,len=listJsonArray.length(); i<len; i++)
				{
					JSONObject provinceJsonObject = listJsonArray.getJSONObject(i);
					Province province = new Province();
					province.provincename = provinceJsonObject.getString("provincename");
					province.provincecode = provinceJsonObject.getString("provincecode");
					province.provincenamep = provinceJsonObject.getString("provincenamep");
					JSONArray cityArray = provinceJsonObject.getJSONArray("citylist");
					for (int j = 0; j < cityArray.length(); j++) 
					{
						JSONObject cityJsonObject = cityArray.getJSONObject(j);
						City city = new City();
						city.adminname = cityJsonObject.getString("cityname");
						city.admincode = cityJsonObject.getString("citycode");
						city.adminnamep = cityJsonObject.getString("citynamep");
						city.lonLats = cityJsonObject.getString("LonLats");
						city.isCenter = cityJsonObject.getString("IsCenter");
						city.provincecode = province.provincecode;
						
						province.cities.add(city);
					}
					mProvinces.add(province);
				}
			}
			
			// parse user login info
			mUserLoginInfo.ParseUserLoginInfo(arg0);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return true;
	}


}
