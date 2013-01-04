package com.busx.protocol.ingather;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import cm.framework.protocol.BaseJSONRsponse;

import com.busx.entities.UserLoginInfo;

public class IngatherResponse extends BaseJSONRsponse implements Serializable
{
	private static final long serialVersionUID = 1L;

	public int mStatus ;
	public String mRes;
	public UserLoginInfo mUserLoginInfo = new UserLoginInfo();
	
	@Override
	protected boolean extractBody(JSONObject arg0)
	{
		try 
		{
			mStatus = arg0.getInt("status");
			mRes = arg0.getString("res");
			mUserLoginInfo.ParseUserLoginInfo(arg0);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return true;
	}


}
