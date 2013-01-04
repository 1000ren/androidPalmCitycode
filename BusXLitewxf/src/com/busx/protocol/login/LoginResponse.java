package com.busx.protocol.login;

import org.json.JSONException;
import org.json.JSONObject;

import cm.framework.protocol.BaseJSONRsponse;

import com.busx.entities.UserLoginInfo;

public class LoginResponse extends BaseJSONRsponse 
{
	public UserLoginInfo mUserLoginInfo = new UserLoginInfo();

	@Override
	protected boolean extractBody(JSONObject arg0)
	{
		try 
		{
			JSONObject dataJsonObject = arg0.getJSONObject("res");
			if ( dataJsonObject != null )
			{
				mUserLoginInfo.sid = dataJsonObject.getString("phpsessid");
				mUserLoginInfo.uid = dataJsonObject.getString("id");
				mUserLoginInfo.userName = dataJsonObject.getString("username");
				mUserLoginInfo.password = dataJsonObject.getString("password");
				mUserLoginInfo.email = dataJsonObject.getString("email");
				mUserLoginInfo.nickName = dataJsonObject.getString("nickname");
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return true;
	}

}
