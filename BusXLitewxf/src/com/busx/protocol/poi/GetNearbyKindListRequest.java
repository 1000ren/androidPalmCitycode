package com.busx.protocol.poi;

import java.util.List;

import org.apache.http.NameValuePair;

import android.util.Log;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

import com.busx.protocol.ProtocolDef;

public class GetNearbyKindListRequest extends BaseHttpRequest 
{
	public GetNearbyKindListRequest(String sid, String latlon, String dis)
	{
		dis=dis.substring(0, dis.length()-1);
 		dis=String.valueOf(Float.parseFloat(dis)/1000);

		setMethod(GET);
		String urlString;
		urlString = ProtocolDef.ABSOLUTEURI + ProtocolDef.METHOD_AROUNDTYPE_QUERY + 
				"&latlon="+ latlon + "&dis=" + dis;
		urlString += "&sid=" + sid;

		Log.d( "BUSX", urlString );
		setAbsoluteURI( urlString );
	}

	@Override
	public BaseHttpResponse createResponse()
	{
		return new GetNearbyKindListResponse();
	}

	@Override
	public List<NameValuePair> getPostParams() 
	{
		return null;
	}
}
