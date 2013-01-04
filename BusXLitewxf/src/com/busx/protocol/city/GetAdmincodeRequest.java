package com.busx.protocol.city;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

import com.busx.entities.Param;
import com.busx.protocol.ProtocolDef;

public class GetAdmincodeRequest extends BaseHttpRequest 
{
	private List<Param> params = new ArrayList<Param>();
	public GetAdmincodeRequest(String lon,String lat,String sid)
	{
		setMethod(POST);
		setAbsoluteURI(ProtocolDef.ABSOLUTEURI+ProtocolDef.METHDO_ADMINCODE);
		params.add(new Param("lon", lon));
		params.add(new Param("lat", lat));
		params.add(new Param("sid", sid));
		Log.e( "BUSX", ProtocolDef.ABSOLUTEURI+ProtocolDef.METHDO_ADMINCODE );
	}
	
	@Override
	public BaseHttpResponse createResponse()
	{
		return new GetAdmincodeResponse();
	}

	@Override
	public List<NameValuePair> getPostParams() 
	{
		List <NameValuePair> params = new ArrayList<NameValuePair>();
		for (Param param : this.params) 
		{
			params.add(new BasicNameValuePair(param.key, param.value));
		}
		return params;
	}
}
