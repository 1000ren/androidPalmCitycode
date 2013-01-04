package com.busx.protocol.path;

import java.util.List;

import org.apache.http.NameValuePair;

import android.util.Log;

import com.busx.protocol.ProtocolDef;

import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

public class GetPathListRequest extends BaseHttpRequest 
{

	public GetPathListRequest(String sid, String startlatlon, String destlatlon, String timetype, String time, String admincode)
	{
		setMethod(GET);
		String urlString;
		urlString = ProtocolDef.ABSOLUTEURI + ProtocolDef.METHOD_BUSPATH_QUERY + 
				"&startlatlon="+ startlatlon + "&endlatlon=" + destlatlon;
		urlString += "&timetype=" + timetype;
		urlString += "&time=" + time;
		urlString += "&sid=" + sid;
		urlString += "&admincode=" + admincode;
		Log.e( "BUSX", urlString );
		setAbsoluteURI( urlString );
	}
	
	@Override
	public BaseHttpResponse createResponse()
	{
		return new GetPathListResponse();
	}

	@Override
	public List<NameValuePair> getPostParams() 
	{
		return null;
	}

}
