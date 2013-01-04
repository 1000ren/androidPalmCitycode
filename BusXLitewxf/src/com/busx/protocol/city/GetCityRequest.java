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

public class GetCityRequest extends BaseHttpRequest 
{
	private List<Param> params = new ArrayList<Param>();
	public GetCityRequest( String version, String pubdate,String servercode,String sid)
	{
		setMethod(POST);
		setAbsoluteURI(ProtocolDef.METHOD_CITYLIST);
		params.add(new Param("version", version));
		params.add(new Param("pubdate", pubdate));
		params.add(new Param("servercode", servercode));
		params.add(new Param("sid", sid));
		Log.e( "BUSX", ProtocolDef.METHOD_CITYLIST );
	}
	
	@Override
	public BaseHttpResponse createResponse()
	{
		return new GetCityResponse();
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
