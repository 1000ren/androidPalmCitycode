package com.busx.protocol.update;

import org.json.JSONException;
import org.json.JSONObject;

import cm.framework.protocol.BaseJSONRsponse;

import com.busx.entities.AppUpdateInfo;
import com.busx.entities.UserLoginInfo;

public class AppUpdateResponse extends BaseJSONRsponse 
{
	public AppUpdateInfo mAppUpdateInfo = null;
	public UserLoginInfo mUserLoginInfo = new UserLoginInfo();

	@Override
	protected boolean extractBody(JSONObject arg0)
	{
		try 
		{
			JSONObject dataJsonObject = arg0.getJSONObject("res");
			if ( dataJsonObject != null )
			{
				mAppUpdateInfo = new AppUpdateInfo();

				mAppUpdateInfo.needUpdate = dataJsonObject.getInt("needupdate");
				if ( mAppUpdateInfo.needUpdate == 1 )
				{
					mAppUpdateInfo.enforce = dataJsonObject.getInt("enforce");
					mAppUpdateInfo.proName = dataJsonObject.getString("proname");
					mAppUpdateInfo.proVer = dataJsonObject.getString("prover");
					mAppUpdateInfo.desc = dataJsonObject.getString("desc");
					mAppUpdateInfo.downloadUrl = dataJsonObject.getString("downloadurl");
					mAppUpdateInfo.fileSize = dataJsonObject.getInt("filesize");
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
