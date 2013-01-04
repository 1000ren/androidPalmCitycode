package com.busx.protocol.path;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

import com.busx.entities.Param;
import com.busx.protocol.ProtocolDef;

public class GetPathGuideListRequest extends BaseHttpRequest 
{
	private List<Param> params = new ArrayList<Param>();

	public GetPathGuideListRequest(String sid, String startlatlon, String destlatlon, String memkey, String admincode)
	{
		setMethod(POST);
		String url=ProtocolDef.ABSOLUTEURI + ProtocolDef.METHOD_BUSPATH_GUIDE_QUERY;
		url+="&startlatlon="+startlatlon;
		url+="&sid="+sid;
		url+="&endlatlon="+destlatlon;
		url+="&admincode="+admincode;
		setAbsoluteURI(url);
		params.add(new Param("memkey", memkey));
	}
	
	@Override
	public BaseHttpResponse createResponse()
	{
		return new GetPathGuideListResponse();
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
