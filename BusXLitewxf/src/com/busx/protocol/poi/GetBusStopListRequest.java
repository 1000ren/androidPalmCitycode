package com.busx.protocol.poi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

import com.busx.entities.Param;
import com.busx.protocol.ProtocolDef;

public class GetBusStopListRequest extends BaseHttpRequest 
{
	private List<Param> params = new ArrayList<Param>();
	public GetBusStopListRequest( String sid, String lineid ,String admincode)
	{
		setMethod(POST);
		setAbsoluteURI(ProtocolDef.ABSOLUTEURI + ProtocolDef.METHOD_BUSSTOP_QUERY);
		params.add(new Param("lineid", lineid));
		params.add(new Param("admincode", admincode));
		params.add(new Param("sid", sid));
	}

	@Override
	public BaseHttpResponse createResponse()
	{
		return new GetBusStopListResponse();
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
