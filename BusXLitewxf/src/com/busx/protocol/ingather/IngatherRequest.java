package com.busx.protocol.ingather;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

import com.busx.entities.Param;
import com.busx.protocol.ProtocolDef;

public class IngatherRequest extends BaseHttpRequest 
{
	private List<Param> params = new ArrayList<Param>();

	public IngatherRequest( String sid, String clientinfo)
	{
		setMethod(POST);
		setAbsoluteURI(ProtocolDef.ABSOLUTEURI+ProtocolDef.METHOD_INGATHER_QUERY);
		params.add(new Param("clientinfo", clientinfo));
		params.add(new Param("sid", sid));
	}
	
	@Override
	public BaseHttpResponse createResponse()
	{
		return new IngatherResponse();
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
