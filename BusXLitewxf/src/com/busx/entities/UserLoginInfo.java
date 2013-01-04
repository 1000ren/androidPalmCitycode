package com.busx.entities;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class UserLoginInfo implements Serializable
{
	private static final long serialVersionUID = 100L;

	public String sid = null;
	public String uid = null;
	public String userName = null;
	public String password = null;
	public String nickName = null;
	public String email = null;

	public UserLoginInfo()
	{
		sid = "123456";
		uid = "123456";
		userName = "guest";
		password = "guest";
		nickName = "guest";
		email = "guest@ctfo.com";
	}

	public void copySID( UserLoginInfo userLoginInfo )
	{
		if ( userLoginInfo != null )
		{
			// 仅拷贝SID
			sid = userLoginInfo.sid;
		}
	}

	public void copy( UserLoginInfo userLoginInfo )
	{
		if ( userLoginInfo != null )
		{
			sid = userLoginInfo.sid;
			uid = userLoginInfo.uid;
			userName = userLoginInfo.userName;
			password = userLoginInfo.password;
			nickName = userLoginInfo.nickName;
			email = userLoginInfo.email;
		}
	}

	public void ParseUserLoginInfo( JSONObject jsonObject )
	{
		try 
		{
			JSONObject userDetailJsonObject = jsonObject.getJSONObject("userdetail");
			if ( userDetailJsonObject != null )
			{
				if ( userDetailJsonObject.has("sid") )
				{
					sid = userDetailJsonObject.getString("sid");
				}
				if ( userDetailJsonObject.has("uid") )
				{
					uid = userDetailJsonObject.getString("uid");
				}
				if ( userDetailJsonObject.has("username") )
				{
					userName = userDetailJsonObject.getString("username");
				}
				if ( userDetailJsonObject.has("nickname") )
				{
					nickName = userDetailJsonObject.getString("nickname");
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
}
