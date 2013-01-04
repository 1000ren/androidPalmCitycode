package com.busx.protocol.path;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import cm.framework.protocol.BaseJSONRsponse;

public class GetRouteBusResultUserCommentResponse extends BaseJSONRsponse
implements Serializable
{
	private static final long serialVersionUID = 1L;
	public int status;

	@Override
	protected boolean extractBody(JSONObject arg0) {
		// TODO Auto-generated method stub
		try {
			status = arg0.getInt("status");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
