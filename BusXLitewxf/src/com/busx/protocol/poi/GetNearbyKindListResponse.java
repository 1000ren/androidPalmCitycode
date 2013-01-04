package com.busx.protocol.poi;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cm.framework.protocol.BaseJSONRsponse;

import com.busx.entities.NearbyKindItem;
import com.busx.entities.NearbyKindItemRes;
import com.busx.entities.UserLoginInfo;

public class GetNearbyKindListResponse extends BaseJSONRsponse implements Serializable
{
	private static final long serialVersionUID = 1L;

	public NearbyKindItemRes mNearbyKindItemRes = null;
	public int mTotal = 0;
	public UserLoginInfo mUserLoginInfo = new UserLoginInfo();

	@Override
	protected boolean extractBody(JSONObject arg0)
	{
		try 
		{
			JSONObject dataJsonObject = arg0.getJSONObject("res");
			mTotal = dataJsonObject.getInt("num");
			JSONArray listJsonArray = dataJsonObject.getJSONArray("catlist");
			if( mTotal > 0 && listJsonArray != null && listJsonArray.length() > 0)
			{
				mNearbyKindItemRes = new NearbyKindItemRes();
				mNearbyKindItemRes.mNearbyKindItemList = new ArrayList<NearbyKindItem>();
				for (int i=0,len=listJsonArray.length(); i<len; i++)
				{
					JSONObject kindItemJsonObject = listJsonArray.getJSONObject(i);
					NearbyKindItem nearby_kinditem = new NearbyKindItem ();
					nearby_kinditem.num = kindItemJsonObject.getInt("num");
					nearby_kinditem.id = kindItemJsonObject.getString("cat");
					mNearbyKindItemRes.mNearbyKindItemList.add(nearby_kinditem);
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
