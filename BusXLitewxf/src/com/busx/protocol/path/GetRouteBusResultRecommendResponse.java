package com.busx.protocol.path;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.busx.entities.UserLoginInfo;

import cm.framework.protocol.BaseJSONRsponse;

public class GetRouteBusResultRecommendResponse extends BaseJSONRsponse
		implements Serializable {
	private static final long serialVersionUID = 1L;
	public UserLoginInfo mUserLoginInfo = new UserLoginInfo();
	public int status = 0;
	

	@Override
	protected boolean extractBody(JSONObject arg0) {
		try
		{
			this.status = arg0.getInt("status");
			mUserLoginInfo.ParseUserLoginInfo(arg0);
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		return true;
	}
	


}
