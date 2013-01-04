package com.busx.protocol.path;


import java.util.List;

import org.apache.http.NameValuePair;

import android.util.Log;

import com.busx.protocol.ProtocolDef;

import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

public class GetRouteBusResultUserRecRequest extends BaseHttpRequest {

	String firstUrl = ProtocolDef.ABSOLUTEURI + ProtocolDef.METHOD_BUSPATH_GET_RECOMMEND;
	public GetRouteBusResultUserRecRequest(String sid, String startlatlon, String destlatlon)
	{
		setMethod(GET);
		String urlString = firstUrl+"&sid=" + sid + "&startlatlon=" + startlatlon +"&endlatlon=" +destlatlon;
		
		Log.e( "BUSX", urlString );
		setAbsoluteURI( urlString );
	}
	
	public GetRouteBusResultUserRecRequest(String sid, String recid)
	{
		setMethod(GET);
		String urlString = firstUrl+"&sid=" + sid + "&recid=" + recid ;
		
		Log.e( "BUSX", urlString );
		setAbsoluteURI( urlString );
	}
	
	@Override
	public BaseHttpResponse createResponse() 
	{
		
		return new GetRouteBusResultUserRecResponse();
	}
	
	@Override
	public List<NameValuePair> getPostParams() 
	{
		return null;
	}
}
