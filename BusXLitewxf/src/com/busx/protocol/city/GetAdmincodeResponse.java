package com.busx.protocol.city;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

import cm.framework.protocol.BaseJSONRsponse;

import com.busx.entities.City;
import com.busx.entities.UserLoginInfo;

public class GetAdmincodeResponse extends BaseJSONRsponse implements Serializable
{
	private static final long serialVersionUID = 1L;
	public UserLoginInfo mUserLoginInfo = new UserLoginInfo();
	
	public City mLocationCity = null;
	@Override
	protected boolean extractBody(JSONObject arg0)
	{
		try 
		{
			JSONObject dataJsonObject = arg0.getJSONObject("res");
			
			if (null != dataJsonObject) {
				mLocationCity = new City();
			}
			mLocationCity.provincecode = dataJsonObject.getString("provcode");
			mLocationCity.admincode = dataJsonObject.getString("djscode");
			mLocationCity.adminname = dataJsonObject.getString("djsvdesc");
			getCityByCode();
			// parse user login info
			mUserLoginInfo.ParseUserLoginInfo(arg0);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return true;
	}


	public void getCityByCode()
	{
		//北京市
		if (mLocationCity.provincecode.startsWith("11")) 
		{
			mLocationCity.admincode = "110000";
			mLocationCity.adminname = "北京市";
		}
		//天津市
		else if (mLocationCity.provincecode.startsWith("12")) 
		{
			mLocationCity.admincode = "120000";
			mLocationCity.adminname = "天津市";
		}
		//上海市
		else if (mLocationCity.provincecode.startsWith("31")) 
		{
			mLocationCity.admincode = "310000";
			mLocationCity.adminname = "上海市";
		}
		//重庆市
		else if (mLocationCity.provincecode.startsWith("50")) 
		{
			mLocationCity.admincode = "500000";
			mLocationCity.adminname = "重庆市";
		}
		else 
		{
			mLocationCity.admincode += "00";
		}
	}
}
